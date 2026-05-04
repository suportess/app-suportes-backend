package br.tec.suportes.backend.dto.ngrok.reserveddomain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NgrokReservedDomainDTO {

    private String id;
    private String uri;

    @JsonProperty("created_at")
    private String createdAt;

    private String description;
    private String metadata;
    private String domain;
    private String region;

    @JsonProperty("cname_target")
    private String cnameTarget;

    @JsonProperty("acme_challenge_cname_target")
    private String acmeChallengeCnameTarget;
}
