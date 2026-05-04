package br.tec.suportes.backend.dto.ngrok.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NgrokEndpointCreateRequest {

    @NotBlank(message = "url é obrigatório")
    private String url;

    @NotBlank(message = "type é obrigatório")
    private String type;

    @JsonProperty("traffic_policy")
    private String trafficPolicy;

    private String description;
    private String metadata;
}
