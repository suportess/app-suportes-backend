package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UnidadeDTO {

    @JsonProperty("CD_UNIDADE")
    private String cdUnidade;

    @JsonProperty("DS_UNIDADE")
    private String dsUnidade;

    @JsonProperty("VL_FATOR")
    private Double vlFator;
}
