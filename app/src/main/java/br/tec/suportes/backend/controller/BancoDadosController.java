package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.banco.BancoDadosDTO;
import br.tec.suportes.backend.dto.banco.BancoDadosRequest;
import br.tec.suportes.backend.dto.banco.PortalStatusDTO;
import br.tec.suportes.backend.service.BancoDadosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracoes/banco")
@RequiredArgsConstructor
public class BancoDadosController {

    private final BancoDadosService service;

    /** Verifica o status do portal ngrok da empresa ativa. */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<PortalStatusDTO>> status(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.verificarStatus(auth0Sub)));
    }

    /** Retorna a configuração de banco da empresa do usuário, ou null se não houver. */
    @GetMapping
    public ResponseEntity<ApiResponse<BancoDadosDTO>> buscar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorUsuario(auth0Sub).orElse(null)));
    }

    /** Cria ou atualiza a configuração de banco (apenas 1 por empresa). */
    @PostMapping
    public ResponseEntity<ApiResponse<BancoDadosDTO>> salvar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody BancoDadosRequest req
    ) {
        var dto = service.salvar(auth0Sub, req);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Banco de dados configurado com sucesso.", dto));
    }

    /** Remove a configuração de banco da empresa. */
    @DeleteMapping
    public ResponseEntity<Void> remover(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        service.remover(auth0Sub);
        return ResponseEntity.noContent().build();
    }
}
