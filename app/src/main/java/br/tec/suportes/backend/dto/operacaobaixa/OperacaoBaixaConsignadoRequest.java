package br.tec.suportes.backend.dto.operacaobaixa;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OperacaoBaixaConsignadoRequest {

    @NotNull
    private Long cdMultiEmpresa;

    @NotNull
    private Long cdEstoque;

    private String dsEstoque;

    @NotEmpty
    @Valid
    private List<OrigemRequest> origens;

    @NotEmpty
    @Valid
    private List<ItemRequest> itens;

    @Data
    public static class OrigemRequest {
        @NotNull
        private Long cdProduto;
        private String dsProduto;
        private BigDecimal qtEstoqueAtual;
    }

    @Data
    public static class ItemRequest {
        @NotNull
        private Long cdProduto;
        private String dsProduto;
        private String snLote;
        private String snValidade;

        @NotEmpty
        @Valid
        private List<LinhaRequest> linhas;
    }

    @Data
    public static class LinhaRequest {
        @NotNull
        private Long cdFornecedor;
        private String nmFornecedor;

        @NotNull
        @DecimalMin(value = "0.0001", message = "Quantidade deve ser maior que zero.")
        private BigDecimal quantidade;

        private String lote;
        private String validade;
    }
}
