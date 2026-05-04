package br.tec.suportes.backend.dto.ngrok.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokEndpointListResponse {

    private List<NgrokEndpointDTO> endpoints;
    private String uri;

    @JsonProperty("next_page_uri")
    private String nextPageUri;
}
