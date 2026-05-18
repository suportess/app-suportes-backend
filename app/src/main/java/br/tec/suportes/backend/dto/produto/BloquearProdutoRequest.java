package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BloquearProdutoRequest {

    @NotNull
    @JsonProperty("cd_produto")
    private Long cdProduto;

    /** "BLOQUEIO" ou "DESBLOQUEIO" */
    @NotNull
    @JsonProperty("acao")
    private String acao;
}
