package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO que espelha as colunas retornadas pelo comando listar-produtos-mv
 * e pela rota GET /mv/api/produtos do portal.
 *
 * As chaves são UPPERCASE porque o portal repassa os nomes de coluna Oracle
 * diretamente no JSON (ex: "CD_PRODUTO", "DS_PRODUTO").
 */
@Data
public class ProdutoDTO {

    @JsonProperty("CD_PRODUTO")
    private String cdProduto;

    @JsonProperty("DS_PRODUTO")
    private String dsProduto;

    @JsonProperty("DS_COMERCIAL")
    private String dsComercial;

    @JsonProperty("SN_LOTE")
    private String snLote;

    /** Alias de SN_CONTROLE_VALIDADE definido no SQL da migration V34. */
    @JsonProperty("SN_VALIDADE")
    private String snValidade;

    @JsonProperty("SN_CONSIGNADO")
    private String snConsignado;

    @JsonProperty("SN_MEDICAMENTO")
    private String snMedicamento;

    @JsonProperty("TP_SEXO")
    private String tpSexo;

    @JsonProperty("TP_ATIVO")
    private String tpAtivo;

    @JsonProperty("DS_SUB_CLA")
    private String dsSubCla;

    @JsonProperty("CD_ESPECIE")
    private Integer cdEspecie;

    @JsonProperty("CD_CLASSE")
    private Integer cdClasse;

    @JsonProperty("CD_SUB_CLA")
    private Integer cdSubCla;

    /** Unidade de medida referencial (TP_RELATORIOS = 'R') do produto. */
    @JsonProperty("DS_UNIDADE_REF")
    private String dsUnidadeRef;
}
