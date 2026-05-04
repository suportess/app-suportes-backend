package br.tec.suportes.backend.dto.ngrok.tunnel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokTunnelListResponse {

    private List<NgrokTunnelDTO> tunnels;
    private String uri;

    @JsonProperty("next_page_uri")
    private String nextPageUri;
}
