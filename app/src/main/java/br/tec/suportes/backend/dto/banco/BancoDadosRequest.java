package br.tec.suportes.backend.dto.banco;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BancoDadosRequest(

        @NotBlank
        @Pattern(regexp = "postgres|oracle|mysql", message = "Driver inválido. Use: postgres, oracle ou mysql")
        String dsDriver,

        @NotBlank
        String dsHost,

        @NotNull
        @Min(1) @Max(65535)
        Integer nrPorta,

        @NotBlank
        String nmBanco,

        @NotBlank
        String nmUsuario,

        @NotBlank
        String dsSenha,

        @NotNull @Min(1)  @Max(100) Integer nrMaxOpenConns,
        @NotNull @Min(1)  @Max(100) Integer nrMaxIdleConns,
        @NotNull @Min(10) @Max(3600) Integer nrConnMaxLifetime,
        @NotNull @Min(10) @Max(3600) Integer nrConnMaxIdleTime
) {}
