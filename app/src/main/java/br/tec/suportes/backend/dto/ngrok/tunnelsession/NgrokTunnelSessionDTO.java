package br.tec.suportes.backend.dto.ngrok.tunnelsession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NgrokTunnelSessionDTO {

    private String id;
    private String uri;
    private String ip;
    private String os;
    private String region;
    private String transport;
    private String metadata;

    @JsonProperty("started_at")
    private String startedAt;

    @JsonProperty("agent_version")
    private String agentVersion;
}
