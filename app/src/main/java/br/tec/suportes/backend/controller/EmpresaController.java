package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.empresa.EmpresaDTO;
import br.tec.suportes.backend.dto.empresa.EmpresaRequest;
import br.tec.suportes.backend.dto.empresa.GerarNgrokResponse;
import br.tec.suportes.backend.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/configuracoes/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    /** Lista todas as empresas do usuário autenticado. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.listarPorUsuario(auth0Sub)));
    }

    /** Busca uma empresa específica do usuário. */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaDTO>> buscar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.buscarPorUsuario(auth0Sub, id)));
    }

    /** Cadastra uma nova empresa vinculada ao usuário autenticado. */
    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaDTO>> cadastrar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody EmpresaRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa cadastrada com sucesso.", empresaService.cadastrar(auth0Sub, req)));
    }

    /** Atualiza os dados de uma empresa do usuário. */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaDTO>> atualizar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @Valid @RequestBody EmpresaRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Empresa atualizada.", empresaService.atualizar(auth0Sub, id, req)));
    }

    /** Gera uma nova API Key para a empresa. */
    @PostMapping("/{id}/regenerar-apikey")
    public ResponseEntity<ApiResponse<EmpresaDTO>> regenerarApikey(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok("API Key regenerada.", empresaService.regenerarApikey(auth0Sub, id)));
    }

    /** Remove a empresa e o vínculo com o usuário. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        empresaService.deletar(auth0Sub, id);
        return ResponseEntity.noContent().build();
    }

    /** Gera um domínio ngrok e salva em ds_host_portal de forma atômica. */
    @PostMapping("/{id}/gerar-ngrok")
    public ResponseEntity<ApiResponse<GerarNgrokResponse>> gerarNgrokDomain(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("Domínio ngrok gerado e salvo.", empresaService.gerarNgrokDomain(auth0Sub, id)));
    }

    /** Remove o domínio ngrok (via API ngrok se ngrokId informado) e limpa ds_host_portal. */
    @DeleteMapping("/{id}/gerar-ngrok")
    public ResponseEntity<ApiResponse<EmpresaDTO>> removerNgrokDomain(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @RequestParam(required = false) String ngrokId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("Domínio removido.", empresaService.removerNgrokDomain(auth0Sub, id, ngrokId)));
    }
}
