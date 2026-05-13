package br.tec.suportes.backend.dto.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO para as unidades de medida (UNI_PRO) de um produto.
 * Retornado por GET /mv/api/produtos/{cd_produto}/unidades
 */
@Data
public class UniProDTO {

    @JsonProperty("CD_UNI_PRO")
    private String cdUniPro;

    @JsonProperty("CD_PRODUTO")
    private String cdProduto;

    @JsonProperty("CD_UNIDADE")
    private String cdUnidade;

    @JsonProperty("DS_UNIDADE")
    private String dsUnidade;

    @JsonProperty("VL_FATOR")
    private String vlFator;

    /** R = Referencial (fator 1), C = Complementar, E = Embalagem */
    @JsonProperty("TP_RELATORIOS")
    private String tpRelatorios;

    @JsonProperty("SN_ATIVO")
    private String snAtivo;

    @JsonProperty("SN_PRESCRICAO")
    private String snPrescricao;
}
