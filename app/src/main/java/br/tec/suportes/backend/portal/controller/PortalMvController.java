package br.tec.suportes.backend.portal.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.portal.dto.EntradaProdutoDTO;
import br.tec.suportes.backend.portal.dto.EstoqueDTO;
import br.tec.suportes.backend.portal.dto.MultiEmpresaDTO;
import br.tec.suportes.backend.portal.dto.ProdutoConsignadoDTO;
import br.tec.suportes.backend.portal.dto.ProdutoDetalheDTO;
import br.tec.suportes.backend.portal.dto.SaldoLoteDTO;
import br.tec.suportes.backend.portal.service.PortalMvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints que fazem proxy para a API MV do portal configurado em conf_empresa.
 *
 * Base: /portal/mv/empresas/{empresaId}
 *   GET /multiempresas                                   → lista multi-empresas
 *   GET /multiempresas/{cdMultiEmpresa}/estoques         → lista estoques com consignados
 *   GET /multiempresas/{cdMultiEmpresa}/estoques/{cdEstoque}/produtos → produtos consignados
 */
@RestController
@RequestMapping("/portal/mv/empresas/{empresaId}")
@RequiredArgsConstructor
public class PortalMvController {

    private final PortalMvService portalMvService;

    /** Lista todas as multi-empresas disponíveis no portal da empresa configurada. */
    @GetMapping("/multiempresas")
    public ResponseEntity<ApiResponse<List<MultiEmpresaDTO>>> listarMultiEmpresas(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId
    ) {
        var dados = portalMvService.listarMultiEmpresas(auth0Sub, empresaId);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    /** Lista os estoques com consignados de uma multi-empresa no portal. */
    @GetMapping("/multiempresas/{cdMultiEmpresa}/estoques")
    public ResponseEntity<ApiResponse<PagedResponse<EstoqueDTO>>> listarEstoques(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long cdMultiEmpresa,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        var dados = portalMvService.listarEstoques(auth0Sub, empresaId, cdMultiEmpresa, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    /** Lista os produtos consignados de um estoque no portal. */
    @GetMapping("/multiempresas/{cdMultiEmpresa}/estoques/{cdEstoque}/produtos")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoConsignadoDTO>>> listarProdutosConsignados(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long cdMultiEmpresa,
            @PathVariable Long cdEstoque,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        var dados = portalMvService.listarProdutosConsignados(
                auth0Sub, empresaId, cdMultiEmpresa, cdEstoque, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    /** Consulta o saldo em lote de um produto em um estoque. */
    @GetMapping("/estoques/{cdEstoque}/produtos/{cdProduto}/saldo-lote")
    public ResponseEntity<ApiResponse<SaldoLoteDTO>> buscarSaldoLote(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long cdEstoque,
            @PathVariable Long cdProduto
    ) {
        var dados = portalMvService.buscarSaldoLote(auth0Sub, empresaId, cdEstoque, cdProduto);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    /** Lista entradas não-consignadas de um produto em um estoque. */
    @GetMapping("/estoques/{cdEstoque}/produtos/{cdProduto}/entradas")
    public ResponseEntity<ApiResponse<PagedResponse<EntradaProdutoDTO>>> listarEntradas(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long cdEstoque,
            @PathVariable Long cdProduto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        var dados = portalMvService.listarEntradas(
                auth0Sub, empresaId, cdEstoque, cdProduto, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }

    /** Busca detalhes de um produto: flags de lote, validade e consignado. */
    @GetMapping("/produtos/{cdProduto}")
    public ResponseEntity<ApiResponse<ProdutoDetalheDTO>> buscarProdutoDetalhe(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long cdProduto
    ) {
        var dados = portalMvService.buscarProdutoDetalhe(auth0Sub, empresaId, cdProduto);
        return ResponseEntity.ok(ApiResponse.ok(dados));
    }
}
