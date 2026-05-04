package br.tec.suportes.backend.client;

import br.tec.suportes.backend.dto.ngrok.credential.NgrokCredentialCreateRequest;
import br.tec.suportes.backend.dto.ngrok.credential.NgrokCredentialDTO;
import br.tec.suportes.backend.dto.ngrok.credential.NgrokCredentialListResponse;
import br.tec.suportes.backend.dto.ngrok.credential.NgrokCredentialUpdateRequest;
import br.tec.suportes.backend.dto.ngrok.endpoint.NgrokEndpointCreateRequest;
import br.tec.suportes.backend.dto.ngrok.endpoint.NgrokEndpointDTO;
import br.tec.suportes.backend.dto.ngrok.endpoint.NgrokEndpointListResponse;
import br.tec.suportes.backend.dto.ngrok.endpoint.NgrokEndpointUpdateRequest;
import br.tec.suportes.backend.dto.ngrok.reserveddomain.NgrokReservedDomainCreateRequest;
import br.tec.suportes.backend.dto.ngrok.reserveddomain.NgrokReservedDomainDTO;
import br.tec.suportes.backend.dto.ngrok.reserveddomain.NgrokReservedDomainListResponse;
import br.tec.suportes.backend.dto.ngrok.reserveddomain.NgrokReservedDomainUpdateRequest;
import br.tec.suportes.backend.dto.ngrok.tunnel.NgrokTunnelDTO;
import br.tec.suportes.backend.dto.ngrok.tunnel.NgrokTunnelListResponse;
import br.tec.suportes.backend.dto.ngrok.tunnelsession.NgrokTunnelSessionDTO;
import br.tec.suportes.backend.dto.ngrok.tunnelsession.NgrokTunnelSessionListResponse;
import br.tec.suportes.backend.exception.NgrokClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class NgrokClient {

    @Qualifier("ngrokRestClient")
    private final RestClient ngrokRestClient;

    // ─── Endpoints ───────────────────────────────────────────────────────────

    public NgrokEndpointListResponse listarEndpoints(String beforeId, String limit) {
        var uri = UriComponentsBuilder.fromPath("/endpoints")
                .queryParamIfPresent("before_id", java.util.Optional.ofNullable(beforeId))
                .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                .build()
                .toUriString();
        return get(uri, new ParameterizedTypeReference<>() {});
    }

    public NgrokEndpointDTO buscarEndpoint(String id) {
        return get("/endpoints/" + id, new ParameterizedTypeReference<>() {});
    }

    public NgrokEndpointDTO criarEndpoint(NgrokEndpointCreateRequest req) {
        return post("/endpoints", req, new ParameterizedTypeReference<>() {});
    }

    public NgrokEndpointDTO atualizarEndpoint(String id, NgrokEndpointUpdateRequest req) {
        return patch("/endpoints/" + id, req, new ParameterizedTypeReference<>() {});
    }

    public void deletarEndpoint(String id) {
        delete("/endpoints/" + id);
    }

    // ─── Tunnels ─────────────────────────────────────────────────────────────

    public NgrokTunnelListResponse listarTunnels(String beforeId, String limit) {
        var uri = UriComponentsBuilder.fromPath("/tunnels")
                .queryParamIfPresent("before_id", java.util.Optional.ofNullable(beforeId))
                .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                .build()
                .toUriString();
        return get(uri, new ParameterizedTypeReference<>() {});
    }

    public NgrokTunnelDTO buscarTunnel(String id) {
        return get("/tunnels/" + id, new ParameterizedTypeReference<>() {});
    }

    // ─── Tunnel Sessions ─────────────────────────────────────────────────────

    public NgrokTunnelSessionListResponse listarTunnelSessions(String beforeId, String limit) {
        var uri = UriComponentsBuilder.fromPath("/tunnel_sessions")
                .queryParamIfPresent("before_id", java.util.Optional.ofNullable(beforeId))
                .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                .build()
                .toUriString();
        return get(uri, new ParameterizedTypeReference<>() {});
    }

    public NgrokTunnelSessionDTO buscarTunnelSession(String id) {
        return get("/tunnel_sessions/" + id, new ParameterizedTypeReference<>() {});
    }

    public void reiniciarTunnelSession(String id) {
        postVoid("/tunnel_sessions/" + id + "/restart", null);
    }

    public void pararTunnelSession(String id) {
        postVoid("/tunnel_sessions/" + id + "/stop", null);
    }

    // ─── Reserved Domains ────────────────────────────────────────────────────

    public NgrokReservedDomainListResponse listarReservedDomains(String beforeId, String limit) {
        var uri = UriComponentsBuilder.fromPath("/reserved_domains")
                .queryParamIfPresent("before_id", java.util.Optional.ofNullable(beforeId))
                .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                .build()
                .toUriString();
        return get(uri, new ParameterizedTypeReference<>() {});
    }

    public NgrokReservedDomainDTO buscarReservedDomain(String id) {
        return get("/reserved_domains/" + id, new ParameterizedTypeReference<>() {});
    }

    public NgrokReservedDomainDTO criarReservedDomain(NgrokReservedDomainCreateRequest req) {
        return post("/reserved_domains", req, new ParameterizedTypeReference<>() {});
    }

    public NgrokReservedDomainDTO atualizarReservedDomain(String id, NgrokReservedDomainUpdateRequest req) {
        return patch("/reserved_domains/" + id, req, new ParameterizedTypeReference<>() {});
    }

    public void deletarReservedDomain(String id) {
        delete("/reserved_domains/" + id);
    }

    // ─── Credentials ─────────────────────────────────────────────────────────

    public NgrokCredentialListResponse listarCredentials(String beforeId, String limit) {
        var uri = UriComponentsBuilder.fromPath("/credentials")
                .queryParamIfPresent("before_id", java.util.Optional.ofNullable(beforeId))
                .queryParamIfPresent("limit", java.util.Optional.ofNullable(limit))
                .build()
                .toUriString();
        return get(uri, new ParameterizedTypeReference<>() {});
    }

    public NgrokCredentialDTO buscarCredential(String id) {
        return get("/credentials/" + id, new ParameterizedTypeReference<>() {});
    }

    public NgrokCredentialDTO criarCredential(NgrokCredentialCreateRequest req) {
        return post("/credentials", req, new ParameterizedTypeReference<>() {});
    }

    public NgrokCredentialDTO atualizarCredential(String id, NgrokCredentialUpdateRequest req) {
        return patch("/credentials/" + id, req, new ParameterizedTypeReference<>() {});
    }

    public void deletarCredential(String id) {
        delete("/credentials/" + id);
    }

    // ─── Helpers HTTP ────────────────────────────────────────────────────────

    private <T> T get(String uri, ParameterizedTypeReference<T> type) {
        try {
            log.debug("ngrok GET {}", uri);
            return ngrokRestClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ngrok GET {}: {}", uri, ex.getMessage());
            throw new NgrokClientException("Falha na comunicação com o ngrok: " + ex.getMessage(), ex);
        }
    }

    private <T> T post(String uri, Object body, ParameterizedTypeReference<T> type) {
        try {
            log.debug("ngrok POST {}", uri);
            return ngrokRestClient.post()
                    .uri(uri)
                    .body(body)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ngrok POST {}: {}", uri, ex.getMessage());
            throw new NgrokClientException("Falha na comunicação com o ngrok: " + ex.getMessage(), ex);
        }
    }

    private void postVoid(String uri, Object body) {
        try {
            log.debug("ngrok POST (void) {}", uri);
            var req = ngrokRestClient.post().uri(uri);
            if (body != null) req.body(body);
            req.retrieve().toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ngrok POST {}: {}", uri, ex.getMessage());
            throw new NgrokClientException("Falha na comunicação com o ngrok: " + ex.getMessage(), ex);
        }
    }

    private <T> T patch(String uri, Object body, ParameterizedTypeReference<T> type) {
        try {
            log.debug("ngrok PATCH {}", uri);
            return ngrokRestClient.patch()
                    .uri(uri)
                    .body(body)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            log.error("Erro ngrok PATCH {}: {}", uri, ex.getMessage());
            throw new NgrokClientException("Falha na comunicação com o ngrok: " + ex.getMessage(), ex);
        }
    }

    private void delete(String uri) {
        try {
            log.debug("ngrok DELETE {}", uri);
            ngrokRestClient.delete()
                    .uri(uri)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Erro ngrok DELETE {}: {}", uri, ex.getMessage());
            throw new NgrokClientException("Falha na comunicação com o ngrok: " + ex.getMessage(), ex);
        }
    }
}
