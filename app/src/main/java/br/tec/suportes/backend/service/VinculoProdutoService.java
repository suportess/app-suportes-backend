package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ItVinculoProduto;
import br.tec.suportes.backend.domain.VinculoProduto;
import br.tec.suportes.backend.dto.vinculo.VinculoProdutoDTO;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ItVinculoProdutoRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import br.tec.suportes.backend.repository.VinculoProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VinculoProdutoService {

    private final VinculoProdutoRepository   sessaoRepository;
    private final ItVinculoProdutoRepository  itemRepository;
    private final UsuarioRepository           usuarioRepository;

    /**
     * Persiste o cabeçalho e os itens de vínculo SUS gerados pela transferência Oracle.
     * Só deve ser chamado quando {@code oracleResult} contém "sus_vinculos" não vazio.
     *
     * @return ID da sessão criada em {@code vinculo_produto}
     */
    @Transactional
    public Long persistir(String auth0Sub, Map<String, Object> oracleResult) {

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vinculos =
                (List<Map<String, Object>>) oracleResult.get("sus_vinculos");

        if (vinculos == null || vinculos.isEmpty()) return null;

        // ── Lookup usuário / empresa ──────────────────────────────────────────
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        String nmEmpresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .map(e -> e.getNmEmpresa())
                .orElse(null);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        VinculoProduto sessao = sessaoRepository.save(VinculoProduto.builder()
                .auth0Sub(auth0Sub)
                .nmUsuario(usuario.getNmUsuario())
                .emailUsuario(usuario.getEmail())
                .nmEmpresa(nmEmpresa)
                .qtVinculos(vinculos.size())
                .build());

        // ── Itens ─────────────────────────────────────────────────────────────
        for (int i = 0; i < vinculos.size(); i++) {
            Map<String, Object> v = vinculos.get(i);
            itemRepository.save(ItVinculoProduto.builder()
                    .cdSessao(sessao.getId())
                    .nrLinha(i + 1)
                    .cdProdutoAntigo(str(v.get("cd_produto_antigo")))
                    .cdProdutoNovo(str(v.get("cd_produto_novo")))
                    .cdProcedimentoSus(str(v.get("cd_procedimento_sus")))
                    .build());
        }

        return sessao.getId();
    }

    /**
     * Persiste cabeçalho e itens de vínculo SUS recebidos diretamente
     * (sem precisar do wrapper "sus_vinculos" do oracleResult da transferência).
     *
     * @param vinculos lista de maps com: cd_produto_antigo, ds_produto_antigo, cd_produto_novo,
     *                 ds_produto_novo, cd_procedimento_sus
     * @return ID da sessão criada em {@code vinculo_produto}
     */
    @Transactional
    public Long persistirVinculos(String auth0Sub, List<Map<String, Object>> vinculos) {
        if (vinculos == null || vinculos.isEmpty()) return null;

        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        String nmEmpresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .map(e -> e.getNmEmpresa())
                .orElse(null);

        VinculoProduto sessao = sessaoRepository.save(VinculoProduto.builder()
                .auth0Sub(auth0Sub)
                .nmUsuario(usuario.getNmUsuario())
                .emailUsuario(usuario.getEmail())
                .nmEmpresa(nmEmpresa)
                .qtVinculos(vinculos.size())
                .build());

        for (int i = 0; i < vinculos.size(); i++) {
            Map<String, Object> v = vinculos.get(i);
            itemRepository.save(ItVinculoProduto.builder()
                    .cdSessao(sessao.getId())
                    .nrLinha(i + 1)
                    .cdProdutoAntigo(str(v.get("cd_produto_antigo")))
                    .dsProdutoAntigo(str(v.get("ds_produto_antigo")))
                    .cdProdutoNovo(str(v.get("cd_produto_novo")))
                    .dsProdutoNovo(str(v.get("ds_produto_novo")))
                    .cdProcedimentoSus(str(v.get("cd_procedimento_sus")))
                    .build());
        }

        return sessao.getId();
    }

    /** Lista as últimas 50 sessões de vínculo SUS (mais recentes primeiro). */
    public List<VinculoProdutoDTO> listarSessoes() {
        return sessaoRepository
                .findAllByOrderByDtVinculoDesc(org.springframework.data.domain.PageRequest.of(0, 50))
                .map(VinculoProdutoDTO::of)
                .toList();
    }

    private static String str(Object v) {
        return v != null ? v.toString() : null;
    }
}
