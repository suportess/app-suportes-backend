package br.tec.suportes.backend.dto.ngrok.credential;

import lombok.Data;

import java.util.List;

@Data
public class NgrokCredentialCreateRequest {

    private String description;
    private String metadata;
    private List<String> acl;
}
