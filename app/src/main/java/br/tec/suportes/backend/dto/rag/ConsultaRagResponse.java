package br.tec.suportes.backend.dto.rag;

import lombok.Data;

@Data
public class ConsultaRagResponse {
    private String pergunta;
    private String sql;
}
