package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.movimento.MovimentoDTO;
import br.tec.suportes.backend.dto.movimento.MovimentoRequest;
import br.tec.suportes.backend.service.MovimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movimentos")
@RequiredArgsConstructor
public class MovimentoController {

    private final MovimentoService movimentoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MovimentoDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(required = false) Long cdEstoque,
            @RequestParam(required = false) String tpMvtoEstoque,
            @RequestParam(required = false) String dtMvtoEstoque,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        var resultado = movimentoService.listar(auth0Sub, cdEstoque, tpMvtoEstoque, dtMvtoEstoque, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimentoDTO>> buscarPorId(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(movimentoService.buscarPorId(auth0Sub, id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<?, ?>>> inserir(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody MovimentoRequest req
    ) {
        var resultado = movimentoService.inserir(auth0Sub, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Movimento criado.", resultado));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ApiResponse<Map<?, ?>>> concluir(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @RequestParam String dtConclusao
    ) {
        var resultado = movimentoService.concluir(auth0Sub, id, dtConclusao);
        return ResponseEntity.ok(ApiResponse.ok("Movimento concluído.", resultado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        movimentoService.excluir(auth0Sub, id);
        return ResponseEntity.ok(ApiResponse.ok("Movimento excluído.", null));
    }
}
