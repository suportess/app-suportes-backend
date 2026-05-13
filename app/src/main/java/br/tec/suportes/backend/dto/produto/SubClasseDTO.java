package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubClasseDTO {

    @JsonProperty("CD_ESPECIE")
    private Integer cdEspecie;

    @JsonProperty("CD_CLASSE")
    private Integer cdClasse;

    @JsonProperty("CD_SUB_CLA")
    private Integer cdSubCla;

    @JsonProperty("DS_SUB_CLA")
    private String dsSubCla;
}
