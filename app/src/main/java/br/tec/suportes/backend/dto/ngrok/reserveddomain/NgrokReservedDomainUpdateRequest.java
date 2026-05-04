package br.tec.suportes.backend.dto.ngrok.reserveddomain;

import lombok.Data;

@Data
public class NgrokReservedDomainUpdateRequest {

    private String description;
    private String metadata;
}
