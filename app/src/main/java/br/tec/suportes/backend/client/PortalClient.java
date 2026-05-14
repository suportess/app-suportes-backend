package br.tec.suportes.backend.client;

import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.movimento.MovimentoDTO;
import br.tec.suportes.backend.dto.movimento.MovimentoRequest;
import br.tec.suportes.backend.dto.produto.ClasseDTO;
import br.tec.suportes.backend.dto.produto.ClassificacaoRequest;
import br.tec.suportes.backend.dto.produto.EmpresaProdutoDTO;
import br.tec.suportes.backend.dto.produto.EspecieDTO;
import br.tec.suportes.backend.dto.produto.ProdutoDTO;
import br.tec.suportes.backend.dto.produto.SubClasseDTO;
import br.tec.suportes.backend.dto.produto.UniProDTO;
import br.tec.suportes.backend.dto.produto.UnidadeDTO;

import java.util.List;
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
            String busca, int page, int pageSize) {
        // Portal exige o parâmetro "busca" sempre presente (mesmo vazio) para resolver o bind ":busca"
        var path = UriComponentsBuilder.fromPath("/mv/api/produtos")
                .queryParam("busca", busca != null ? busca : "")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();
        return get(host, apikey, path, new ParameterizedTypeReference<>() {});
    }

    public ProdutoDTO buscarProduto(String host, String apikey, Long id) {
        return get(host, apikey, "/mv/api/produtos/" + id,
                new ParameterizedTypeReference<>() {});
    }

    public List<UniProDTO> listarUniPro(String host, String apikey, Long cdProduto) {
        return get(host, apikey, "/mv/api/produtos/" + cdProduto + "/unidades",
                new ParameterizedTypeReference<>() {});
    }

    public List<EmpresaProdutoDTO> listarEmpresaProduto(String host, String apikey, Long cdProduto) {
        return get(host, apikey, "/mv/api/produtos/" + cdProduto + "/empresas",
                new ParameterizedTypeReference<>() {});
    }

    public List<EspecieDTO> listarEspecies(String host, String apikey) {
        return get(host, apikey, "/mv/api/especies",
                new ParameterizedTypeReference<>() {});
    }

    public List<UnidadeDTO> listarUnidades(String host, String apikey) {
        return get(host, apikey, "/mv/api/unidades",
                new ParameterizedTypeReference<>() {});
    }

    public List<ClasseDTO> listarClasses(String host, String apikey, Integer cdEspecie) {
        return get(host, apikey, "/mv/api/classes?cd_especie=" + cdEspecie,
                new ParameterizedTypeReference<>() {});
    }

    public List<SubClasseDTO> listarSubClasses(String host, String apikey, Integer cdEspecie, Integer cdClasse) {
        return get(host, apikey, "/mv/api/sub-classes?cd_especie=" + cdEspecie + "&cd_classe=" + cdClasse,
                new ParameterizedTypeReference<>() {});
    }

    public void vincularClassificacao(String host, String apikey, Long cdProduto, ClassificacaoRequest req) {
        put(host, apikey, "/mv/api/produtos/" + cdProduto + "/classificacao",
                Map.of("cd_especie", req.getCdEspecie(), "cd_classe", req.getCdClasse(), "cd_sub_cla", req.getCdSubCla()));
    }

    public java.util.Map<String, Object> cadastrarProduto(String host, String apikey, java.util.Map<String, Object> body) {
        return post(host, apikey, "/mv/api/produtos", body,
                new ParameterizedTypeReference<java.util.Map<String, Object>>() {});
    }

    public Map<String, Object> cadastrarProFat(String host, String apikey, java.util.Map<String, Object> body) {
        return post(host, apikey, "/mv/api/pro-fat", body,
                new ParameterizedTypeReference<Map<String, Object>>() {});
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

    private void put(String host, String apikey, String path, Object body) {
        try {
            log.debug("portal PUT {}{}", host, path);
            portalRestClient.put()
                    .uri(host + path)
                    .header("Authorization", "Bearer " + apikey)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ao chamar portal PUT {}{}: {}", host, path, ex.getMessage());
            throw new PortalClientException("Falha na comunicacao com o portal: " + ex.getMessage(), ex);
        }
    }
}
