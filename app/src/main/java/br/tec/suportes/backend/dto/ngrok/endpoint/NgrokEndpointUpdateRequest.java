package br.tec.suportes.backend.dto.ngrok.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NgrokEndpointUpdateRequest {

    private String url;
    private String description;
    private String metadata;

    @JsonProperty("traffic_policy")
    private String trafficPolicy;
}
