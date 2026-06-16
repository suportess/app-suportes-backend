package br.tec.suportes.backend.dto.rag;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TabelaRagResponse {
    private UUID id;
    private String nome;
    private String schemaOra;
    private String descricao;
    private LocalDateTime indexadoEm;
    private List<ColunaRagResponse> colunas;
}
