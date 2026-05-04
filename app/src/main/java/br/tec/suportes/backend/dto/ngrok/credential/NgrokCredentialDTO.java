package br.tec.suportes.backend.dto.ngrok.credential;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokCredentialDTO {

    private String id;
    private String uri;

    @JsonProperty("created_at")
    private String createdAt;

    private String description;
    private String metadata;
    private String token;
    private List<String> acl;

    @JsonProperty("owner_id")
    private String ownerId;
}
