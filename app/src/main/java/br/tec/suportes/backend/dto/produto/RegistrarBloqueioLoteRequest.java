package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RegistrarBloqueioLoteRequest {

    private List<ItemBloqueio> itens;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemBloqueio {

        @JsonProperty("cd_produto")
        private Long cdProduto;

        private String acao;

        @JsonProperty("sn_sucesso")
        private Boolean snSucesso;

        @JsonProperty("ds_erro")
        private String dsErro;
    }
}
