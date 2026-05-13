package br.tec.suportes.backend.dto.produto;

import br.tec.suportes.backend.domain.ImportacaoProduto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportacaoProdutoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("cd_produto_mv")
    private Long cdProdutoMv;

    @JsonProperty("ds_produto")
    private String dsProduto;

    @JsonProperty("ds_comercial")
    private String dsComercial;

    @JsonProperty("cd_especie")
    private Integer cdEspecie;

    @JsonProperty("cd_classe")
    private Integer cdClasse;

    @JsonProperty("cd_sub_cla")
    private Integer cdSubCla;

    @JsonProperty("ds_sub_cla")
    private String dsSubCla;

    @JsonProperty("cd_unidade")
    private String cdUnidade;

    @JsonProperty("sn_lote")
    private String snLote;

    @JsonProperty("sn_validade")
    private String snValidade;

    @JsonProperty("dt_importacao")
    private LocalDateTime dtImportacao;

    public static ImportacaoProdutoDTO of(ImportacaoProduto e) {
        var dto = new ImportacaoProdutoDTO();
        dto.id            = e.getId();
        dto.cdProdutoMv   = e.getCdProdutoMv();
        dto.dsProduto     = e.getDsProduto();
        dto.dsComercial   = e.getDsComercial();
        dto.cdEspecie     = e.getCdEspecie();
        dto.cdClasse      = e.getCdClasse();
        dto.cdSubCla      = e.getCdSubCla();
        dto.dsSubCla      = e.getDsSubCla();
        dto.cdUnidade     = e.getCdUnidade();
        dto.snLote        = e.getSnLote();
        dto.snValidade    = e.getSnValidade();
        dto.dtImportacao  = e.getDtImportacao();
        return dto;
    }
}
