package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaldoConsigFornDTO {

    @JsonProperty("CD_MULTI_EMPRESA")
    private Long cdMultiEmpresa;

    @JsonProperty("CD_ESTOQUE")
    private Long cdEstoque;

    @JsonProperty("DS_ESTOQUE")
    private String dsEstoque;

    @JsonProperty("CD_PRODUTO")
    private Long cdProduto;

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;

    @JsonProperty("CD_FORNECEDOR")
    private Long cdFornecedor;

    @JsonProperty("NM_FORNECEDOR")
    private String nmFornecedor;

    @JsonProperty("QT_SALDO_CONSIG")
    private BigDecimal qtSaldoConsig;

    @JsonProperty("QT_DISPONIVEL_ENTPRO")
    private BigDecimal qtDisponivelEntpro;

    @JsonProperty("SN_ENTROU_POR_ESTOQUE")
    private String snEntrouPorEstoque;
}
