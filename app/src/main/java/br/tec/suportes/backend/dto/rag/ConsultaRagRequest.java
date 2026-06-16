package br.tec.suportes.backend.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConsultaRagRequest {

    @NotBlank(message = "pergunta é obrigatória")
    private String pergunta;

    private int topK = 5;
}
