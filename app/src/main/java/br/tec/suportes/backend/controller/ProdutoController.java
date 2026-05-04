package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.produto.ProdutoDTO;
import br.tec.suportes.backend.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(required = false) String nmProduto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize
    ) {
        var resultado = produtoService.listar(auth0Sub, nmProduto, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoDTO>> buscarPorId(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.buscarPorId(auth0Sub, id)));
    }
}
