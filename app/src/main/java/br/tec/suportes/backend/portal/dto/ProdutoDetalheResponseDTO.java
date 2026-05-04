package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProdutoDetalheResponseDTO {

    @JsonProperty("produto")
    private ProdutoDetalheDTO produto;
}
