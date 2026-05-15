package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ImportacaoLote;
import br.tec.suportes.backend.domain.ImportacaoProduto;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ImportacaoLoteRepository;
import br.tec.suportes.backend.repository.ImportacaoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioImportacaoService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ImportacaoLoteRepository loteRepository;
    private final ImportacaoProdutoRepository produtoRepository;

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
