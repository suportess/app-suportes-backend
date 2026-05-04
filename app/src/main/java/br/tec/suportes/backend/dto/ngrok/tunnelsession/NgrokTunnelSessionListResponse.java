package br.tec.suportes.backend.dto.ngrok.tunnelsession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokTunnelSessionListResponse {

    @JsonProperty("tunnel_sessions")
    private List<NgrokTunnelSessionDTO> tunnelSessions;

    private String uri;

    @JsonProperty("next_page_uri")
    private String nextPageUri;
}
