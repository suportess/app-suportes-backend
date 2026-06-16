package br.tec.suportes.backend.portal.client;

import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.exception.PortalClientException;
import br.tec.suportes.backend.portal.dto.EntradaProdutoDTO;
import br.tec.suportes.backend.portal.dto.FornecedorDTO;
import br.tec.suportes.backend.portal.dto.EstoqueDTO;
import br.tec.suportes.backend.portal.dto.MultiEmpresaDTO;
import br.tec.suportes.backend.portal.dto.ProdutoConsignadoDTO;
import br.tec.suportes.backend.portal.dto.ProdutoDetalheDTO;
import br.tec.suportes.backend.portal.dto.ProdutoDetalheResponseDTO;
import br.tec.suportes.backend.portal.dto.SaldoConsigFornDTO;
import br.tec.suportes.backend.portal.dto.SaldoLoteDTO;
import br.tec.suportes.backend.portal.dto.SaldoLoteResponseDTO;
import br.tec.suportes.backend.portal.dto.SaldoProdutoConsignadoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Client dinâmico para a API MV do portal.
 * O host base e a API key são fornecidos por chamada (vindos de conf_empresa),
 * portanto um RestClient é construído on-demand por empresa.
 */
@Slf4j
@Component
public class PortalMvClient {

    private static final int TIMEOUT_SECONDS = 30;

    // ─── Factory ──────────────────────────────────────────────────────────────

    private RestClient buildClient(String baseUrl, String apiKey) {
        int ms = (int) Duration.ofSeconds(TIMEOUT_SECONDS).toMillis();
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }

    // ─── API MV — Multi-empresas ──────────────────────────────────────────────

    /**
     * Lista todas as multi-empresas disponíveis no portal.
     * GET /mv/api/multiempresas
     */
    public List<MultiEmpresaDTO> listarMultiEmpresas(String hostPortal, String apiKey) {
        var client = buildClient(hostPortal, apiKey);
        return get(client, "/mv/api/multiempresas", new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista os estoques (com consignados) de uma empresa.
     * GET /mv/api/multiempresas/{cdEmpresa}/estoques?page=&pageSize=
     */
    public PagedResponse<EstoqueDTO> listarEstoques(
            String hostPortal, String apiKey,
            Long cdEmpresa, int page, int pageSize
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/multiempresas/" + cdEmpresa + "/estoques"
                + "?page=" + page + "&pageSize=" + pageSize;
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista os produtos consignados de um estoque de uma empresa.
     * GET /mv/api/multiempresas/{cdEmpresa}/estoques/{cdEstoque}/produtos?page=&pageSize=
     */
    public PagedResponse<ProdutoConsignadoDTO> listarProdutosConsignados(
            String hostPortal, String apiKey,
            Long cdEmpresa, Long cdEstoque, int page, int pageSize
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/multiempresas/" + cdEmpresa + "/estoques/" + cdEstoque + "/produtos"
                + "?page=" + page + "&pageSize=" + pageSize;
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }

    /**
     * Consulta o saldo em lote de um produto em um estoque.
     * GET /mv/api/estoques/{cdEstoque}/produtos/{cdProduto}/saldo-lote
     * O portal retorna: { "saldo": { "QT_ESTOQUE_ATUAL": "576", "DS_UNIDADE": "Unidade" } }
     */
    public SaldoLoteDTO buscarSaldoLote(
            String hostPortal, String apiKey,
            Long cdEstoque, Long cdProduto
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/estoques/" + cdEstoque + "/produtos/" + cdProduto + "/saldo-lote";
        SaldoLoteResponseDTO envelope = get(client, uri, new ParameterizedTypeReference<>() {});
        return envelope != null ? envelope.getSaldo() : null;
    }

    /**
     * Lista entradas de um produto não-consignadas em um estoque.
     * GET /mv/api/estoques/{cdEstoque}/produtos/{cdProduto}/entradas?page=&pageSize=
     */
    public PagedResponse<EntradaProdutoDTO> listarEntradas(
            String hostPortal, String apiKey,
            Long cdEstoque, Long cdProduto, int page, int pageSize
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/estoques/" + cdEstoque + "/produtos/" + cdProduto + "/entradas"
                + "?page=" + page + "&pageSize=" + pageSize;
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }
    /**
     * Busca detalhes de um produto: flags de lote, validade e consignado.
     * GET /mv/api/produtos/{cdProduto}
     */
    public ProdutoDetalheDTO buscarProdutoDetalhe(
            String hostPortal, String apiKey,
            Long cdProduto
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/produtos/" + cdProduto;
        ProdutoDetalheResponseDTO envelope = get(client, uri, new ParameterizedTypeReference<>() {});
        return envelope != null ? envelope.getProduto() : null;
    }
    /**
     * Registra a devolução consignada no portal.
     * POST /mv/api/estoques/{cdEstoque}/devolucoes
     */
    public void devolverProdutoConsignado(
            String hostPortal, String apiKey,
            Long cdEstoque,
            java.util.Map<String, Object> body
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/estoques/" + cdEstoque + "/devolucoes";
        try {
            log.debug("portal-mv POST {}", uri);
            client.post()
                    .uri(uri)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal-mv POST {}: {}", uri, ex.getMessage());
            throw new PortalClientException("Falha na comunicação com o portal MV: " + ex.getMessage(), ex);
        }
    }

    /**
     * Lista todos os estoques de uma multi-empresa que possuem consignados (sem filtro de saldo).
     * GET /mv/api/multiempresas/{cdEmpresa}/estoques/todos?busca=
     */
    public List<EstoqueDTO> listarEstoquesTodosConsignados(
            String hostPortal, String apiKey,
            Long cdEmpresa, String busca
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/multiempresas/" + cdEmpresa + "/estoques/todos"
                + "?busca=" + (busca != null ? busca : "");
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista todos os produtos consignados de um estoque com saldo atual e unidade.
     * GET /mv/api/multiempresas/{cdEmpresa}/estoques/{cdEstoque}/saldo-consignados?busca=
     */
    public List<SaldoProdutoConsignadoDTO> listarSaldoConsignados(
            String hostPortal, String apiKey,
            Long cdEmpresa, Long cdEstoque, String busca
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/multiempresas/" + cdEmpresa + "/estoques/" + cdEstoque + "/saldo-consignados"
                + "?busca=" + (busca != null ? busca : "");
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista saldo consignado por fornecedor de um produto em um estoque.
     * GET /mv/api/estoques/{cdEstoque}/produtos/{cdProduto}/saldo-consig-forn
     */
    public List<SaldoConsigFornDTO> listarSaldoConsigForn(
            String hostPortal, String apiKey,
            Long cdEstoque, Long cdProduto
    ) {
        var client = buildClient(hostPortal, apiKey);
        var uri = "/mv/api/estoques/" + cdEstoque + "/produtos/" + cdProduto + "/saldo-consig-forn";
        return get(client, uri, new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista todos os fornecedores.
     * GET /mv/api/fornecedores
     */
    public List<FornecedorDTO> listarFornecedores(String hostPortal, String apiKey) {
        var client = buildClient(hostPortal, apiKey);
        return get(client, "/mv/api/fornecedores", new ParameterizedTypeReference<>() {});
    }

    /**
     * Lista todos os produtos consignados cadastrados no sistema MV.
     * GET /mv/api/produtos-consignados
     */
    public List<ProdutoConsignadoDTO> listarTodosProdutosConsignados(
            String hostPortal, String apiKey
    ) {
        var client = buildClient(hostPortal, apiKey);
        return get(client, "/mv/api/produtos-consignados", new ParameterizedTypeReference<>() {});
    }

    /**
     * Executa um SELECT Oracle dinamicamente e retorna o resultado como JSON string.
     * POST /mv/api/consulta/generica
     */
    public Map<String, Object> consultaGenerica(String hostPortal, String apiKey, String sqlQuery) {
        var client = buildClient(hostPortal, apiKey);
        try {
            log.debug("portal-mv POST /mv/api/consulta/generica");
            return client.post()
                    .uri("/mv/api/consulta/generica")
                    .body(java.util.Map.of("sql_query", sqlQuery))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal-mv POST /mv/api/consulta/generica: {}", ex.getMessage());
            throw new PortalClientException("Falha na comunicação com o portal MV: " + ex.getMessage(), ex);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private <T> T get(RestClient client, String uri, ParameterizedTypeReference<T> type) {
        try {
            log.debug("portal-mv GET {}", uri);
            return client.get()
                    .uri(uri)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal-mv GET {}: {}", uri, ex.getMessage());
            throw new PortalClientException("Falha na comunicação com o portal MV: " + ex.getMessage(), ex);
        }
    }
}
