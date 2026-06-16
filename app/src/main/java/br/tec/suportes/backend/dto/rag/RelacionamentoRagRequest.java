package br.tec.suportes.backend.dto.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RelacionamentoRagRequest {

    @NotNull(message = "tabelaOrigemId é obrigatório")
    private UUID tabelaOrigemId;

    @NotBlank(message = "colunaOrigem é obrigatória")
    private String colunaOrigem;

    @NotNull(message = "tabelaDestinoId é obrigatório")
    private UUID tabelaDestinoId;

    @NotBlank(message = "colunaDestino é obrigatória")
    private String colunaDestino;

    private String descricao;
}
