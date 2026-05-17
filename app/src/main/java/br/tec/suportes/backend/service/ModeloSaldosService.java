package br.tec.suportes.backend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ModeloSaldosService {

    /** Gera o modelo padrão (devolução). Mantido para compatibilidade. */
    public byte[] gerarModelo() {
        return gerarModelo("devolucao");
    }

    /**
     * Gera o arquivo Excel modelo conforme o tipo solicitado.
     * @param tipo "devolucao" | "entrada" | "misto"
     */
    public byte[] gerarModelo(String tipo) {
        String t = (tipo == null || tipo.isBlank()) ? "devolucao" : tipo.toLowerCase().trim();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            // ── Aba principal ─────────────────────────────────────────────────
            String sheetName = switch (t) {
                case "entrada"       -> "Importacao_Entrada";
                case "misto"         -> "Importacao_Misto";
                case "transferencia" -> "Importacao_Transferencia";
                default              -> "Importacao_Saldos";
            };
            Sheet sheet = wb.createSheet(sheetName);

            CellStyle csHeader      = criarHeader(wb);
            CellStyle csEntrada     = criarEntrada(wb);
            CellStyle csDev         = criarDevolucao(wb);
            CellStyle csTransf      = criarTransferencia(wb);
            CellStyle csTransfItem  = criarTransferenciaItem(wb);
            CellStyle csNumRight    = criarNumRight(wb);
            CellStyle csNumRightDev = criarNumRightDev(wb);
            CellStyle csNumRightTr  = criarNumRightTransf(wb);

            // Cabeçalhos
            String[] colunas = {"Produto", "Estoque", "Fornecedor", "Unidade", "Saldo", "Movimento"};
            Row rowHeader = sheet.createRow(0);
            rowHeader.setHeightInPoints(22);
            for (int i = 0; i < colunas.length; i++) {
                setCell(rowHeader, i, colunas[i], csHeader);
            }

            // Exemplos conforme tipo
            Object[][] exemplos = switch (t) {
                case "entrada" -> new Object[][]{
                    {"10305", "30", "37999", "UNI", "150", "ENTRADA"},
                };
                case "transferencia" -> new Object[][]{
                    {"10305", "30", "37999", "UNI", "150", "TRANSFERENCIA"},
                    {"12313", "30", "37999", "UNI", "50",  ""},
                    {"9572",  "30", "37999", "UNI", "100", ""},
                };
                case "misto" -> new Object[][]{
                    {"10305", "30", "",      "UNI", "5",   "TRANSFERENCIA"},
                    {"4091",  "30", "",      "UNI", "5",   "TRANSFERENCIA"},
                    {"12313", "30", "37999", "UNI", "50",  ""},
                    {"9572",  "30", "37999", "UNI", "100", ""},
                    {"10305", "30", "37999", "UNI", "150", "ENTRADA"},
                    {"10305", "30", "37999", "UNI", "150", "DEVOLUCAO"},
                    {"9572",  "30", "37999", "UNI", "100", "ENTRADA"},
                    {"9572",  "30", "37999", "UNI", "100", "DEVOLUCAO"},
                    {"10305", "30", "37999", "UNI", "5",   "ENTRADA"},
                };
                default -> new Object[][]{  // devolucao
                    {"3080", "2", "0099", "UND", "2",  "DEVOLUCAO"},
                    {"4010", "1", "5678", "FRS", "80", "DEVOLUCAO"},
                };
            };

            int rowIdx = 1;
            for (Object[] linha : exemplos) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(16);
                String mov = String.valueOf(linha[5]);
                boolean isTransfHead = "TRANSFERENCIA".equals(mov);
                boolean isTransfItem = mov.isBlank();
                boolean isDev = "DEVOLUCAO".equals(mov);
                for (int c = 0; c < linha.length; c++) {
                    CellStyle cs;
                    if (c == 4) {
                        cs = isTransfHead || isTransfItem ? csNumRightTr
                           : isDev ? csNumRightDev : csNumRight;
                    } else {
                        cs = isTransfHead ? csTransf
                           : isTransfItem ? csTransfItem
                           : isDev ? csDev : csEntrada;
                    }
                    setCell(row, c, String.valueOf(linha[c]), cs);
                }
            }

            // Larguras (em unidades de 256)
            int[] larguras = {15, 12, 15, 10, 10, 15};
            for (int i = 0; i < larguras.length; i++) {
                sheet.setColumnWidth(i, larguras[i] * 256);
            }
            sheet.createFreezePane(0, 1);

            // ── Aba de instruções ─────────────────────────────────────────────
            Sheet inst = wb.createSheet("Instrucoes");
            inst.setColumnWidth(0, 80 * 256);

            CellStyle csTitulo    = criarInstTitulo(wb);
            CellStyle csCabecalho = criarInstCabecalho(wb);
            CellStyle csNota      = criarInstNota(wb);
            CellStyle csDefault   = criarInstDefault(wb);

            String[][] instrucoes = "entrada".equals(t)
                ? new String[][]{
                    {"INSTRUCOES DE PREENCHIMENTO — MODELO ENTRADA"},
                    {""},
                    {"COLUNA         | OBRIGATORIO | DESCRICAO"},
                    {"Produto        | Nao         | CD_PRODUTO do MV (codigo numerico do produto)"},
                    {"Estoque        | SIM         | CD_ESTOQUE do MV — OBRIGATORIO para ENTRADA"},
                    {"Fornecedor     | SIM         | CD_FORNECEDOR do MV — OBRIGATORIO para ENTRADA"},
                    {"Unidade        | SIM         | Unidade de medida ex: UND, FRS, KG — OBRIGATORIO para ENTRADA"},
                    {"Saldo          | SIM         | Quantidade (valor numerico positivo)"},
                    {"Movimento      | SIM         | Use: ENTRADA (tambem aceita: ENT)"},
                    {""},
                    {"ATENCAO:"},
                    {"- Para ENTRADA os campos Estoque, Fornecedor e Unidade sao OBRIGATORIOS."},
                    {"- Linhas com esses campos vazios serao rejeitadas na tela de revisao."},
                    {"- Numeros decimais: use virgula (ex: 1,5) ou ponto (ex: 1.5)"},
                    {"- Valores negativos de saldo serao rejeitados"},
                  }
                : "transferencia".equals(t)
                ? new String[][]{
                    {"INSTRUCOES DE PREENCHIMENTO — MODELO TRANSFERENCIA"},
                    {""},
                    {"COLUNA         | OBRIGATORIO | DESCRICAO"},
                    {"Produto        | SIM (cabeca) | CD_PRODUTO do MV. Itens abaixo podem deixar vazio."},
                    {"Estoque        | Nao          | CD_ESTOQUE do MV. Itens herdam da linha cabeca se vazio."},
                    {"Fornecedor     | Nao          | CD_FORNECEDOR. Itens herdam da linha cabeca se vazio."},
                    {"Unidade        | Nao          | Unidade de medida. Itens herdam da linha cabeca se vazio."},
                    {"Saldo          | SIM          | Quantidade (valor numerico positivo)"},
                    {"Movimento      | SIM (cabeca) | Use: TRANSFERENCIA (tambem aceita: TRANSF, TRANS)"},
                    {""},
                    {"COMO FUNCIONA O GRUPO DE TRANSFERENCIA:"},
                    {"- A linha com Movimento=TRANSFERENCIA e a CABECA do grupo."},
                    {"- As linhas seguintes com Movimento VAZIO pertencem ao mesmo grupo (sao ITENS)."},
                    {"- O grupo termina quando aparece outra linha com Movimento preenchido."},
                    {"- Estoque, Fornecedor e Unidade da cabeca sao herdados pelos itens que nao os informarem."},
                    {""},
                    {"NOTAS:"},
                    {"- Itens do grupo nao precisam de Movimento preenchido — serao marcados como ITEM TRANSF."},
                    {"- Numeros decimais: use virgula (ex: 1,5) ou ponto (ex: 1.5)"},
                    {"- Valores negativos de saldo serao rejeitados"},
                  }
                : new String[][]{
                    {t.equals("misto") ? "INSTRUCOES DE PREENCHIMENTO — MODELO MISTO" : "INSTRUCOES DE PREENCHIMENTO — MODELO DEVOLUCAO"},
                    {""},
                    {"COLUNA         | OBRIGATORIO | DESCRICAO"},
                    {"Produto        | Nao         | CD_PRODUTO do MV (codigo numerico do produto)"},
                    {"Estoque        | Nao         | CD_ESTOQUE do MV (codigo numerico do estoque/setor)"},
                    {"Fornecedor     | Nao         | CD_FORNECEDOR do MV (codigo numerico do fornecedor)"},
                    {"Unidade        | Nao         | Unidade de medida (ex: UND, FRS, PAR, KG)"},
                    {"Saldo          | SIM         | Quantidade (valor numerico positivo)"},
                    {"Movimento      | SIM         | Valor do movimento (veja lista abaixo)"},
                    {""},
                    {"VALORES ACEITOS NO CAMPO MOVIMENTO:"},
                    {"  ENTRADA       - Entrada de estoque           (tambem aceita: ENT)"},
                    {"  BAIXA         - Baixa de estoque             (tambem aceita: BAI)"},
                    {"  DEVOLUCAO     - Devolucao ao fornecedor/setor (tambem aceita: DEV, DEVOL)"},
                    {"  TRANSFERENCIA - Transferencia entre setores   (tambem aceita: TRANSF, TRANS)"},
                    {""},
                    {"NOTAS:"},
                    {"- Os campos Saldo e Movimento sao obrigatorios. Linhas sem eles serao rejeitadas."},
                    {"- Para ENTRADA: Estoque, Fornecedor e Unidade tambem sao obrigatorios."},
                    {"- O campo Movimento nao e case-sensitive: ENTRADA, entrada e Entrada sao equivalentes."},
                    {"- Numeros decimais: use virgula (ex: 1,5) ou ponto (ex: 1.5)"},
                    {"- Valores negativos de saldo serao rejeitados"},
                    {"- Os cabecalhos da planilha devem seguir os nomes da aba modelo"},
                  };

            for (int i = 0; i < instrucoes.length; i++) {
                Row row = inst.createRow(i);
                row.setHeightInPoints(16);
                CellStyle cs;
                if (i == 0)                                   cs = csTitulo;
                else if (i == 2)                              cs = csCabecalho;
                else if (instrucoes[i][0].startsWith("NOTAS") || instrucoes[i][0].startsWith("ATENCAO")) cs = csNota;
                else                                          cs = csDefault;
                setCell(row, 0, instrucoes[i][0], cs);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar modelo de saldos.", e);
        }
    }

    // ── Estilos ───────────────────────────────────────────────────────────────

    private CellStyle criarHeader(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0x1A, (byte)0x5F, (byte)0x7A}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        f.setFontHeightInPoints((short) 11);
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarEntrada(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xEA, (byte)0xF4, (byte)0xFB}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarDevolucao(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xFF, (byte)0xF3, (byte)0xCD}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarNumRight(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xEA, (byte)0xF4, (byte)0xFB}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarNumRightDev(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xFF, (byte)0xF3, (byte)0xCD}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarTransferencia(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xED, (byte)0xE9, (byte)0xFE}, null)); // roxo claro
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(new XSSFColor(new byte[]{(byte)0x5B, (byte)0x21, (byte)0xB6}, null).getIndexed() == -1
            ? IndexedColors.VIOLET.getIndex() : IndexedColors.VIOLET.getIndex());
        s.setFont(f);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarTransferenciaItem(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xF5, (byte)0xF3, (byte)0xFF}, null)); // roxo muito claro
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarNumRightTransf(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xED, (byte)0xE9, (byte)0xFE}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorder(s);
        return s;
    }

    private CellStyle criarInstTitulo(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 12);
        f.setColor(IndexedColors.DARK_TEAL.getIndex());
        s.setFont(f);
        return s;
    }

    private CellStyle criarInstCabecalho(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private CellStyle criarInstNota(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private CellStyle criarInstDefault(XSSFWorkbook wb) {
        return wb.createCellStyle();
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
}
