package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.saldos.ConsultaSaldosRequest;
import br.tec.suportes.backend.dto.saldos.ImportacaoDevolucaoItemDTO;
import br.tec.suportes.backend.dto.saldos.ImportacaoSaldosHistoricoDTO;
import br.tec.suportes.backend.dto.saldos.ImportacaoSaldosRequest;
import br.tec.suportes.backend.dto.saldos.ImportarDevolucaoItemRequest;
import br.tec.suportes.backend.dto.saldos.ImportarDevolucaoItemResponse;
import br.tec.suportes.backend.dto.saldos.TransferenciaRequest;
import br.tec.suportes.backend.dto.vinculo.ItVinculoProdutoDTO;
import br.tec.suportes.backend.dto.vinculo.VinculoProdutoDTO;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoItemRepository;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoRepository;
import br.tec.suportes.backend.repository.ItVinculoProdutoRepository;
import br.tec.suportes.backend.repository.VinculoProdutoRepository;
import br.tec.suportes.backend.service.ConsultaSaldosService;
import br.tec.suportes.backend.service.ImportacaoDevolucaoService;
import br.tec.suportes.backend.service.ImportacaoTransferenciaService;
import br.tec.suportes.backend.service.ModeloSaldosService;
import br.tec.suportes.backend.service.RelatorioImportacaoService;
import br.tec.suportes.backend.service.VinculoProdutoService;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/saldos")
@RequiredArgsConstructor
public class SaldosController {

    private final ModeloSaldosService modeloSaldosService;
    private final ConsultaSaldosService consultaSaldosService;
    private final ImportacaoDevolucaoService importacaoDevolucaoService;
    private final ImportacaoTransferenciaService importacaoTransferenciaService;
    private final ImportacaoDevolucaoRepository importacaoDevolucaoRepository;
    private final ImportacaoDevolucaoItemRepository importacaoDevolucaoItemRepository;
    private final RelatorioImportacaoService relatorioImportacaoService;
    private final VinculoProdutoService vinculoProdutoService;
    private final VinculoProdutoRepository vinculoProdutoRepository;
    private final ItVinculoProdutoRepository itVinculoProdutoRepository;

    /**
     * Retorna o arquivo Excel modelo para importação de saldos.
     * GET /saldos/modelo
     */
    @GetMapping("/modelo")
    public ResponseEntity<byte[]> downloadModelo(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(name = "tipo", defaultValue = "devolucao") String tipo
    ) {
        byte[] excel = modeloSaldosService.gerarModelo(tipo);
        String filename = "modelo_" + tipo + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(excel.length);

        return ResponseEntity.ok().headers(headers).body(excel);
    }

    /**
     * Recebe a lista de saldos validados e os importa.
     * POST /saldos/importar
     */
    @PostMapping("/importar")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> importar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestBody @Valid ImportacaoSaldosRequest request
    ) {
        int importados = request.itens().size();
        // TODO: implementar lógica de persistência dos saldos no Oracle (MV)
        return ResponseEntity.ok(ApiResponse.ok(Map.of("importados", importados)));
    }

    /**
     * Consulta saldo atual de cada item no Oracle (EST_PRO, EST_CONSIG_FORN, LOT_PRO).
     * POST /saldos/consultar
     */
    @PostMapping("/consultar")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> consultar(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestBody @Valid ConsultaSaldosRequest request
    ) {
        List<Map<String, Object>> resultado = consultaSaldosService.consultar(auth0Sub, request.itens());
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    /**
     * POST /saldos/devolver-item — registra e executa uma devolução de saldo consignado, linha a linha.
     * Persiste snapshot completo da tela no Postgres e chama o bloco Oracle devolver-saldo-consig.
     */
    @PostMapping("/devolver-item")
    public ResponseEntity<ApiResponse<ImportarDevolucaoItemResponse>> devolverItem(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestBody @Valid ImportarDevolucaoItemRequest request
    ) {
        ImportarDevolucaoItemResponse resultado = importacaoDevolucaoService.processarItem(auth0Sub, request);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    /**
     * Retorna as últimas sessões de importação de saldos paginadas (10/página).
     * GET /saldos/historico?page=0&size=10
     */
    @GetMapping("/historico")
    public ResponseEntity<ApiResponse<PagedResponse<ImportacaoSaldosHistoricoDTO>>> historico(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, Math.min(size, 50));
        var pg = importacaoDevolucaoRepository.findAllByOrderByDtImportacaoDesc(pageable);

        var resp = new PagedResponse<ImportacaoSaldosHistoricoDTO>();
        resp.setDados(pg.getContent().stream().map(ImportacaoSaldosHistoricoDTO::of).toList());
        resp.setPagina(pg.getNumber());
        resp.setTamanhoPagina(pg.getSize());
        resp.setTotal(pg.getTotalElements());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    /**
     * Retorna os itens de uma sessão de importação.
     * GET /saldos/historico/{id}/itens
     */
    @GetMapping("/historico/{id}/itens")
    public ResponseEntity<ApiResponse<List<ImportacaoDevolucaoItemDTO>>> historicoItens(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        if (!importacaoDevolucaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Sessão " + id + " não encontrada.");
        }
        List<ImportacaoDevolucaoItemDTO> itens = importacaoDevolucaoItemRepository
                .findByCdSessaoOrderByNrLinha(id)
                .stream()
                .map(ImportacaoDevolucaoItemDTO::of)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(itens));
    }

    /**
     * Gera e devolve o relatório Excel de uma sessão de importação de saldos.
     * GET /saldos/historico/{id}/relatorio
     */
    @GetMapping("/historico/{id}/relatorio")
    public ResponseEntity<byte[]> relatorioSaldos(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        byte[] excel = relatorioImportacaoService.gerarExcelSaldos(id);
        String filename = "relatorio-saldos-" + id + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(excel.length);

        return ResponseEntity.ok().headers(headers).body(excel);
    }

    /**
     * Executa transferência atômica: devolução das cabeças + entrada dos filhos em Oracle.
     * POST /saldos/transferencia
     */
    @PostMapping("/transferencia")
    public ResponseEntity<ApiResponse<Map<String, Object>>> transferencia(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestBody @Valid TransferenciaRequest request
    ) {
        Map<String, Object> resultado = importacaoTransferenciaService.processar(auth0Sub, request);

        // Persiste no histórico como um único item por grupo
        if (request.cdProduto() != null || request.cdSessao() != null) {
            try {
                Long cdSessao = importacaoDevolucaoService.persistirTransferencia(auth0Sub, request, resultado);
                resultado = new java.util.HashMap<>(resultado);
                resultado.put("cd_sessao", cdSessao);
            } catch (Exception e) {
                log.warn("Falha ao persistir transferencia no historico: {}", e.getMessage());
            }
        }

        // Persiste vínculos SUS gerados (se houver)
        try {
            @SuppressWarnings("unchecked")
            java.util.List<?> susVinculos = (java.util.List<?>) resultado.get("sus_vinculos");
            if (susVinculos != null && !susVinculos.isEmpty()) {
                Long cdVinculo = vinculoProdutoService.persistir(auth0Sub, resultado);
                resultado = new java.util.HashMap<>(resultado);
                resultado.put("cd_vinculo_sessao", cdVinculo);
            }
        } catch (Exception e) {
            log.warn("Falha ao persistir vinculos SUS: {}", e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    /**
     * Retorna o histórico paginado de sessões de vínculo SUS.
     * GET /saldos/vinculos?page=0&size=10
     */
    @GetMapping("/vinculos")
    public ResponseEntity<ApiResponse<PagedResponse<VinculoProdutoDTO>>> vinculos(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, Math.min(size, 50));
        var pg = vinculoProdutoRepository.findAllByOrderByDtVinculoDesc(pageable);

        var resp = new PagedResponse<VinculoProdutoDTO>();
        resp.setDados(pg.getContent().stream().map(VinculoProdutoDTO::of).toList());
        resp.setPagina(pg.getNumber());
        resp.setTamanhoPagina(pg.getSize());
        resp.setTotal(pg.getTotalElements());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    /**
     * Retorna os itens de uma sessão de vínculo SUS.
     * GET /saldos/vinculos/{id}/itens
     */
    @GetMapping("/vinculos/{id}/itens")
    public ResponseEntity<ApiResponse<java.util.List<ItVinculoProdutoDTO>>> vinculosItens(
            @RequestHeader("X-Auth0-Sub") String auth0Sub,
            @PathVariable Long id
    ) {
        if (!vinculoProdutoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Sessão de vínculo " + id + " não encontrada.");
        }
        var itens = itVinculoProdutoRepository.findByCdSessaoOrderByNrLinha(id)
                .stream().map(ItVinculoProdutoDTO::of).toList();
        return ResponseEntity.ok(ApiResponse.ok(itens));
    }
}
