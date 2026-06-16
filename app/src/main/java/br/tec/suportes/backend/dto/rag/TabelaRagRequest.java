package br.tec.suportes.backend.dto.rag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TabelaRagRequest {

    @NotBlank(message = "nome é obrigatório")
    private String nome;

    private String schemaOra = "DBAMV";

    @NotBlank(message = "descricao é obrigatória")
    private String descricao;

    @Valid
    private List<ColunaRagRequest> colunas = List.of();
}
