package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.BloqueioItem;
import br.tec.suportes.backend.domain.BloqueioLote;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.BloqueioLoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloqueioRelatorioService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BloqueioLoteRepository    loteRepository;
    private final BloqueioHistoricoService  historicoService;

    public byte[] gerarExcel(String auth0Sub, Long idLote) {
        BloqueioLote lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote " + idLote + " não encontrado."));
        if (!lote.getAuth0Sub().equals(auth0Sub))
            throw new RecursoNaoEncontradoException("Lote " + idLote + " não encontrado.");

        List<BloqueioItem> itens = historicoService.listarItens(auth0Sub, idLote);

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Bloqueio_Produtos");

            // ── Estilos ─────────────────────────────────────────────────────
            CellStyle csTitulo  = titulo(wb);
            CellStyle csInfo    = info(wb);
            CellStyle csHeader  = header(wb);
            CellStyle csBloq    = celula(wb, new byte[]{(byte)0xED,(byte)0xE9,(byte)0xFE}); // roxo claro
            CellStyle csDesBloq = celula(wb, new byte[]{(byte)0xDC,(byte)0xFC,(byte)0xE7}); // verde claro
            CellStyle csErro    = celula(wb, new byte[]{(byte)0xFE,(byte)0xE2,(byte)0xE2}); // vermelho claro
            CellStyle csDefault = celula(wb, null);

            int row = 0;

            // ── Título ───────────────────────────────────────────────────────
            Row rTitulo = sheet.createRow(row++);
            rTitulo.setHeightInPoints(24);
            Cell cTitulo = rTitulo.createCell(0);
            cTitulo.setCellValue("RELATÓRIO DE BLOQUEIO / DESBLOQUEIO DE PRODUTOS");
            cTitulo.setCellStyle(csTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // ── Info ─────────────────────────────────────────────────────────
            Row rInfo1 = sheet.createRow(row++);
            set(rInfo1, 0, "Empresa: " + nvl(lote.getNmEmpresa()), csInfo);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 2));
            set(rInfo1, 3, "Usuário: " + nvl(lote.getNmUsuario()), csInfo);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 3, 4));

            Row rInfo2 = sheet.createRow(row++);
            set(rInfo2, 0, "Data/Hora: " + (lote.getDtBloqueio() != null ? lote.getDtBloqueio().format(DT_FMT) : "-"), csInfo);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 2));
            set(rInfo2, 3, "Total: " + lote.getQtItens() + "   OK: " + lote.getQtSucesso() + "   Erro: " + lote.getQtErro(), csInfo);
            sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 3, 4));

            sheet.createRow(row++); // espaço

            // ── Cabeçalhos ───────────────────────────────────────────────────
            Row rHeader = sheet.createRow(row++);
            rHeader.setHeightInPoints(18);
            String[] cols = {"#", "Cod. Produto", "Ação", "Status", "Observação"};
            for (int i = 0; i < cols.length; i++) set(rHeader, i, cols[i], csHeader);

            // ── Dados ────────────────────────────────────────────────────────
            int seq = 1;
            for (BloqueioItem item : itens) {
                Row r = sheet.createRow(row++);
                r.setHeightInPoints(15);

                boolean isBloq = "BLOQUEIO".equals(item.getAcao());
                CellStyle cs = item.getSnSucesso()
                        ? (isBloq ? csBloq : csDesBloq)
                        : csErro;

                set(r, 0, String.valueOf(seq++),                          cs);
                set(r, 1, String.valueOf(item.getCdProduto()),            cs);
                set(r, 2, item.getAcao(),                                 cs);
                set(r, 3, item.getSnSucesso() ? "Sucesso" : "Erro",      cs);
                set(r, 4, nvl(item.getDsErro()),                          cs);
            }

            // ── Larguras ─────────────────────────────────────────────────────
            int[] widths = {2000, 4000, 5000, 4000, 18000};
            for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);
            sheet.createFreezePane(0, row - itens.size());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório de bloqueio.", e);
        }
    }

    // ── Helpers de estilo ─────────────────────────────────────────────────────

    private void set(Row r, int col, String v, CellStyle cs) {
        Cell c = r.createCell(col);
        c.setCellValue(v != null ? v : "");
        c.setCellStyle(cs);
    }

    private String nvl(String v) { return v != null ? v : ""; }

    private CellStyle titulo(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 13); f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0x2D,(byte)0x22,(byte)0x7B}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private CellStyle info(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xF3,(byte)0xF0,(byte)0xFF}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT); s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
        return s;
    }

    private CellStyle header(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0x4C,(byte)0x1D,(byte)0x95}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s, BorderStyle.THIN, IndexedColors.WHITE.getIndex());
        return s;
    }

    private CellStyle celula(XSSFWorkbook wb, byte[] rgb) {
        XSSFCellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setFontHeightInPoints((short) 10); s.setFont(f);
        if (rgb != null) {
            s.setFillForegroundColor(new XSSFColor(rgb, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(s, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
        return s;
    }

    private void setBorder(CellStyle s, BorderStyle bs, short color) {
        s.setBorderBottom(bs); s.setBottomBorderColor(color);
        s.setBorderTop(bs);    s.setTopBorderColor(color);
        s.setBorderLeft(bs);   s.setLeftBorderColor(color);
        s.setBorderRight(bs);  s.setRightBorderColor(color);
    }
}
