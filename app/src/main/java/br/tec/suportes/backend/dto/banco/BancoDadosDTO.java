package br.tec.suportes.backend.dto.banco;

import java.time.Instant;

public record BancoDadosDTO(
        Long    id,
        String  dsDriver,
        String  dsHost,
        Integer nrPorta,
        String  nmBanco,
        String  nmUsuario,
        Integer nrMaxOpenConns,
        Integer nrMaxIdleConns,
        Integer nrConnMaxLifetime,
        Integer nrConnMaxIdleTime,
        Instant dtCriacao,
        Instant dtAtualizacao
) {}
