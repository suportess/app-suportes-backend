package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ImportacaoSaldosRequest(

        @NotEmpty(message = "A lista de itens não pode estar vazia.")
        @Valid
        List<SaldoItemRequest> itens
) {}
