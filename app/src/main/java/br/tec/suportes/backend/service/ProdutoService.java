package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.produto.ClasseDTO;
import br.tec.suportes.backend.dto.produto.ClassificacaoRequest;
import br.tec.suportes.backend.dto.produto.EmpresaProdutoDTO;
import br.tec.suportes.backend.dto.produto.EspecieDTO;
import br.tec.suportes.backend.dto.produto.ProdutoDTO;
import br.tec.suportes.backend.dto.produto.SubClasseDTO;
import br.tec.suportes.backend.dto.produto.UniProDTO;
import br.tec.suportes.backend.dto.produto.UnidadeDTO;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;

    public PagedResponse<ProdutoDTO> listar(String auth0Sub, String busca, int page, int pageSize) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarProdutos(c.host(), c.apikey(), busca, page, pageSize);
    }

    public ProdutoDTO buscarPorId(String auth0Sub, Long id) {
        var c = resolveCreds(auth0Sub);
        ProdutoDTO dto = portalClient.buscarProduto(c.host(), c.apikey(), id);
        if (dto == null) throw new RecursoNaoEncontradoException("Produto não encontrado: " + id);
        return dto;
    }

    public List<UniProDTO> listarUniPro(String auth0Sub, Long cdProduto) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarUniPro(c.host(), c.apikey(), cdProduto);
    }

    public List<EmpresaProdutoDTO> listarEmpresaProduto(String auth0Sub, Long cdProduto) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarEmpresaProduto(c.host(), c.apikey(), cdProduto);
    }

    public List<EspecieDTO> listarEspecies(String auth0Sub) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarEspecies(c.host(), c.apikey());
    }

    public List<UnidadeDTO> listarUnidades(String auth0Sub) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarUnidades(c.host(), c.apikey());
    }

    public List<ClasseDTO> listarClasses(String auth0Sub, Integer cdEspecie) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarClasses(c.host(), c.apikey(), cdEspecie);
    }

    public List<SubClasseDTO> listarSubClasses(String auth0Sub, Integer cdEspecie, Integer cdClasse) {
        var c = resolveCreds(auth0Sub);
        return portalClient.listarSubClasses(c.host(), c.apikey(), cdEspecie, cdClasse);
    }

    public void vincularClassificacao(String auth0Sub, Long cdProduto, ClassificacaoRequest req) {
        var c = resolveCreds(auth0Sub);
        portalClient.vincularClassificacao(c.host(), c.apikey(), cdProduto, req);
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
