package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.MvtoEstoque;
import br.tec.suportes.backend.dto.dashboard.DashboardDTO;
import br.tec.suportes.backend.dto.dashboard.UltimoMovimentoDTO;
import br.tec.suportes.backend.repository.EstProRepository;
import br.tec.suportes.backend.repository.MvtoEstoqueRepository;
import br.tec.suportes.backend.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final MvtoEstoqueRepository mvtoEstoqueRepository;
    private final EstProRepository estProRepository;

    @Transactional(readOnly = true)
    public DashboardDTO resumo() {
        Instant inicioDia = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant fimDia = inicioDia.plus(1, ChronoUnit.DAYS);

        long totalProdutos = produtoRepository.count();
        long movimentosHoje = mvtoEstoqueRepository.countByDia(inicioDia, fimDia);
        long entradasPendentes = mvtoEstoqueRepository.countByDtConclusaoIsNull();
        long itensSaldoBaixo = estProRepository.countSaldoBaixo();

        List<UltimoMovimentoDTO> ultimos = mvtoEstoqueRepository
                .findTop10ByOrderByDtMvtoEstoqueDesc()
                .stream()
                .map(this::toUltimoMovimentoDTO)
                .toList();

        return DashboardDTO.builder()
                .totalProdutos(totalProdutos)
                .movimentosHoje(movimentosHoje)
                .entradasPendentes(entradasPendentes)
                .itensSaldoBaixo(itensSaldoBaixo)
                .ultimosMovimentos(ultimos)
                .build();
    }

    private UltimoMovimentoDTO toUltimoMovimentoDTO(MvtoEstoque m) {
        return UltimoMovimentoDTO.builder()
                .cdMvtoEstoque(m.getCdMvtoEstoque())
                .tpMvtoEstoque(m.getTpMvtoEstoque())
                .cdEstoque(m.getCdEstoque())
                .dtMvtoEstoque(m.getDtMvtoEstoque())
                .dtConclusao(m.getDtConclusao())
                .dsObservacao(m.getDsObservacao())
                .build();
    }
}
