package br.tec.suportes.backend.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TabelaDescricaoRagRequest {

    @NotBlank(message = "descricao é obrigatória")
    private String descricao;
}
