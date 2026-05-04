package br.tec.suportes.backend.dto.ngrok.reserveddomain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NgrokReservedDomainCreateRequest {

    @NotBlank(message = "domain é obrigatório")
    private String domain;

    private String description;
    private String metadata;
    private String region;
}
