package br.tec.suportes.backend.client;

import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.rag.*;
import br.tec.suportes.backend.exception.RagClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagClient {

    private final RestClient ragRestClient;

    // --- Consulta -------------------------------------------------------------

    public ConsultaRagResponse consultar(ConsultaRagRequest req) {
        return post("/api/consulta", req, new ParameterizedTypeReference<>() {});
    }

    // --- Indexação ------------------------------------------------------------

    public Map<String, Object> indexarTudo() {
        return postVoid("/api/indexacao", new ParameterizedTypeReference<>() {});
    }

    public Map<String, Object> indexarTabela(String nome) {
        return postVoid("/api/indexacao/" + nome, new ParameterizedTypeReference<>() {});
    }

    // --- Tabelas --------------------------------------------------------------

    public PagedResponse<TabelaRagResponse> listarTabelas(int pagina, int tamanhoPagina) {
        return get("/api/tabelas?pagina=" + pagina + "&tamanhoPagina=" + tamanhoPagina,
                new ParameterizedTypeReference<>() {});
    }

    public TabelaRagResponse salvarTabela(TabelaRagRequest req) {
        return post("/api/tabelas", req, new ParameterizedTypeReference<>() {});
    }

    public TabelaRagResponse buscarTabela(UUID id) {
        return get("/api/tabelas/" + id, new ParameterizedTypeReference<>() {});
    }

    public TabelaRagResponse buscarTabelaPorNome(String nome) {
        return get("/api/tabelas/nome/" + nome, new ParameterizedTypeReference<>() {});
    }

    public TabelaRagResponse atualizarDescricaoTabela(String nome, TabelaDescricaoRagRequest req) {
        return patch("/api/tabelas/" + nome + "/descricao", req, new ParameterizedTypeReference<>() {});
    }

    public void deletarTabela(UUID id) {
        delete("/api/tabelas/" + id);
    }

    // --- Relacionamentos ------------------------------------------------------

    public Map<String, Object> salvarRelacionamento(RelacionamentoRagRequest req) {
        return post("/api/relacionamentos", req, new ParameterizedTypeReference<>() {});
    }

    public void deletarRelacionamento(UUID id) {
        delete("/api/relacionamentos/" + id);
    }

    // --- Exemplos -------------------------------------------------------------

    public ExemploRagResponse salvarExemplo(ExemploRagRequest req) {
        return post("/api/exemplos", req, new ParameterizedTypeReference<>() {});
    }

    public List<ExemploRagResponse> listarExemplos() {
        return get("/api/exemplos", new ParameterizedTypeReference<>() {});
    }

    public void deletarExemplo(UUID id) {
        delete("/api/exemplos/" + id);
    }

    // --- Helpers HTTP ---------------------------------------------------------

    private <T> T get(String path, ParameterizedTypeReference<T> type) {
        try {
            log.debug("rag GET {}", path);
            return ragRestClient.get()
                    .uri(path)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar rag GET {}: {}", path, ex.getMessage());
            throw new RagClientException("Falha na comunicacao com o RAG: " + ex.getMessage(), ex);
        }
    }

    private <T> T post(String path, Object body, ParameterizedTypeReference<T> type) {
        try {
            log.debug("rag POST {}", path);
            return ragRestClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar rag POST {}: {}", path, ex.getMessage());
            throw new RagClientException("Falha na comunicacao com o RAG: " + ex.getMessage(), ex);
        }
    }

    private <T> T postVoid(String path, ParameterizedTypeReference<T> type) {
        try {
            log.debug("rag POST {}", path);
            return ragRestClient.post()
                    .uri(path)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar rag POST {}: {}", path, ex.getMessage());
            throw new RagClientException("Falha na comunicacao com o RAG: " + ex.getMessage(), ex);
        }
    }

    private <T> T patch(String path, Object body, ParameterizedTypeReference<T> type) {
        try {
            log.debug("rag PATCH {}", path);
            return ragRestClient.patch()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ao chamar rag PATCH {}: {}", path, ex.getMessage());
            throw new RagClientException("Falha na comunicacao com o RAG: " + ex.getMessage(), ex);
        }
    }

    private void delete(String path) {
        try {
            log.debug("rag DELETE {}", path);
            ragRestClient.delete()
                    .uri(path)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ao chamar rag DELETE {}: {}", path, ex.getMessage());
            throw new RagClientException("Falha na comunicacao com o RAG: " + ex.getMessage(), ex);
        }
    }
}
