package br.tec.suportes.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {

    @JsonProperty("dados")
    private List<T> dados;

    @JsonProperty("pagina")
    private int pagina;

    @JsonProperty("tamanhoPagina")
    private int tamanhoPagina;

    @JsonProperty("total")
    private long total;
}
