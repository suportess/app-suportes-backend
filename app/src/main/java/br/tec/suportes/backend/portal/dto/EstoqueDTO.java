package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EstoqueDTO {

    @JsonProperty("CD_ESTOQUE")
    private String cdEstoque;   // a API retorna como string, ex: "45"

    @JsonProperty("DS_ESTOQUE")
    private String dsEstoque;
}
