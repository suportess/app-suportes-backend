package br.tec.suportes.backend.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardDTO {

    private long totalProdutos;
    private long movimentosHoje;
    private long entradasPendentes;
    private long itensSaldoBaixo;
    private List<UltimoMovimentoDTO> ultimosMovimentos;
}
