package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FornecedorDTO {

    @JsonProperty("CD_FORNECEDOR")
    private Long cdFornecedor;

    @JsonProperty("NM_FORNECEDOR")
    private String nmFornecedor;
}
