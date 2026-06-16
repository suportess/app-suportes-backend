package br.tec.suportes.backend.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ColunaRagRequest {

    @NotBlank(message = "nome da coluna é obrigatório")
    private String nome;

    private String tipoDado;
    private String descricao;
    private Boolean nullable = true;
    private Boolean chavePrimaria = false;
}
