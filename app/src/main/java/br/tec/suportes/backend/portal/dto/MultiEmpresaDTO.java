package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MultiEmpresaDTO {

    @JsonProperty("CD_MULTI_EMPRESA")
    private Long cdMultiEmpresa;

    @JsonProperty("DS_MULTI_EMPRESA")
    private String dsMultiEmpresa;
}
