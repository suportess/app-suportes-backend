package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClasseDTO {

    @JsonProperty("CD_ESPECIE")
    private Integer cdEspecie;

    @JsonProperty("CD_CLASSE")
    private Integer cdClasse;

    @JsonProperty("DS_CLASSE")
    private String dsClasse;
}
