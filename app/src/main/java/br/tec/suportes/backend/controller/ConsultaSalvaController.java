package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.consulta.ConsultaSalvaDTO;
import br.tec.suportes.backend.dto.consulta.ConsultaSalvaRequest;
import br.tec.suportes.backend.service.ConsultaSalvaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaSalvaController {

    private final ConsultaSalvaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsultaSalvaDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub) {
        return ResponseEntity.ok(ApiResponse.ok(service.listar(auth0Sub)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConsultaSalvaDTO>> salvar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody ConsultaSalvaRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.salvar(auth0Sub, req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultaSalvaDTO>> atualizar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @Valid @RequestBody ConsultaSalvaRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(auth0Sub, id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id) {
        service.deletar(auth0Sub, id);
        return ResponseEntity.noContent().build();
    }
}
