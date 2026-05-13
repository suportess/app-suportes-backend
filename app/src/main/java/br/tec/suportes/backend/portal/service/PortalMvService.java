package br.tec.suportes.backend.portal.service;

import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.portal.client.PortalMvClient;
import br.tec.suportes.backend.portal.dto.EntradaProdutoDTO;
import br.tec.suportes.backend.portal.dto.FornecedorDTO;
import br.tec.suportes.backend.portal.dto.EstoqueDTO;
import br.tec.suportes.backend.portal.dto.MultiEmpresaDTO;
import br.tec.suportes.backend.portal.dto.ProdutoConsignadoDTO;
import br.tec.suportes.backend.portal.dto.ProdutoDetalheDTO;
import br.tec.suportes.backend.portal.dto.SaldoConsigFornDTO;
import br.tec.suportes.backend.portal.dto.SaldoLoteDTO;
import br.tec.suportes.backend.portal.dto.SaldoProdutoConsignadoDTO;
import br.tec.suportes.backend.repository.ConfEmpresaRepository;
import br.tec.suportes.backend.repository.UsuarioEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortalMvService {

    private final PortalMvClient portalMvClient;
    private final ConfEmpresaRepository confEmpresaRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    // ─── Validação de acesso ──────────────────────────────────────────────────

    /**
     * Valida que o usuário tem acesso à empresa e retorna host + apikey dela.
     */
    private String[] resolverCredenciais(String auth0Sub, Long empresaId) {
        usuarioEmpresaRepository
                .findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Empresa não encontrada para este usuário."));

        var empresa = confEmpresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank()) {
            throw new RecursoNaoEncontradoException(
                    "Host do portal não configurado para esta empresa. "
                    + "Acesse Configurações › Empresa e preencha o campo 'Host Portal'.");
        }

        return new String[]{ empresa.getDsHostPortal(), empresa.getApikey() };
    }

    // ─── Multi-empresas ───────────────────────────────────────────────────────

    public List<MultiEmpresaDTO> listarMultiEmpresas(String auth0Sub, Long empresaId) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarMultiEmpresas(creds[0], creds[1]);
    }

    // ─── Estoques ─────────────────────────────────────────────────────────────

    public PagedResponse<EstoqueDTO> listarEstoques(
            String auth0Sub, Long empresaId,
            Long cdMultiEmpresa, int page, int pageSize
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarEstoques(creds[0], creds[1], cdMultiEmpresa, page, pageSize);
    }

    // ─── Produtos consignados ─────────────────────────────────────────────────

    public PagedResponse<ProdutoConsignadoDTO> listarProdutosConsignados(
            String auth0Sub, Long empresaId,
            Long cdMultiEmpresa, Long cdEstoque, int page, int pageSize
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarProdutosConsignados(
                creds[0], creds[1], cdMultiEmpresa, cdEstoque, page, pageSize);
    }

    // ─── Saldo em lote ────────────────────────────────────────────────────────

    public SaldoLoteDTO buscarSaldoLote(
            String auth0Sub, Long empresaId,
            Long cdEstoque, Long cdProduto
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.buscarSaldoLote(creds[0], creds[1], cdEstoque, cdProduto);
    }

    // ─── Entradas por produto ─────────────────────────────────────────────────

    public PagedResponse<EntradaProdutoDTO> listarEntradas(
            String auth0Sub, Long empresaId,
            Long cdEstoque, Long cdProduto, int page, int pageSize
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarEntradas(creds[0], creds[1], cdEstoque, cdProduto, page, pageSize);
    }

    // ─── Detalhe do produto ───────────────────────────────────────────────────

    public ProdutoDetalheDTO buscarProdutoDetalhe(
            String auth0Sub, Long empresaId,
            Long cdProduto
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.buscarProdutoDetalhe(creds[0], creds[1], cdProduto);
    }

    // ─── Devolução consignada ─────────────────────────────────────────────────

    public void devolverProdutoConsignado(
            String auth0Sub, Long empresaId,
            Long cdEstoque,
            Long cdMultiEmpresa,
            Long cdEntPro,
            Long cdProdutoDev,
            String cdLoteDev,
            String dtValidadeDev,
            java.math.BigDecimal qtDevolvida,
            Long cdProdutoEnt,
            String cdLoteEnt,
            String dtValidadeEnt,
            java.math.BigDecimal qtEntrada,
            String dtDevolucao
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        var body = new java.util.LinkedHashMap<String, Object>();
        body.put("cd_multi_empresa", cdMultiEmpresa);
        body.put("cd_ent_pro",       cdEntPro);
        body.put("cd_produto_dev",   cdProdutoDev);
        body.put("cd_lote_dev",      cdLoteDev);
        body.put("dt_validade_dev",  dtValidadeDev);
        body.put("qt_devolvida",     qtDevolvida);
        body.put("cd_produto_ent",   cdProdutoEnt);
        body.put("cd_lote_ent",      cdLoteEnt);
        body.put("dt_validade_ent",  dtValidadeEnt);
        body.put("qt_entrada",       qtEntrada);
        body.put("dt_devolucao",     dtDevolucao);
        portalMvClient.devolverProdutoConsignado(creds[0], creds[1], cdEstoque, body);
    }

    // ─── Estoques todos consignados ──────────────────────────────────────────

    public List<EstoqueDTO> listarEstoquesTodosConsignados(
            String auth0Sub, Long empresaId,
            Long cdMultiEmpresa, String busca
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarEstoquesTodosConsignados(creds[0], creds[1], cdMultiEmpresa, busca);
    }

    // ─── Saldo consignado por fornecedor ────────────────────────────────────

    public List<SaldoConsigFornDTO> listarSaldoConsigForn(
            String auth0Sub, Long empresaId,
            Long cdEstoque, Long cdProduto
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarSaldoConsigForn(creds[0], creds[1], cdEstoque, cdProduto);
    }

    // ─── Fornecedores ──────────────────────────────────────────────────

    public List<FornecedorDTO> listarFornecedores(String auth0Sub, Long empresaId) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarFornecedores(creds[0], creds[1]);
    }

    // ─── Todos os produtos consignados ──────────────────────────────────────

    public List<ProdutoConsignadoDTO> listarTodosProdutosConsignados(
            String auth0Sub, Long empresaId
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarTodosProdutosConsignados(creds[0], creds[1]);
    }

    // ─── Saldo completo de consignados por estoque ───────────────────────────

    public List<SaldoProdutoConsignadoDTO> listarSaldoConsignados(
            String auth0Sub, Long empresaId,
            Long cdMultiEmpresa, Long cdEstoque, String busca
    ) {
        var creds = resolverCredenciais(auth0Sub, empresaId);
        return portalMvClient.listarSaldoConsignados(creds[0], creds[1], cdMultiEmpresa, cdEstoque, busca);
    }
}
