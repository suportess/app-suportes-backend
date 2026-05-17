package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SaldoItemRequest(

        String produto,

        String estoque,

        String fornecedor,

        String unidade,

        @NotBlank(message = "Saldo é obrigatório.")
        String saldo,

        @NotBlank(message = "Movimento é obrigatório.")
        @Pattern(
                regexp = "ENTRADA|BAIXA|DEVOLUCAO|TRANSFERENCIA",
                message = "Movimento deve ser: ENTRADA, BAIXA, DEVOLUCAO ou TRANSFERENCIA."
        )
        String movimento
) {}
