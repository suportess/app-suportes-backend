package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.constraints.NotNull;

public record ConsultaSaldoItemRequest(
        @NotNull Long cdProduto,
        Long cdEstoque,
        Long cdFornecedor,
        String cdUnidade
) {}
