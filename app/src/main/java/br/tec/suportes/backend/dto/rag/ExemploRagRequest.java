package br.tec.suportes.backend.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExemploRagRequest {

    @NotBlank(message = "pergunta é obrigatória")
    private String pergunta;

    @NotBlank(message = "sqlGerado é obrigatório")
    private String sqlGerado;

    private String descricao;
}
