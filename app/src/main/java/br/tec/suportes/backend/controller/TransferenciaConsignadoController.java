package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.transferencia.TransferenciaConsignadoDTO;
import br.tec.suportes.backend.dto.transferencia.TransferenciaConsignadoRequest;
import br.tec.suportes.backend.service.TransferenciaConsignadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas/{empresaId}/transferencias-consignado")
@RequiredArgsConstructor
public class TransferenciaConsignadoController {

    private final TransferenciaConsignadoService service;

    /** Lista as transferências da empresa, paginado (20/página). */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TransferenciaConsignadoDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.listar(empresaId, page, pageSize)));
    }

    /** Salva como PENDENTE ao avançar para o passo 4. */
    @PostMapping
    public ResponseEntity<ApiResponse<TransferenciaConsignadoDTO>> salvar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @Valid @RequestBody TransferenciaConsignadoRequest req
    ) {
        var dto = service.salvar(auth0Sub, empresaId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Transferência salva.", dto));
    }

    /** Confirma a operação: chama o portal e marca como CONCLUIDO. */
    @PostMapping("/{id}/concluir")
    public ResponseEntity<ApiResponse<TransferenciaConsignadoDTO>> concluir(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long id
    ) {
        var dto = service.concluir(auth0Sub, empresaId, id);
        return ResponseEntity.ok(ApiResponse.ok("Transferência concluída.", dto));
    }
}
