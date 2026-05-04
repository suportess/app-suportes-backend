package br.tec.suportes.backend.dto.ngrok.credential;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokCredentialListResponse {

    private List<NgrokCredentialDTO> credentials;
    private String uri;

    @JsonProperty("next_page_uri")
    private String nextPageUri;
}
