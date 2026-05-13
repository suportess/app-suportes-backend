package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO para as empresas vinculadas a um produto (EMPRESA_PRODUTO).
 * Retornado por GET /mv/api/produtos/{cd_produto}/empresas
 */
@Data
public class EmpresaProdutoDTO {

    @JsonProperty("CD_PRODUTO")
    private String cdProduto;

    @JsonProperty("CD_MULTI_EMPRESA")
    private Integer cdMultiEmpresa;

    @JsonProperty("QT_ESTOQUE_ATUAL")
    private Double qtEstoqueAtual;

    @JsonProperty("QT_ESTOQUE_MINIMO")
    private Double qtEstoqueMinimo;

    @JsonProperty("QT_ESTOQUE_MAXIMO")
    private Double qtEstoqueMaximo;

    @JsonProperty("SN_ATIVO")
    private String snAtivo;

    @JsonProperty("SN_PADRONIZADO")
    private String snPadronizado;

    @JsonProperty("SN_LOTE")
    private String snLote;

    @JsonProperty("SN_CONTROLE_VALIDADE")
    private String snControleValidade;

    @JsonProperty("VL_CUSTO_MEDIO")
    private String vlCustoMedio;

    @JsonProperty("VL_PRECO_DE_VENDA")
    private String vlPrecoDeVenda;
}
