package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.produto.CadastroProdutoRequest;
import br.tec.suportes.backend.dto.produto.CadastroProdutoResponse;
import br.tec.suportes.backend.dto.produto.ClasseDTO;
import br.tec.suportes.backend.dto.produto.ClassificacaoRequest;
import br.tec.suportes.backend.dto.produto.EmpresaProdutoDTO;
import br.tec.suportes.backend.dto.produto.EspecieDTO;
import br.tec.suportes.backend.dto.produto.ImportacaoLoteDTO;
import br.tec.suportes.backend.dto.produto.ProdutoDTO;
import br.tec.suportes.backend.dto.produto.SubClasseDTO;
import br.tec.suportes.backend.dto.produto.UniProDTO;
import br.tec.suportes.backend.dto.produto.UnidadeDTO;
import br.tec.suportes.backend.dto.produto.ImportacaoProdutoDTO;
import br.tec.suportes.backend.service.CadastroProdutoService;
import br.tec.suportes.backend.service.ModeloProdutosService;
import br.tec.suportes.backend.service.ProdutoService;
import br.tec.suportes.backend.service.RelatorioImportacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CadastroProdutoService cadastroProdutoService;
    private final RelatorioImportacaoService relatorioImportacaoService;
    private final ModeloProdutosService modeloProdutosService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoDTO>>> listar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize
    ) {
        var resultado = produtoService.listar(auth0Sub, busca, page, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoDTO>> buscarPorId(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.buscarPorId(auth0Sub, id)));
    }

    @GetMapping("/{id}/unidades")
    public ResponseEntity<ApiResponse<List<UniProDTO>>> listarUnidades(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarUniPro(auth0Sub, id)));
    }

    @GetMapping("/{id}/empresas")
    public ResponseEntity<ApiResponse<List<EmpresaProdutoDTO>>> listarEmpresas(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarEmpresaProduto(auth0Sub, id)));
    }

    @GetMapping("/especies")
    public ResponseEntity<ApiResponse<List<EspecieDTO>>> listarEspecies(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarEspecies(auth0Sub)));
    }

    @GetMapping("/unidades")
    public ResponseEntity<ApiResponse<List<UnidadeDTO>>> listarUnidades(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarUnidades(auth0Sub)));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClasseDTO>>> listarClasses(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam Integer cdEspecie
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarClasses(auth0Sub, cdEspecie)));
    }

    @GetMapping("/sub-classes")
    public ResponseEntity<ApiResponse<List<SubClasseDTO>>> listarSubClasses(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam Integer cdEspecie,
            @RequestParam Integer cdClasse
    ) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.listarSubClasses(auth0Sub, cdEspecie, cdClasse)));
    }

    @PutMapping("/{id}/classificacao")
    public ResponseEntity<ApiResponse<Void>> vincularClassificacao(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id,
            @RequestBody @Valid ClassificacaoRequest request
    ) {
        produtoService.vincularClassificacao(auth0Sub, id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CadastroProdutoResponse>> cadastrar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestBody @Valid CadastroProdutoRequest request
    ) {
        var response = cadastroProdutoService.cadastrar(auth0Sub, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/importacoes")
    public ResponseEntity<ApiResponse<List<ImportacaoProdutoDTO>>> listarImportacoes(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(cadastroProdutoService.listarImportacoes(auth0Sub)));
    }

    // ── Lotes de importação ─────────────────────────────────────────────────

    @PostMapping("/importacoes/lotes")
    public ResponseEntity<ApiResponse<ImportacaoLoteDTO>> criarLote(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        var lote = cadastroProdutoService.criarLote(auth0Sub);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(lote));
    }

    @GetMapping("/importacoes/lotes")
    public ResponseEntity<ApiResponse<List<ImportacaoLoteDTO>>> listarLotes(
            @RequestHeader("X-Auth0-Sub") String auth0Sub
    ) {
        return ResponseEntity.ok(ApiResponse.ok(cadastroProdutoService.listarLotes(auth0Sub)));
    }

    @GetMapping("/modelo")
    public ResponseEntity<byte[]> downloadModelo(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(name = "tipo", defaultValue = "padrao") String tipo
    ) {
        byte[] excel = modeloProdutosService.gerarModelo(tipo);
        String filename = "modelo_produtos_" + tipo + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(excel.length);
        return ResponseEntity.ok().headers(headers).body(excel);
    }

    @GetMapping("/importacoes/lotes/{id}/relatorio")
    public ResponseEntity<byte[]> downloadRelatorio(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        byte[] excel = relatorioImportacaoService.gerarExcel(id);
        String filename = "importacao-" + id + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(excel.length);
        return ResponseEntity.ok().headers(headers).body(excel);
    }
}

