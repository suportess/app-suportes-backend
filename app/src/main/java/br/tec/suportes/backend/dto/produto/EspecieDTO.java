package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EspecieDTO {

    @JsonProperty("CD_ESPECIE")
    private Integer cdEspecie;

    @JsonProperty("DS_ESPECIE")
    private String dsEspecie;
}
