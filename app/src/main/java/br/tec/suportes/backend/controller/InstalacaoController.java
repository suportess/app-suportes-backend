package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.service.InstalacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracoes/instalacao")
@RequiredArgsConstructor
public class InstalacaoController {

    private final InstalacaoService service;

    /**
     * Retorna o docker-compose.yml pré-preenchido com os dados da empresa ativa do usuário,
     * dentro do envelope ApiResponse padrão para facilitar o tratamento no frontend.
     */
    @GetMapping("/docker-compose")
    public ResponseEntity<ApiResponse<String>> dockerCompose(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.gerarDockerCompose(auth0Sub)));
    }
}
