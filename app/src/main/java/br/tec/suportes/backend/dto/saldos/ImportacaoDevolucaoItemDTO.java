package br.tec.suportes.backend.dto.saldos;

import br.tec.suportes.backend.domain.ImportacaoDevolucaoItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ImportacaoDevolucaoItemDTO {

    @JsonProperty("id")                  private Long          id;
    @JsonProperty("cd_sessao")           private Long          cdSessao;
    @JsonProperty("nr_linha")            private Integer       nrLinha;
    @JsonProperty("cd_produto")          private String        cdProduto;
    @JsonProperty("ds_produto")          private String        dsProduto;
    @JsonProperty("cd_estoque")          private String        cdEstoque;
    @JsonProperty("ds_estoque")          private String        dsEstoque;
    @JsonProperty("cd_fornecedor")       private String        cdFornecedor;
    @JsonProperty("nm_fornecedor")       private String        nmFornecedor;
    @JsonProperty("cd_unidade")          private String        cdUnidade;
    @JsonProperty("ds_unidade")          private String        dsUnidade;
    @JsonProperty("qt_devolvida")        private BigDecimal    qtDevolvida;
    @JsonProperty("tp_movimento")        private String        tpMovimento;
    @JsonProperty("st_execucao")         private String        stExecucao;
    @JsonProperty("qt_total_devolvida")  private BigDecimal    qtTotalDevolvida;
    @JsonProperty("qt_nao_atendida")     private BigDecimal    qtNaoAtendida;
    @JsonProperty("ds_erro")             private String        dsErro;
    @JsonProperty("json_resultado")      private String        jsonResultado;
    @JsonProperty("dt_execucao")         private LocalDateTime dtExecucao;

    public static ImportacaoDevolucaoItemDTO of(ImportacaoDevolucaoItem e) {
        var dto = new ImportacaoDevolucaoItemDTO();
        dto.id               = e.getId();
        dto.cdSessao         = e.getCdSessao();
        dto.nrLinha          = e.getNrLinha();
        dto.cdProduto        = e.getCdProduto();
        dto.dsProduto        = e.getDsProduto();
        dto.cdEstoque        = e.getCdEstoque();
        dto.dsEstoque        = e.getDsEstoque();
        dto.cdFornecedor     = e.getCdFornecedor();
        dto.nmFornecedor     = e.getNmFornecedor();
        dto.cdUnidade        = e.getCdUnidade();
        dto.dsUnidade        = e.getDsUnidade();
        dto.qtDevolvida      = e.getQtDevolvida();
        dto.tpMovimento      = e.getTpMovimento();
        dto.stExecucao       = e.getStExecucao();
        dto.qtTotalDevolvida = e.getQtTotalDevolvida();
        dto.qtNaoAtendida    = e.getQtNaoAtendida();
        dto.dsErro           = e.getDsErro();
        dto.jsonResultado    = e.getJsonResultado();
        dto.dtExecucao       = e.getDtExecucao();
        return dto;
    }
}
