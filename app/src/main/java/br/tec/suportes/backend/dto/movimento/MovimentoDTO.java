package br.tec.suportes.backend.dto.movimento;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class MovimentoDTO {

    @JsonProperty("cd_mvto_estoque")
    private Long cdMvtoEstoque;

    @JsonProperty("cd_estoque")
    private Long cdEstoque;

    @JsonProperty("tp_mvto_estoque")
    private String tpMvtoEstoque;

    @JsonProperty("dt_mvto_estoque")
    private Instant dtMvtoEstoque;

    @JsonProperty("dt_conclusao")
    private Instant dtConclusao;

    @JsonProperty("cd_paciente")
    private Long cdPaciente;

    @JsonProperty("cd_setor")
    private Long cdSetor;

    @JsonProperty("cd_estoque_destino")
    private Long cdEstoqueDestino;

    @JsonProperty("cd_empresa_destino")
    private Long cdEmpresaDestino;

    @JsonProperty("cd_baixa")
    private Long cdBaixa;

    @JsonProperty("ds_observacao")
    private String dsObservacao;
}
