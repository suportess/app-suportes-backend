package br.tec.suportes.backend.dto.rag;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExemploRagResponse {
    private UUID id;
    private String pergunta;
    private String sqlGerado;
    private String descricao;
    private LocalDateTime criadoEm;
}
