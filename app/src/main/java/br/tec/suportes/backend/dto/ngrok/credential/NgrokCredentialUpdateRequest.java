package br.tec.suportes.backend.dto.ngrok.credential;

import lombok.Data;

import java.util.List;

@Data
public class NgrokCredentialUpdateRequest {

    private String description;
    private String metadata;
    private List<String> acl;
}
