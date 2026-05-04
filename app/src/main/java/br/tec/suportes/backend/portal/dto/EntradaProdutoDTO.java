package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EntradaProdutoDTO {

    @JsonProperty("CD_ENT_PRO")
    private String cdEntPro;

    @JsonProperty("CD_FORNECEDOR")
    private Long cdFornecedor;

    @JsonProperty("CD_LOTE")
    private String cdLote;

    @JsonProperty("DT_VALIDADE")
    private String dtValidade;

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;

    @JsonProperty("DT_ENTRADA")
    private String dtEntrada;

    @JsonProperty("NM_FORNECEDOR")
    private String nmFornecedor;

    @JsonProperty("QT_DISPONIVEL")
    private Double qtDisponivel;

    @JsonProperty("DS_UNIDADE")
    private String dsUnidade;

    @JsonProperty("CD_UNI_PRO")
    private String cdUniPro;
}
