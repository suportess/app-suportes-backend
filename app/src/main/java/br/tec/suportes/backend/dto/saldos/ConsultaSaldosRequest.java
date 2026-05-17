package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ConsultaSaldosRequest(
        @NotEmpty @Valid List<ConsultaSaldoItemRequest> itens
) {}
