package br.tec.suportes.backend.dto.movimento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MovimentoRequest {

    @NotNull(message = "cd_estoque é obrigatório")
    private Long cdEstoque;

    @NotNull(message = "tp_mvto_estoque é obrigatório")
    @Pattern(regexp = "^[SPCDTEB]$", message = "tp_mvto_estoque deve ser S, P, C, D, T, E ou B")
    private String tpMvtoEstoque;

    private Long cdPaciente;
    private Long cdSetor;
    private Long cdEstoqueDestino;
    private Long cdEmpresaDestino;
    private Long cdBaixa;
    private Long cdCirurgia;
    private String dsObservacao;
}
