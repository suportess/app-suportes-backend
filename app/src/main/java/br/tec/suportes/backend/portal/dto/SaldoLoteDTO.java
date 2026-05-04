package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SaldoLoteDTO {

    @JsonProperty("QT_ESTOQUE_ATUAL")
    private String qtEstoqueAtual;  // portal retorna como string, ex: "576"

    @JsonProperty("DS_UNIDADE")
    private String dsUnidade;
}
