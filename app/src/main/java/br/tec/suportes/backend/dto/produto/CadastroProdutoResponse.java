package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CadastroProdutoResponse {

    @JsonProperty("ds_produto")
    private String dsProduto;

    @JsonProperty("cd_produto")
    private Long cdProduto;

    @JsonProperty("resultado_json")
    private String resultadoJson;

    public static CadastroProdutoResponse of(String dsProduto, Long cdProduto, String resultadoJson) {
        var r = new CadastroProdutoResponse();
        r.dsProduto = dsProduto;
        r.cdProduto = cdProduto;
        r.resultadoJson = resultadoJson;
        return r;
    }
}
