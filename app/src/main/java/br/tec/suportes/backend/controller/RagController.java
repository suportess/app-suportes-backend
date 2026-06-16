package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.client.RagClient;
import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.rag.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagClient ragClient;

    // --- Consulta -------------------------------------------------------------

    @PostMapping("/consulta")
    public ResponseEntity<ApiResponse<ConsultaRagResponse>> consultar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody ConsultaRagRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.consultar(req)));
    }

    // --- Indexação ------------------------------------------------------------

    @PostMapping("/indexacao")
    public ResponseEntity<ApiResponse<Map<String, Object>>> indexarTudo(
            @RequestHeader("X-Auth0-Sub") String auth0Sub) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.indexarTudo()));
    }

    @PostMapping("/indexacao/{nome}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> indexarTabela(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable String nome) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.indexarTabela(nome)));
    }

    // --- Tabelas --------------------------------------------------------------

    @GetMapping("/tabelas")
    public ResponseEntity<ApiResponse<PagedResponse<TabelaRagResponse>>> listarTabelas(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanhoPagina,
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.listarTabelas(pagina, tamanhoPagina, nome)));
    }

    @PostMapping("/tabelas")
    public ResponseEntity<ApiResponse<TabelaRagResponse>> salvarTabela(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody TabelaRagRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.salvarTabela(req)));
    }

    @GetMapping("/tabelas/{id}")
    public ResponseEntity<ApiResponse<TabelaRagResponse>> buscarTabela(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.buscarTabela(id)));
    }

    @GetMapping("/tabelas/nome/{nome}")
    public ResponseEntity<ApiResponse<TabelaRagResponse>> buscarTabelaPorNome(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable String nome) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.buscarTabelaPorNome(nome)));
    }

    @PatchMapping("/tabelas/{nome}/descricao")
    public ResponseEntity<ApiResponse<TabelaRagResponse>> atualizarDescricaoTabela(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable String nome,
            @Valid @RequestBody TabelaDescricaoRagRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.atualizarDescricaoTabela(nome, req)));
    }

    @DeleteMapping("/tabelas/{id}")
    public ResponseEntity<Void> deletarTabela(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable UUID id) {
        ragClient.deletarTabela(id);
        return ResponseEntity.noContent().build();
    }

    // --- Relacionamentos ------------------------------------------------------

    @PostMapping("/relacionamentos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> salvarRelacionamento(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody RelacionamentoRagRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.salvarRelacionamento(req)));
    }

    @DeleteMapping("/relacionamentos/{id}")
    public ResponseEntity<Void> deletarRelacionamento(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable UUID id) {
        ragClient.deletarRelacionamento(id);
        return ResponseEntity.noContent().build();
    }

    // --- Exemplos -------------------------------------------------------------

    @PostMapping("/exemplos")
    public ResponseEntity<ApiResponse<ExemploRagResponse>> salvarExemplo(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @Valid @RequestBody ExemploRagRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.salvarExemplo(req)));
    }

    @GetMapping("/exemplos")
    public ResponseEntity<ApiResponse<List<ExemploRagResponse>>> listarExemplos(
            @RequestHeader("X-Auth0-Sub") String auth0Sub) {
        return ResponseEntity.ok(ApiResponse.ok(ragClient.listarExemplos()));
    }

    @DeleteMapping("/exemplos/{id}")
    public ResponseEntity<Void> deletarExemplo(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable UUID id) {
        ragClient.deletarExemplo(id);
        return ResponseEntity.noContent().build();
    }
}
