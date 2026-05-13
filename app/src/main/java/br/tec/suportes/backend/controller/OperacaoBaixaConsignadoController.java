package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.operacaobaixa.OperacaoBaixaConsignadoDTO;
import br.tec.suportes.backend.dto.operacaobaixa.OperacaoBaixaConsignadoRequest;
import br.tec.suportes.backend.service.OperacaoBaixaConsignadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas/{empresaId}/operacoes-baixa-consignado")
@RequiredArgsConstructor
public class OperacaoBaixaConsignadoController {

    private final OperacaoBaixaConsignadoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OperacaoBaixaConsignadoDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.listar(empresaId, page, pageSize)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OperacaoBaixaConsignadoDTO>> salvar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @Valid @RequestBody OperacaoBaixaConsignadoRequest req
    ) {
        var dto = service.salvar(auth0Sub, empresaId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Operação salva.", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long id
    ) {
        service.deletar(empresaId, id);
        return ResponseEntity.ok(ApiResponse.ok("Operação excluída.", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OperacaoBaixaConsignadoDTO>> atualizar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId,
            @PathVariable Long id,
            @Valid @RequestBody OperacaoBaixaConsignadoRequest req
    ) {
        var dto = service.atualizar(empresaId, id, req);
        return ResponseEntity.ok(ApiResponse.ok("Operação atualizada.", dto));
    }
}
