package br.tec.suportes.backend.dto.consulta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultaSalvaRequest(
    @NotBlank @Size(max = 200) String nome,
    @NotBlank String sqlTexto
) {}
