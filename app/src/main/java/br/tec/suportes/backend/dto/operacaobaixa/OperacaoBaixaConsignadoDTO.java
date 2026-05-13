package br.tec.suportes.backend.dto.operacaobaixa;

import br.tec.suportes.backend.domain.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OperacaoBaixaConsignadoDTO {

    private Long id;
    private Long empresaId;
    private Long cdMultiEmpresa;
    private Long cdEstoque;
    private String dsEstoque;
    private String status;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtConclusao;
    private List<OrigemDTO> origens;
    private List<ItemDTO> itens;

    public static OperacaoBaixaConsignadoDTO from(OperacaoBaixaConsignado op) {
        var dto = new OperacaoBaixaConsignadoDTO();
        dto.setId(op.getId());
        dto.setEmpresaId(op.getEmpresaId());
        dto.setCdMultiEmpresa(op.getCdMultiEmpresa());
        dto.setCdEstoque(op.getCdEstoque());
        dto.setDsEstoque(op.getDsEstoque());
        dto.setStatus(op.getStatus());
        dto.setDtCriacao(op.getDtCriacao());
        dto.setDtConclusao(op.getDtConclusao());
        dto.setOrigens(op.getOrigens().stream().map(OrigemDTO::from).toList());
        dto.setItens(op.getItens().stream().map(ItemDTO::from).toList());
        return dto;
    }

    @Data
    public static class OrigemDTO {
        private Long id;
        private Long cdProduto;
        private String dsProduto;
        private BigDecimal qtEstoqueAtual;

        static OrigemDTO from(OperacaoBaixaConsignadoOrigem o) {
            var dto = new OrigemDTO();
            dto.setId(o.getId());
            dto.setCdProduto(o.getCdProduto());
            dto.setDsProduto(o.getDsProduto());
            dto.setQtEstoqueAtual(o.getQtEstoqueAtual());
            return dto;
        }
    }

    @Data
    public static class ItemDTO {
        private Long id;
        private Long cdProduto;
        private String dsProduto;
        private String snLote;
        private String snValidade;
        private List<LinhaDTO> linhas;

        static ItemDTO from(OperacaoBaixaConsignadoItem i) {
            var dto = new ItemDTO();
            dto.setId(i.getId());
            dto.setCdProduto(i.getCdProduto());
            dto.setDsProduto(i.getDsProduto());
            dto.setSnLote(i.getSnLote());
            dto.setSnValidade(i.getSnValidade());
            dto.setLinhas(i.getLinhas().stream().map(LinhaDTO::from).toList());
            return dto;
        }
    }

    @Data
    public static class LinhaDTO {
        private Long id;
        private Long cdFornecedor;
        private String nmFornecedor;
        private BigDecimal quantidade;
        private String lote;
        private String validade;

        static LinhaDTO from(OperacaoBaixaConsignadoLinha l) {
            var dto = new LinhaDTO();
            dto.setId(l.getId());
            dto.setCdFornecedor(l.getCdFornecedor());
            dto.setNmFornecedor(l.getNmFornecedor());
            dto.setQuantidade(l.getQuantidade());
            dto.setLote(l.getLote());
            dto.setValidade(l.getValidade());
            return dto;
        }
    }
}
