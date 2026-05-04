package br.tec.suportes.backend.dto.ngrok.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NgrokEndpointDTO {

    private String id;
    private String uri;
    private String name;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String region;
    private String proto;
    private String url;

    @JsonProperty("public_url")
    private String publicUrl;

    private String hostport;

    @JsonProperty("upstream_url")
    private String upstreamUrl;

    @JsonProperty("upstream_protocol")
    private String upstreamProtocol;

    private String type;
    private String description;
    private String metadata;
}
