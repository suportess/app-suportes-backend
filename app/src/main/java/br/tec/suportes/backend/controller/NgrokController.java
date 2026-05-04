package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
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
import br.tec.suportes.backend.service.NgrokService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ngrok")
@RequiredArgsConstructor
public class NgrokController {

    private final NgrokService ngrokService;

    // ─── Endpoints ───────────────────────────────────────────────────────────

    @GetMapping("/endpoints")
    public ResponseEntity<ApiResponse<NgrokEndpointListResponse>> listarEndpoints(
            @RequestParam(required = false) String beforeId,
            @RequestParam(required = false) String limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.listarEndpoints(beforeId, limit)));
    }

    @GetMapping("/endpoints/{id}")
    public ResponseEntity<ApiResponse<NgrokEndpointDTO>> buscarEndpoint(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.buscarEndpoint(id)));
    }

    @PostMapping("/endpoints")
    public ResponseEntity<ApiResponse<NgrokEndpointDTO>> criarEndpoint(
            @Valid @RequestBody NgrokEndpointCreateRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Endpoint criado com sucesso.", ngrokService.criarEndpoint(req)));
    }

    @PatchMapping("/endpoints/{id}")
    public ResponseEntity<ApiResponse<NgrokEndpointDTO>> atualizarEndpoint(
            @PathVariable String id,
            @RequestBody NgrokEndpointUpdateRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.atualizarEndpoint(id, req)));
    }

    @DeleteMapping("/endpoints/{id}")
    public ResponseEntity<Void> deletarEndpoint(@PathVariable String id) {
        ngrokService.deletarEndpoint(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Tunnels ─────────────────────────────────────────────────────────────

    @GetMapping("/tunnels")
    public ResponseEntity<ApiResponse<NgrokTunnelListResponse>> listarTunnels(
            @RequestParam(required = false) String beforeId,
            @RequestParam(required = false) String limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.listarTunnels(beforeId, limit)));
    }

    @GetMapping("/tunnels/{id}")
    public ResponseEntity<ApiResponse<NgrokTunnelDTO>> buscarTunnel(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.buscarTunnel(id)));
    }

    // ─── Tunnel Sessions ─────────────────────────────────────────────────────

    @GetMapping("/tunnel-sessions")
    public ResponseEntity<ApiResponse<NgrokTunnelSessionListResponse>> listarTunnelSessions(
            @RequestParam(required = false) String beforeId,
            @RequestParam(required = false) String limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.listarTunnelSessions(beforeId, limit)));
    }

    @GetMapping("/tunnel-sessions/{id}")
    public ResponseEntity<ApiResponse<NgrokTunnelSessionDTO>> buscarTunnelSession(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.buscarTunnelSession(id)));
    }

    @PostMapping("/tunnel-sessions/{id}/restart")
    public ResponseEntity<Void> reiniciarTunnelSession(@PathVariable String id) {
        ngrokService.reiniciarTunnelSession(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tunnel-sessions/{id}/stop")
    public ResponseEntity<Void> pararTunnelSession(@PathVariable String id) {
        ngrokService.pararTunnelSession(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Reserved Domains ────────────────────────────────────────────────────

    @GetMapping("/reserved-domains")
    public ResponseEntity<ApiResponse<NgrokReservedDomainListResponse>> listarReservedDomains(
            @RequestParam(required = false) String beforeId,
            @RequestParam(required = false) String limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.listarReservedDomains(beforeId, limit)));
    }

    @GetMapping("/reserved-domains/{id}")
    public ResponseEntity<ApiResponse<NgrokReservedDomainDTO>> buscarReservedDomain(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.buscarReservedDomain(id)));
    }

    @PostMapping("/reserved-domains")
    public ResponseEntity<ApiResponse<NgrokReservedDomainDTO>> criarReservedDomain(
            @Valid @RequestBody NgrokReservedDomainCreateRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Domínio reservado criado com sucesso.", ngrokService.criarReservedDomain(req)));
    }

    /** Cria um domínio ngrok com subdomínio UUID gerado automaticamente. Sem body. */
    @PostMapping("/reserved-domains/generate")
    public ResponseEntity<ApiResponse<NgrokReservedDomainDTO>> gerarReservedDomain() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Domínio gerado com sucesso.", ngrokService.gerarReservedDomain()));
    }

    @PatchMapping("/reserved-domains/{id}")
    public ResponseEntity<ApiResponse<NgrokReservedDomainDTO>> atualizarReservedDomain(
            @PathVariable String id,
            @RequestBody NgrokReservedDomainUpdateRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.atualizarReservedDomain(id, req)));
    }

    @DeleteMapping("/reserved-domains/{id}")
    public ResponseEntity<ApiResponse<Void>> deletarReservedDomain(@PathVariable String id) {
        ngrokService.deletarReservedDomain(id);
        return ResponseEntity.ok(ApiResponse.ok("Domínio removido com sucesso.", null));
    }

    // ─── Credentials ─────────────────────────────────────────────────────────

    @GetMapping("/credentials")
    public ResponseEntity<ApiResponse<NgrokCredentialListResponse>> listarCredentials(
            @RequestParam(required = false) String beforeId,
            @RequestParam(required = false) String limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.listarCredentials(beforeId, limit)));
    }

    @GetMapping("/credentials/{id}")
    public ResponseEntity<ApiResponse<NgrokCredentialDTO>> buscarCredential(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.buscarCredential(id)));
    }

    @PostMapping("/credentials")
    public ResponseEntity<ApiResponse<NgrokCredentialDTO>> criarCredential(
            @RequestBody NgrokCredentialCreateRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Credential criada com sucesso.", ngrokService.criarCredential(req)));
    }

    @PatchMapping("/credentials/{id}")
    public ResponseEntity<ApiResponse<NgrokCredentialDTO>> atualizarCredential(
            @PathVariable String id,
            @RequestBody NgrokCredentialUpdateRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ngrokService.atualizarCredential(id, req)));
    }

    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<Void> deletarCredential(@PathVariable String id) {
        ngrokService.deletarCredential(id);
        return ResponseEntity.noContent().build();
    }
}
