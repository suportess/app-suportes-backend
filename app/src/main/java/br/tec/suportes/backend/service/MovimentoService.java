package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.movimento.MovimentoDTO;
import br.tec.suportes.backend.dto.movimento.MovimentoRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimentoService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;

    public PagedResponse<MovimentoDTO> listar(
            String auth0Sub,
            Long cdEstoque,
            String tpMvtoEstoque,
            String dtMvtoEstoque,
            int page,
            int pageSize
    ) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarMovimentos(c.host(), c.apikey(), cdEstoque, tpMvtoEstoque, dtMvtoEstoque, page, pageSize);
    }

    public MovimentoDTO buscarPorId(String auth0Sub, Long id) {
        var c = resolveCreds(auth0Sub);
        MovimentoDTO dto = portalClient.buscarMovimento(c.host(), c.apikey(), id);
        if (dto == null) throw new RecursoNaoEncontradoException("Movimento não encontrado: " + id);
        return dto;
    }

    public Map<?, ?> inserir(String auth0Sub, MovimentoRequest req) {
        var c = resolveCreds(auth0Sub);
        return portalClient.inserirMovimento(c.host(), c.apikey(), req);
    }

    public Map<?, ?> concluir(String auth0Sub, Long id, String dtConclusao) {
        var c = resolveCreds(auth0Sub);
        return portalClient.concluirMovimento(c.host(), c.apikey(), id, dtConclusao);
    }

    public void excluir(String auth0Sub, Long id) {
        var c = resolveCreds(auth0Sub);
        portalClient.excluirMovimento(c.host(), c.apikey(), id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private record Creds(String host, String apikey) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa selecionada. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui portal configurado. Configure o domínio ngrok primeiro.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }
}
