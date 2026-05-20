package br.tec.suportes.backend.dto.vinculo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VincularSusRequest {

    @NotEmpty(message = "A lista de vínculos não pode ser vazia.")
    private List<Item> vinculos;

    @Data
    public static class Item {
        @NotNull(message = "cd_produto_antigo é obrigatório.")
        private Long cdProdutoAntigo;

        @NotNull(message = "cd_produto_novo é obrigatório.")
        private Long cdProdutoNovo;
    }
}
