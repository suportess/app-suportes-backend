package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaldoProdutoConsignadoDTO {

    @JsonProperty("CD_PRODUTO")
    private String cdProduto;

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;

    @JsonProperty("QT_ESTOQUE_ATUAL")
    private BigDecimal qtEstoqueAtual;

    @JsonProperty("DS_UNI_PRO")
    private String dsUniPro;
}
