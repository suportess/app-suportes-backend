package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.produto.BloquearProdutoRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BloquearProdutoService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;

    public Map<String, Object> bloquear(String auth0Sub, BloquearProdutoRequest req) {
        var creds = resolveCreds(auth0Sub);

        Map<String, Object> body = Map.of(
                "cd_produto", req.getCdProduto(),
                "acao",       req.getAcao().toUpperCase()
        );

        return portalClient.bloquearProduto(creds.host(), creds.apikey(), body);
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
                    "A empresa ativa não possui portal configurado.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }
}
