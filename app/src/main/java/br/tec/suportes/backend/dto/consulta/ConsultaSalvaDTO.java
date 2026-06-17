package br.tec.suportes.backend.dto.consulta;

import br.tec.suportes.backend.domain.ConsultaSalva;

import java.time.Instant;

public record ConsultaSalvaDTO(Long id, String nome, String sqlTexto, Instant dtCriacao) {
    public static ConsultaSalvaDTO from(ConsultaSalva c) {
        return new ConsultaSalvaDTO(c.getId(), c.getNome(), c.getSqlTexto(), c.getDtCriacao());
    }
}
