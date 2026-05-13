package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CadastroProdutoResponse {

    @JsonProperty("ds_produto")
    private String dsProduto;

    @JsonProperty("cd_produto")
    private Long cdProduto;

    public static CadastroProdutoResponse of(String dsProduto, Long cdProduto) {
        var r = new CadastroProdutoResponse();
        r.dsProduto = dsProduto;
        r.cdProduto = cdProduto;
        return r;
    }
}
