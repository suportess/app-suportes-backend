package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProdutoConsignadoDTO {

    @JsonProperty("CD_PRODUTO")
    private String cdProduto;  // API retorna como string

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;
}
