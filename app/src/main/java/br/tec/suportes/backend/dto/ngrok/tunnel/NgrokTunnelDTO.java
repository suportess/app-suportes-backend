package br.tec.suportes.backend.dto.ngrok.tunnel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NgrokTunnelDTO {

    private String id;
    private String uri;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("started_at")
    private String startedAt;

    private String proto;
    private String region;
    private String metadata;

    @JsonProperty("forwards_to")
    private String forwardsTo;
}
