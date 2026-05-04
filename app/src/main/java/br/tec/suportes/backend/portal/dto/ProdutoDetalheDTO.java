package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProdutoDetalheDTO {

    @JsonProperty("CD_PRODUTO")
    private Long cdProduto;

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;

    @JsonProperty("SN_LOTE")
    private String snLote;

    @JsonProperty("SN_CONTROLE_VALIDADE")
    private String snControleValidade;

    @JsonProperty("SN_CONSIGNADO")
    private String snConsignado;
}
