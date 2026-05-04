package br.tec.suportes.backend.client;

import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.movimento.MovimentoDTO;
import br.tec.suportes.backend.dto.movimento.MovimentoRequest;
import br.tec.suportes.backend.dto.produto.ProdutoDTO;
import br.tec.suportes.backend.exception.PortalClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalClient {

    private final RestClient portalRestClient;

    // --- Movimentos -----------------------------------------------------------

    public PagedResponse<MovimentoDTO> listarMovimentos(
            String host, String apikey,
            Long cdEstoque,
            String tpMvtoEstoque,
            String dtMvtoEstoque,
            int page,
            int pageSize
    ) {
        var path = UriComponentsBuilder.fromPath("/movimento-listar")
                .queryParamIfPresent("cd_estoque",      java.util.Optional.ofNullable(cdEstoque))
                .queryParamIfPresent("tp_mvto_estoque", java.util.Optional.ofNullable(tpMvtoEstoque))
                .queryParamIfPresent("dt_mvto_estoque", java.util.Optional.ofNullable(dtMvtoEstoque))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        return get(host, apikey, path, new ParameterizedTypeReference<>() {});
    }

    public MovimentoDTO buscarMovimento(String host, String apikey, Long id) {
        return get(host, apikey, "/movimento-buscar-por-id?cd_mvto_estoque=" + id,
                new ParameterizedTypeReference<>() {});
    }

    public Map<?, ?> inserirMovimento(String host, String apikey, MovimentoRequest req) {
        return post(host, apikey, "/movimento-inserir", req, new ParameterizedTypeReference<>() {});
    }

    public Map<?, ?> concluirMovimento(String host, String apikey, Long id, String dtConclusao) {
        return post(host, apikey, "/movimento-concluir",
                Map.of("cd_mvto_estoque", id, "dt_conclusao", dtConclusao),
                new ParameterizedTypeReference<>() {});
    }

    public void excluirMovimento(String host, String apikey, Long id) {
        delete(host, apikey, "/movimento-excluir?cd_mvto_estoque=" + id);
    }

    // --- Produtos -------------------------------------------------------------

    public PagedResponse<ProdutoDTO> listarProdutos(String host, String apikey,
            String nmProduto, int page, int pageSize) {
        var path = UriComponentsBuilder.fromPath("/produto-listar")
                .queryParamIfPresent("nm_produto", java.util.Optional.ofNullable(nmProduto))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        return get(host, apikey, path, new ParameterizedTypeReference<>() {});
    }

    public ProdutoDTO buscarProduto(String host, String apikey, Long id) {
        return get(host, apikey, "/produto-buscar-por-id?cd_produto=" + id,
                new ParameterizedTypeReference<>() {});
    }

    // --- Status ---------------------------------------------------------------

    /**
     * Verifica o status do portal via GET /status (sem autenticação).
     * Retorna o body como Map (campos: status, uptime, databases).
     */
    public Map<String, Object> verificarStatus(String host) {
        try {
            log.debug("portal GET {}/status", host);
            return portalRestClient.get()
                    .uri(host + "/status")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientException ex) {
            log.error("Erro ao verificar status do portal {}: {}", host, ex.getMessage());
            throw new PortalClientException("Portal indisponivel: " + ex.getMessage(), ex);
        }
    }

    // --- Databases ------------------------------------------------------------

    /** Registra uma nova conexão de banco no portal. Key: sempre "oracle-prod". */
    public Map<?, ?> registrarDatabase(String host, String apikey, Map<String, Object> payload) {
        return post(host, apikey, "/databases", payload, new ParameterizedTypeReference<>() {});
    }

    /**
     * Remove a conexão de banco do portal.
     * Formato: DELETE /databases/0?key={key}
     */
    public void removerDatabase(String host, String apikey, String key) {
        delete(host, apikey, "/databases/0?key=" + key);
    }

    // --- Helpers HTTP ---------------------------------------------------------

    private <T> T get(String host, String apikey, String path, ParameterizedTypeReference<T> type) {
        try {
            log.debug("portal GET {}{}", host, path);
            return portalRestClient.get()
                    .uri(host + path)
                    .header("Authorization", "Bearer " + apikey)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal GET {}{}: {}", host, path, ex.getMessage());
            throw new PortalClientException("Falha na comunicacao com o portal: " + ex.getMessage(), ex);
        }
    }

    private <T> T post(String host, String apikey, String path, Object body, ParameterizedTypeReference<T> type) {
        try {
            log.debug("portal POST {}{}", host, path);
            return portalRestClient.post()
                    .uri(host + path)
                    .header("Authorization", "Bearer " + apikey)
                    .body(body)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal POST {}{}: {}", host, path, ex.getMessage());
            throw new PortalClientException("Falha na comunicacao com o portal: " + ex.getMessage(), ex);
        }
    }

    private void delete(String host, String apikey, String path) {
        try {
            log.debug("portal DELETE {}{}", host, path);
            portalRestClient.delete()
                    .uri(host + path)
                    .header("Authorization", "Bearer " + apikey)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal DELETE {}{}: {}", host, path, ex.getMessage());
            throw new PortalClientException("Falha na comunicacao com o portal: " + ex.getMessage(), ex);
        }
    }
}
