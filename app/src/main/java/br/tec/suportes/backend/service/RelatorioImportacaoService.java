package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ImportacaoDevolucao;
import br.tec.suportes.backend.domain.ImportacaoDevolucaoItem;
import br.tec.suportes.backend.domain.ImportacaoLote;
import br.tec.suportes.backend.domain.ImportacaoProduto;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoItemRepository;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoRepository;
import br.tec.suportes.backend.repository.ImportacaoLoteRepository;
import br.tec.suportes.backend.repository.ImportacaoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioImportacaoService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final ObjectMapper        OM     = new ObjectMapper();

    private final ImportacaoLoteRepository             loteRepository;
    private final ImportacaoProdutoRepository          produtoRepository;
    private final ImportacaoDevolucaoRepository        devolucaoRepository;
    private final ImportacaoDevolucaoItemRepository    devolucaoItemRepository;

    /**
     * Gera relatório Excel de uma sessão de importação de saldos (devolução/entrada/transferência).
     */
    public byte[] gerarExcelSaldos(Long idSessao) {
        ImportacaoDevolucao sessao = devolucaoRepository.findById(idSessao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessão " + idSessao + " não encontrada."));

        List<ImportacaoDevolucaoItem> itens = devolucaoItemRepository.findByCdSessaoOrderByNrLinha(idSessao);

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Importação de Saldos");

            // ── Estilos ───────────────────────────────────────────────────────
            CellStyle csTitulo   = criarEstiloTitulo(wb);
            CellStyle csCabInfo  = criarEstiloCabInfo(wb);
            CellStyle csHeader   = criarEstiloHeader(wb);
            CellStyle csEntrada  = criarCelulaColorida(wb, new byte[]{(byte)0xDC,(byte)0xFC,(byte)0xE7}); // verde claro
            CellStyle csDev      = criarCelulaColorida(wb, new byte[]{(byte)0xDB,(byte)0xEA,(byte)0xFE}); // azul claro
            CellStyle csTransf   = criarCelulaColorida(wb, new byte[]{(byte)0xED,(byte)0xE9,(byte)0xFE}); // roxo claro
            CellStyle csErro     = criarCelulaColorida(wb, new byte[]{(byte)0xFE,(byte)0xE2,(byte)0xE2}); // vermelho claro
            CellStyle csDefault  = criarEstiloCelula(wb);
            CellStyle csNumRight = criarNumRight(wb);
            CellStyle csNumRightEntrada = criarNumRightColorido(wb, new byte[]{(byte)0xDC,(byte)0xFC,(byte)0xE7});
            CellStyle csNumRightDev     = criarNumRightColorido(wb, new byte[]{(byte)0xDB,(byte)0xEA,(byte)0xFE});
            CellStyle csNumRightTransf  = criarNumRightColorido(wb, new byte[]{(byte)0xED,(byte)0xE9,(byte)0xFE});
            CellStyle csNumRightErro    = criarNumRightColorido(wb, new byte[]{(byte)0xFE,(byte)0xE2,(byte)0xE2});
            // ── Sub-linhas de detalhe Oracle ─────────────────────────────────
            CellStyle csDetalhe     = criarEstiloDetalhe(wb, new byte[]{(byte)0xF0,(byte)0xF0,(byte)0xF0});
            CellStyle csDetalheNum  = criarEstiloDetalheNum(wb, new byte[]{(byte)0xF0,(byte)0xF0,(byte)0xF0});
            CellStyle csLoteRow     = criarEstiloDetalhe(wb, new byte[]{(byte)0xFA,(byte)0xFA,(byte)0xFA});
            CellStyle csLoteRowNum  = criarEstiloDetalheNum(wb, new byte[]{(byte)0xFA,(byte)0xFA,(byte)0xFA});
            CellStyle csErroDetalhe = criarEstiloDetalhe(wb, new byte[]{(byte)0xFE,(byte)0xE2,(byte)0xE2});

            int linha = 0;

            // ── Título ────────────────────────────────────────────────────────
            Row rowTitulo = sheet.createRow(linha++);
            rowTitulo.setHeightInPoints(24);
            Cell cTitulo = rowTitulo.createCell(0);
            cTitulo.setCellValue("RELATÓRIO DE IMPORTAÇÃO DE SALDOS");
            cTitulo.setCellStyle(csTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // ── Info: empresa / usuário ───────────────────────────────────────
            Row rowInfo1 = sheet.createRow(linha++);
            setCell(rowInfo1, 0, "Empresa: " + nvl(sessao.getNmEmpresa()), csCabInfo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            setCell(rowInfo1, 4, "Importado por: " + nvl(sessao.getNmUsuario()) + " (" + nvl(sessao.getEmailUsuario()) + ")", csCabInfo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 9));

            // ── Info: data / totais ───────────────────────────────────────────
            Row rowInfo2 = sheet.createRow(linha++);
            setCell(rowInfo2, 0, "Data/Hora: " + (sessao.getDtImportacao() != null ? sessao.getDtImportacao().format(DT_FMT) : "-"), csCabInfo);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
            String resumo = "Total: " + sessao.getQtItens()
                    + "   OK: " + sessao.getQtSucesso()
                    + "   Erro: " + sessao.getQtErro();
            setCell(rowInfo2, 4, resumo, csCabInfo);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 9));

            // ── Espaço ────────────────────────────────────────────────────────
            sheet.createRow(linha++);

            // ── Cabeçalhos ────────────────────────────────────────────────────
            String[] colunas = {"#", "Produto", "Descrição", "Estoque", "Fornecedor", "Unidade", "Qt. Solicitada", "Qt. Executada", "Movimento", "Status"};
            Row rowHeader = sheet.createRow(linha++);
            rowHeader.setHeightInPoints(18);
            for (int i = 0; i < colunas.length; i++) {
                setCell(rowHeader, i, colunas[i], csHeader);
            }

            // ── Dados ─────────────────────────────────────────────────────────
            for (ImportacaoDevolucaoItem item : itens) {
                Row row = sheet.createRow(linha++);
                row.setHeightInPoints(15);

                boolean isErro      = "erro".equals(item.getStExecucao());
                boolean isItemTransf = "ITEM_TRANSF".equals(item.getTpMovimento());
                boolean isEntrada   = "ENTRADA".equals(item.getTpMovimento()) || isItemTransf;
                boolean isTransf    = "TRANSFERENCIA".equals(item.getTpMovimento());
                boolean isDev       = "DEVOLUCAO".equals(item.getTpMovimento());

                CellStyle csLinha    = isErro ? csErro    : isTransf || isItemTransf ? csTransf : isEntrada ? csEntrada : isDev ? csDev : csDefault;
                CellStyle csLinhaNum = isErro ? csNumRightErro : isTransf || isItemTransf ? csNumRightTransf : isEntrada ? csNumRightEntrada : isDev ? csNumRightDev : csNumRight;

                BigDecimal qtExec = item.getQtTotalDevolvida() != null ? item.getQtTotalDevolvida() : item.getQtDevolvida();

                setCell(row, 0, String.valueOf((item.getNrLinha() != null ? item.getNrLinha() : 0) + 1), csLinha);
                setCell(row, 1, nvl(item.getCdProduto()),   csLinha);
                setCell(row, 2, nvl(item.getDsProduto()),   csLinha);
                setCell(row, 3, item.getDsEstoque()    != null ? item.getDsEstoque()    : nvl(item.getCdEstoque()),   csLinha);
                setCell(row, 4, item.getNmFornecedor() != null ? item.getNmFornecedor() : nvl(item.getCdFornecedor()), csLinha);
                setCell(row, 5, item.getDsUnidade()    != null ? item.getDsUnidade()    : nvl(item.getCdUnidade()),   csLinha);
                setCell(row, 6, item.getQtDevolvida() != null ? item.getQtDevolvida().toPlainString() : "—", csLinhaNum);
                setCell(row, 7, qtExec != null ? qtExec.toPlainString() : "—", csLinhaNum);
                setCell(row, 8, nvl(item.getTpMovimento()), csLinha);
                setCell(row, 9, formatarStatus(item.getStExecucao()), csLinha);

                // ── Sub-linhas: detalhes gerados pelo Oracle ───────────────────
                if (isErro && item.getDsErro() != null) {
                    Row sub = sheet.createRow(linha++);
                    sub.setHeightInPoints(13);
                    setCell(sub, 0, "└─", csErroDetalhe);
                    for (int c = 1; c <= 9; c++) setCell(sub, c, c == 1 ? item.getDsErro() : "", csErroDetalhe);
                } else if (item.getJsonResultado() != null && !item.getJsonResultado().isBlank()) {
                    try {
                        Map<String, Object> json = OM.readValue(item.getJsonResultado(), new TypeReference<>() {});
                        String tpMov = nvl(item.getTpMovimento());
                        if ("ENTRADA".equals(tpMov)) {
                            linha = escreverSubRowEntrada(sheet, linha, json, csDetalhe, csDetalheNum);
                        } else if ("ITEM_TRANSF".equals(tpMov)) {
                            // json_resultado é o da transferência inteira; localiza em fase2_entradas
                            // a entrada correspondente a este produto
                            List<Map<String, Object>> fase2 = listOf(json, "fase2_entradas");
                            String cdProd = item.getCdProduto();
                            Map<String, Object> match = fase2.stream()
                                    .filter(e -> cdProd != null && cdProd.equals(strOf(e, "cd_produto")))
                                    .findFirst()
                                    .orElseGet(() -> fase2.isEmpty() ? null : fase2.get(0));
                            if (match != null) {
                                linha = escreverSubRowEntrada(sheet, linha, match, csDetalhe, csDetalheNum);
                            }
                        } else if ("DEVOLUCAO".equals(tpMov)) {
                            linha = escreverSubRowsDevolucao(sheet, linha, json, "devolucoes", csDetalhe, csDetalheNum, csLoteRow, csLoteRowNum);
                        } else if ("TRANSFERENCIA".equals(tpMov)) {
                            linha = escreverSubRowsDevolucao(sheet, linha, json, "fase1_devolucoes", csDetalhe, csDetalheNum, csLoteRow, csLoteRowNum);
                            linha = escreverSubRowsEntrada(sheet, linha, json, "fase2_entradas", csDetalhe, csDetalheNum);
                        }
                    } catch (Exception ignored) { /* JSON inválido */ }
                }
            }

            // ── Larguras: auto-fit + padding ─────────────────────────────────
            int[] maxChars = {5, 10, 45, 22, 32, 12, 16, 16, 35, 10};
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
                int auto    = sheet.getColumnWidth(i);
                int minimum = maxChars[i] * 256;
                int padding = (int) (auto * 0.15);          // +15 % de folga
                sheet.setColumnWidth(i, Math.max(auto + padding, minimum));
            }
            sheet.createFreezePane(0, 5); // congela até linha de dados

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de saldos.", e);
        }
    }

    // ── Sub-row helpers ─────────────────────────────────────────────────────

    private int escreverSubRowEntrada(Sheet sheet, int linha, Map<String, Object> json, CellStyle cs, CellStyle csNum) {
        Row sub = sheet.createRow(linha++);
        sub.setHeightInPoints(13);
        setCell(sub, 0, "└─", cs);
        setCell(sub, 1, "ENT_PRO: "   + strOf(json, "cd_ent_pro"),   cs);
        setCell(sub, 2, "ITENT_PRO: " + strOf(json, "cd_itent_pro"), cs);
        setCell(sub, 3, s(strOf(json, "cd_estoque"),   "Est: "),   cs);
        setCell(sub, 4, s(strOf(json, "cd_fornecedor"), "Forn: "), cs);
        setCell(sub, 5, s(strOf(json, "cd_uni_pro"),    "UNI: "),  cs);
        setCell(sub, 6, strOf(json, "qt_entrada"), csNum);
        setCell(sub, 7, strOf(json, "vl_total"),   csNum);
        String itlot = strOf(json, "cd_itlot_ent");
        setCell(sub, 8, itlot.isEmpty() ? "Sem controle de lote" : "ITLOT_ENT: " + itlot + "  |  LOTE: IMPORT  |  Val: 31/12/2030", cs);
        setCell(sub, 9, "", cs);
        return linha;
    }

    private int escreverSubRowsEntrada(Sheet sheet, int linha, Map<String, Object> json, String key, CellStyle cs, CellStyle csNum) {
        for (Map<String, Object> ent : listOf(json, key)) {
            Row sub = sheet.createRow(linha++);
            sub.setHeightInPoints(13);
            setCell(sub, 0, "└─", cs);
            setCell(sub, 1, "→ ENT_PRO: "  + strOf(ent, "cd_ent_pro"),   cs);
            setCell(sub, 2, "ITENT_PRO: "  + strOf(ent, "cd_itent_pro"), cs);
            setCell(sub, 3, s(strOf(ent, "cd_estoque"),   "Est: "),   cs);
            setCell(sub, 4, s(strOf(ent, "cd_fornecedor"), "Forn: "), cs);
            setCell(sub, 5, s(strOf(ent, "cd_uni_pro"),    "UNI: "),  cs);
            setCell(sub, 6, strOf(ent, "qt_entrada"), csNum);
            setCell(sub, 7, strOf(ent, "vl_total"),   csNum);
            String itlot = strOf(ent, "cd_itlot_ent");
            setCell(sub, 8, itlot.isEmpty() ? "Sem lote" : "ITLOT_ENT: " + itlot + "  |  LOTE: IMPORT", cs);
            setCell(sub, 9, "", cs);
        }
        return linha;
    }

    private int escreverSubRowsDevolucao(Sheet sheet, int linha, Map<String, Object> json,
                                         String key, CellStyle cs, CellStyle csNum,
                                         CellStyle csLote, CellStyle csLoteNum) {
        for (Map<String, Object> dev : listOf(json, key)) {
            Row sub = sheet.createRow(linha++);
            sub.setHeightInPoints(13);
            setCell(sub, 0, "└─", cs);
            setCell(sub, 1, "DEV_FOR: "  + strOf(dev, "cd_devolucao"), cs);
            setCell(sub, 2, "Entrada: "  + strOf(dev, "cd_ent_pro"),   cs);
            setCell(sub, 3, s(strOf(dev, "cd_estoque"),   "Est: "),   cs);
            setCell(sub, 4, s(strOf(dev, "cd_fornecedor"), "Forn: "), cs);
            setCell(sub, 5, s(strOf(dev, "cd_uni_pro"),    "UNI: "),  cs);
            setCell(sub, 6, strOf(dev, "qt_devolvida"), csNum);
            setCell(sub, 7, strOf(dev, "vl_total"),     csNum);
            List<Map<String, Object>> lotes = listOf(dev, "itens");
            setCell(sub, 8, lotes.isEmpty() ? "Sem lotes" : lotes.size() + " lote(s)", cs);
            setCell(sub, 9, "", cs);
            for (Map<String, Object> lt : lotes) {
                Row rl = sheet.createRow(linha++);
                rl.setHeightInPoints(13);
                setCell(rl, 0, "   └─",  csLote);
                setCell(rl, 1, "ITDEV_FOR: " + strOf(lt, "cd_itdev_for"), csLote);
                setCell(rl, 2, "Lote: "      + strOf(lt, "cd_lote"),       csLote);
                setCell(rl, 3, "Val: "       + strOf(lt, "dt_validade"),   csLote);
                setCell(rl, 4, "", csLote);
                setCell(rl, 5, "", csLote);
                setCell(rl, 6, strOf(lt, "qt"), csLoteNum);
                setCell(rl, 7, "", csLote);
                setCell(rl, 8, "", csLote);
                setCell(rl, 9, "", csLote);
            }
        }
        return linha;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> listOf(Map<String, Object> json, String key) {
        Object v = json.get(key);
        if (v instanceof List) return (List<Map<String, Object>>) v;
        return Collections.emptyList();
    }

    private String strOf(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return (v != null && !"null".equals(String.valueOf(v))) ? String.valueOf(v) : "";
    }

    /** Prefixa label somente quando o valor não é vazio. */
    private String s(String val, String label) {
        return val.isEmpty() ? "" : label + val;
    }

    private CellStyle criarEstiloDetalhe(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(rgb, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        Font f = wb.createFont();
        f.setItalic(true);
        f.setFontHeightInPoints((short) 9);
        f.setColor(IndexedColors.GREY_80_PERCENT.getIndex());
        s.setFont(f);
        applyBorder(s);
        return s;
    }

    private CellStyle criarEstiloDetalheNum(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(rgb, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        Font f = wb.createFont();
        f.setItalic(true);
        f.setFontHeightInPoints((short) 9);
        f.setColor(IndexedColors.GREY_80_PERCENT.getIndex());
        s.setFont(f);
        applyBorder(s);
        return s;
    }

    private String formatarStatus(String st) {
        if (st == null) return "—";
        return switch (st) {
            case "ok"       -> "OK";
            case "parcial"  -> "Parcial";
            case "erro"     -> "Erro";
            case "ignorado" -> "Ignorado";
            default         -> st;
        };
    }

    /**
     * Gera um arquivo Excel (.xlsx) com os produtos de um lote de importação.
     *
     * @param cdLote ID do lote
     * @return bytes do arquivo .xlsx
     */
    public byte[] gerarExcel(Long cdLote) {
        ImportacaoLote lote = loteRepository.findById(cdLote)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote de importação não encontrado."));

        List<ImportacaoProduto> produtos = produtoRepository.findByLoteIdOrderByDtImportacaoAsc(cdLote);

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Importação de Produtos");

            // ── Estilos ───────────────────────────────────────────────────────
            CellStyle titulo = criarEstiloTitulo(wb);
            CellStyle cabInfo = criarEstiloCabInfo(wb);
            CellStyle header = criarEstiloHeader(wb);
            CellStyle celula = criarEstiloCelula(wb);
            CellStyle celulaAlt = criarEstiloCelulaAlt(wb);

            int linha = 0;

            // ── Linha 0: título ───────────────────────────────────────────────
            Row rowTitulo = sheet.createRow(linha++);
            rowTitulo.setHeightInPoints(24);
            Cell cTitulo = rowTitulo.createCell(0);
            cTitulo.setCellValue("RELATÓRIO DE IMPORTAÇÃO DE PRODUTOS");
            cTitulo.setCellStyle(titulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // ── Linha 1: empresa / usuário ────────────────────────────────────
            Row rowInfo1 = sheet.createRow(linha++);
            setCell(rowInfo1, 0, "Empresa: " + nvl(lote.getNmEmpresa()), cabInfo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            setCell(rowInfo1, 4, "Importado por: " + nvl(lote.getNmUsuario()) + " (" + nvl(lote.getEmailUsuario()) + ")", cabInfo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 8));

            // ── Linha 2: data / total ─────────────────────────────────────────
            Row rowInfo2 = sheet.createRow(linha++);
            setCell(rowInfo2, 0, "Data/Hora: " + (lote.getDtImportacao() != null ? lote.getDtImportacao().format(DT_FMT) : "-"), cabInfo);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
            setCell(rowInfo2, 4, "Total de produtos: " + lote.getQtProdutos(), cabInfo);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 8));

            // ── Linha 3: espaço ───────────────────────────────────────────────
            sheet.createRow(linha++);

            // ── Linha 4: cabeçalhos da tabela ─────────────────────────────────
            Row rowHeader = sheet.createRow(linha++);
            rowHeader.setHeightInPoints(18);
            String[] colunas = {"Nº", "Cód. MV", "Descrição", "Desc. Comercial", "Subclasse", "Unidade", "Lote", "Validade", "Data/Hora Import."};
            for (int i = 0; i < colunas.length; i++) {
                setCell(rowHeader, i, colunas[i], header);
            }

            // ── Linhas de dados ───────────────────────────────────────────────
            int seq = 1;
            for (ImportacaoProduto p : produtos) {
                Row row = sheet.createRow(linha++);
                CellStyle estilo = (seq % 2 == 0) ? celulaAlt : celula;
                setCell(row, 0, String.valueOf(seq++),                                  estilo);
                setCell(row, 1, String.valueOf(p.getCdProdutoMv()),                      estilo);
                setCell(row, 2, nvl(p.getDsProduto()),                                   estilo);
                setCell(row, 3, nvl(p.getDsComercial()),                                 estilo);
                setCell(row, 4, nvl(p.getDsSubCla()),                                    estilo);
                setCell(row, 5, nvl(p.getCdUnidade()),                                   estilo);
                setCell(row, 6, "S".equals(p.getSnLote())     ? "Sim" : "Não",          estilo);
                setCell(row, 7, "S".equals(p.getSnValidade())  ? "Sim" : "Não",         estilo);
                setCell(row, 8, p.getDtImportacao() != null ? p.getDtImportacao().format(DT_FMT) : "-", estilo);
            }

            // ── Ajuste de largura das colunas ──────────────────────────────────
            int[] larguras = {8, 12, 50, 40, 30, 12, 10, 12, 20};
            for (int i = 0; i < larguras.length; i++) {
                sheet.setColumnWidth(i, larguras[i] * 256);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de importação.", e);
        }
    }

    // ── Helpers de estilo ────────────────────────────────────────────────────

    private CellStyle criarEstiloTitulo(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private CellStyle criarEstiloCabInfo(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(false);
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarEstiloHeader(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarEstiloCelula(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarEstiloCelulaAlt(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarCelulaColorida(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(rgb, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarNumRight(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarNumRightColorido(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(rgb, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private void applyBorder(CellStyle s) {
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }
}
