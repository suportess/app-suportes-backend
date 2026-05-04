package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.usuario.UsuarioDTO;
import br.tec.suportes.backend.dto.usuario.UsuarioRequest;
import br.tec.suportes.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Provisionamento: cria o usuário se não existir, atualiza perfil se já existir.
     * Chamado pelo frontend logo após o login no Auth0.
     */
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioDTO>> provisionarMe(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.findOrCreate(req)));
    }

    /** Retorna o perfil do usuário autenticado pelo sub do Auth0. */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioDTO>> buscarMe(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPorSub(auth0Sub)));
    }

    /** Lista todos os usuários do sistema. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> listarTodos(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listarTodos(auth0Sub)));
    }

    /** Retorna um usuário pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> buscarPorId(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPorId(auth0Sub, id)));
    }

    /** Vincula uma empresa ao usuário. */
    @PostMapping("/{id}/empresas/{empresaId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> vincularEmpresa(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @PathVariable Long empresaId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.vincularEmpresa(auth0Sub, id, empresaId)));
    }

    /** Remove o vínculo de uma empresa ao usuário. */
    @DeleteMapping("/{id}/empresas/{empresaId}")
    public ResponseEntity<ApiResponse<Void>> desvincularEmpresa(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @PathVariable Long empresaId
    ) {
        usuarioService.desvincularEmpresa(auth0Sub, id, empresaId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** Define a empresa ativa do usuário autenticado. */
    @PutMapping("/me/empresa-ativa/{empresaId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> atualizarEmpresaAtiva(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long empresaId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.atualizarEmpresaAtiva(auth0Sub, empresaId)));
    }
}

