package br.tec.suportes.backend.dto.rag;

import lombok.Data;

import java.util.UUID;

@Data
public class ColunaRagResponse {
    private UUID id;
    private String nome;
    private String tipoDado;
    private String descricao;
    private Boolean nullable;
    private Boolean chavePrimaria;
}
