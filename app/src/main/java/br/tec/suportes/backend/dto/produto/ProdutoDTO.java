package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProdutoDTO {

    @JsonProperty("cd_produto")
    private Long cdProduto;

    @JsonProperty("nm_produto")
    private String nmProduto;

    @JsonProperty("sn_consignado")
    private String snConsignado;

    @JsonProperty("sn_lote")
    private String snLote;

    @JsonProperty("sn_validade")
    private String snValidade;
}
