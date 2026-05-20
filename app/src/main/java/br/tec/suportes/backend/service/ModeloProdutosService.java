package br.tec.suportes.backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Gera modelos Excel para importação de produtos.
 * Tipos: "obrigatorios" | "padrao" (default) | "todos"
 *
 * Estrutura da planilha (Row 0 = headers técnicos lidos pelo parser):
 *   Row 0 – nomes técnicos dos campos (ds_produto, sn_lote …)   → colorido
 *   Row 1 – linha de exemplo (em itálico)
 */
@Service
public class ModeloProdutosService {

    public byte[] gerarModelo(String tipo) {
        String t = (tipo == null || tipo.isBlank()) ? "padrao" : tipo.toLowerCase().trim();

        // ── Definição de colunas por tipo ─────────────────────────────────────
        String[] fieldNames;
        String[] exemploRow;

        switch (t) {
            case "obrigatorios" -> {
                fieldNames = new String[]{
                    "ds_produto", "sn_lote", "sn_validade", "sn_medicamento", "sn_consignado"
                };
                exemploRow = new String[]{
                    "DIPIRONA SÓDICA 500MG", "SIM", "SIM", "SIM", "NAO"
                };
            }
            case "todos" -> {
                fieldNames = new String[]{
                    "ds_produto", "sn_lote", "sn_validade", "sn_medicamento", "sn_consignado",
                    "opme_nexo", "cd_tip_ativ", "codigo_anvisa",
                    "cd_pro_fat", "ds_pro_fat", "cd_pro_fat_sus", "cd_procedimento_sus",
                    "valor_inicial_produto"
                };
                exemploRow = new String[]{
                    "CATETER VENOSO DUPLO LUMEN 7F", "NAO", "NAO", "NAO", "SIM",
                    "NAO", "1", "2222",
                    "", "CATETER DUPLO LUMEN", "", "",
                    "150.50"
                };
            }
            case "bloqueio" -> {
                fieldNames = new String[]{ "cd_produto", "acao" };
                exemploRow = new String[]{ "12345", "BLOQUEIO" };
            }
            case "vinculo" -> {
                fieldNames = new String[]{ "cd_produto_antigo", "cd_produto_novo" };
                exemploRow = new String[]{ "12345", "67890" };
            }
            default -> { // padrao
                fieldNames = new String[]{
                    "ds_produto", "sn_lote", "sn_validade", "sn_medicamento", "sn_consignado",
                    "opme_nexo", "cd_tip_ativ", "codigo_anvisa", "cd_pro_fat", "ds_pro_fat",
                    "valor_inicial_produto"
                };
                exemploRow = new String[]{
                    "DIPIRONA SÓDICA 500MG", "SIM", "SIM", "SIM", "NAO",
                    "NAO", "1", "", "", "",
                    "1.00"
                };
            }
        }

        int numCols = fieldNames.length;

        // ── Cores por tipo ────────────────────────────────────────────────────
        byte[] headerRgb = switch (t) {
            case "obrigatorios" -> new byte[]{(byte) 0x14, (byte) 0x6B, (byte) 0x45};
            case "todos"        -> new byte[]{(byte) 0x2D, (byte) 0x22, (byte) 0x7B};
            case "bloqueio"     -> new byte[]{(byte) 0xB4, (byte) 0x45, (byte) 0x09};
            case "vinculo"      -> new byte[]{(byte) 0x05, (byte) 0x6C, (byte) 0x77};
            default             -> new byte[]{(byte) 0x0D, (byte) 0x52, (byte) 0x6E};
        };

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Importacao_Produtos");

            CellStyle csHeader  = criarHeader(wb, headerRgb);
            CellStyle csExemplo = criarExemplo(wb);

            // ── Row 0: headers técnicos (o que o parser lê como nomes de coluna) ─
            Row rowHeader = sheet.createRow(0);
            rowHeader.setHeightInPoints(22);
            for (int i = 0; i < numCols; i++) {
                Cell c = rowHeader.createCell(i);
                c.setCellValue(fieldNames[i]);
                c.setCellStyle(csHeader);
            }

            // ── Row 1: exemplo ────────────────────────────────────────────────
            Row rowEx = sheet.createRow(1);
            rowEx.setHeightInPoints(16);
            for (int i = 0; i < numCols; i++) {
                Cell c = rowEx.createCell(i);
                c.setCellValue(exemploRow[i]);
                c.setCellStyle(csExemplo);
            }

            // ── Larguras automáticas ──────────────────────────────────────────
            for (int i = 0; i < numCols; i++) {
                sheet.autoSizeColumn(i);
                int auto = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max((int) (auto * 1.2) + 256, fieldNames[i].length() * 300));
            }

            sheet.createFreezePane(0, 1);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar modelo de produtos.", e);
        }
    }

    // ── Estilos ───────────────────────────────────────────────────────────────

    private CellStyle criarHeader(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(rgb, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarExemplo(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setItalic(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0xF8, (byte) 0xF9, (byte) 0xFA}, null));
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
}
