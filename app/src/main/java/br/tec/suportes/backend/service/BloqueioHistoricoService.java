package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.BloqueioItem;
import br.tec.suportes.backend.domain.BloqueioLote;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.produto.BloqueioLoteDTO;
import br.tec.suportes.backend.dto.produto.RegistrarBloqueioLoteRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.BloqueioItemRepository;
import br.tec.suportes.backend.repository.BloqueioLoteRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BloqueioHistoricoService {

    private final BloqueioLoteRepository loteRepository;
    private final BloqueioItemRepository itemRepository;
    private final UsuarioRepository      usuarioRepository;

    /**
     * Salva o resultado de uma sessão de bloqueio/desbloqueio.
     * Cria o lote cabeçalho e persiste todos os itens em uma transação.
     */
    @Transactional
    public BloqueioLoteDTO registrar(String auth0Sub, RegistrarBloqueioLoteRequest req) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma empresa ativa selecionada."));

        List<RegistrarBloqueioLoteRequest.ItemBloqueio> itens =
                req.getItens() != null ? req.getItens() : List.of();

        int qtSucesso = (int) itens.stream().filter(i -> Boolean.TRUE.equals(i.getSnSucesso())).count();
        int qtErro    = itens.size() - qtSucesso;

        BloqueioLote lote = BloqueioLote.builder()
                .auth0Sub(auth0Sub)
                .nmUsuario(usuario.getNmUsuario())
                .emailUsuario(usuario.getEmail())
                .nmEmpresa(empresa.getNmEmpresa())
                .qtItens(itens.size())
                .qtSucesso(qtSucesso)
                .qtErro(qtErro)
                .build();
        lote = loteRepository.save(lote);

        for (RegistrarBloqueioLoteRequest.ItemBloqueio item : itens) {
            itemRepository.save(BloqueioItem.builder()
                    .lote(lote)
                    .cdProduto(item.getCdProduto())
                    .acao(item.getAcao() != null ? item.getAcao().toUpperCase() : "")
                    .snSucesso(Boolean.TRUE.equals(item.getSnSucesso()))
                    .dsErro(item.getDsErro())
                    .build());
        }

        return BloqueioLoteDTO.of(lote);
    }

    /** Lista os lotes do usuário, mais recentes primeiro. */
    public List<BloqueioLoteDTO> listar(String auth0Sub) {
        return loteRepository.findByAuth0SubOrderByDtBloqueioDesc(auth0Sub)
                .stream()
                .map(BloqueioLoteDTO::of)
                .toList();
    }

    /** Retorna itens de um lote, verificando que pertence ao usuário. */
    public List<BloqueioItem> listarItens(String auth0Sub, Long idLote) {
        BloqueioLote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote " + idLote + " não encontrado."));
        if (!lote.getAuth0Sub().equals(auth0Sub))
            throw new RecursoNaoEncontradoException("Lote " + idLote + " não encontrado.");
        return itemRepository.findByLoteIdOrderById(idLote);
    }
}
