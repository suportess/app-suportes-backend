package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassificacaoRequest {

    @NotNull
    @JsonProperty("cdEspecie")
    private Integer cdEspecie;

    @NotNull
    @JsonProperty("cdClasse")
    private Integer cdClasse;

    @NotNull
    @JsonProperty("cdSubCla")
    private Integer cdSubCla;
}
