package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.NgrokClient;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NgrokService {

    private final NgrokClient ngrokClient;

    // ─── Endpoints ───────────────────────────────────────────────────────────

    public NgrokEndpointListResponse listarEndpoints(String beforeId, String limit) {
        return ngrokClient.listarEndpoints(beforeId, limit);
    }

    public NgrokEndpointDTO buscarEndpoint(String id) {
        return ngrokClient.buscarEndpoint(id);
    }

    public NgrokEndpointDTO criarEndpoint(NgrokEndpointCreateRequest req) {
        return ngrokClient.criarEndpoint(req);
    }

    public NgrokEndpointDTO atualizarEndpoint(String id, NgrokEndpointUpdateRequest req) {
        return ngrokClient.atualizarEndpoint(id, req);
    }

    public void deletarEndpoint(String id) {
        ngrokClient.deletarEndpoint(id);
    }

    // ─── Tunnels ─────────────────────────────────────────────────────────────

    public NgrokTunnelListResponse listarTunnels(String beforeId, String limit) {
        return ngrokClient.listarTunnels(beforeId, limit);
    }

    public NgrokTunnelDTO buscarTunnel(String id) {
        return ngrokClient.buscarTunnel(id);
    }

    // ─── Tunnel Sessions ─────────────────────────────────────────────────────

    public NgrokTunnelSessionListResponse listarTunnelSessions(String beforeId, String limit) {
        return ngrokClient.listarTunnelSessions(beforeId, limit);
    }

    public NgrokTunnelSessionDTO buscarTunnelSession(String id) {
        return ngrokClient.buscarTunnelSession(id);
    }

    public void reiniciarTunnelSession(String id) {
        ngrokClient.reiniciarTunnelSession(id);
    }

    public void pararTunnelSession(String id) {
        ngrokClient.pararTunnelSession(id);
    }

    // ─── Reserved Domains ────────────────────────────────────────────────────

    public NgrokReservedDomainListResponse listarReservedDomains(String beforeId, String limit) {
        return ngrokClient.listarReservedDomains(beforeId, limit);
    }

    public NgrokReservedDomainDTO buscarReservedDomain(String id) {
        return ngrokClient.buscarReservedDomain(id);
    }

    public NgrokReservedDomainDTO criarReservedDomain(NgrokReservedDomainCreateRequest req) {
        return ngrokClient.criarReservedDomain(req);
    }

    /** Gera automaticamente um subdomínio único no ngrok (UUID-based). */
    public NgrokReservedDomainDTO gerarReservedDomain() {
        return gerarReservedDomain("N/A");
    }

    /**
     * Gera automaticamente um subdomínio único no ngrok com descrição da empresa.
     * A descrição fica visível no painel ngrok: "Gerado automaticamente para Hospital X".
     */
    public NgrokReservedDomainDTO gerarReservedDomain(String nmEmpresa) {
        String subdomain = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        NgrokReservedDomainCreateRequest req = new NgrokReservedDomainCreateRequest();
        req.setDomain(subdomain + ".ngrok-free.dev");
        req.setDescription("Gerado automaticamente para " + nmEmpresa);
        return ngrokClient.criarReservedDomain(req);
    }

    public NgrokReservedDomainDTO atualizarReservedDomain(String id, NgrokReservedDomainUpdateRequest req) {
        return ngrokClient.atualizarReservedDomain(id, req);
    }

    public void deletarReservedDomain(String id) {
        ngrokClient.deletarReservedDomain(id);
    }

    // ─── Credentials ─────────────────────────────────────────────────────────

    public NgrokCredentialListResponse listarCredentials(String beforeId, String limit) {
        return ngrokClient.listarCredentials(beforeId, limit);
    }

    public NgrokCredentialDTO buscarCredential(String id) {
        return ngrokClient.buscarCredential(id);
    }

    public NgrokCredentialDTO criarCredential(NgrokCredentialCreateRequest req) {
        return ngrokClient.criarCredential(req);
    }

    public NgrokCredentialDTO atualizarCredential(String id, NgrokCredentialUpdateRequest req) {
        return ngrokClient.atualizarCredential(id, req);
    }

    public void deletarCredential(String id) {
        ngrokClient.deletarCredential(id);
    }
}
