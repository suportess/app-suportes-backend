package br.tec.suportes.backend.dto.vinculo;

import br.tec.suportes.backend.domain.ItVinculoProduto;

public record ItVinculoProdutoDTO(
        Long   id,
        Integer nrLinha,
        String cdProdutoAntigo,
        String dsProdutoAntigo,
        String cdProdutoNovo,
        String dsProdutoNovo,
        String cdProcedimentoSus
) {
    public static ItVinculoProdutoDTO of(ItVinculoProduto it) {
        return new ItVinculoProdutoDTO(
                it.getId(),
                it.getNrLinha(),
                it.getCdProdutoAntigo(),
                it.getDsProdutoAntigo(),
                it.getCdProdutoNovo(),
                it.getDsProdutoNovo(),
                it.getCdProcedimentoSus()
        );
    }
}
