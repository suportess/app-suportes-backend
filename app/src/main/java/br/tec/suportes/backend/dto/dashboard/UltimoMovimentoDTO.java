package br.tec.suportes.backend.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UltimoMovimentoDTO {

    private Long cdMvtoEstoque;
    private String tpMvtoEstoque;
    private Long cdEstoque;
    private Instant dtMvtoEstoque;
    private Instant dtConclusao;
    private String dsObservacao;
}
