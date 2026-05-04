package br.tec.suportes.backend.dto.ngrok.reserveddomain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NgrokReservedDomainListResponse {

    @JsonProperty("reserved_domains")
    private List<NgrokReservedDomainDTO> reservedDomains;

    private String uri;

    @JsonProperty("next_page_uri")
    private String nextPageUri;
}
