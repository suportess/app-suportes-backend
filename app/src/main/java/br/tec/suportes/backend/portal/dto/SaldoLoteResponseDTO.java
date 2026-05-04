package br.tec.suportes.backend.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Envelope da resposta do portal para saldo-lote.
 * O portal envolve o resultado no alias do step: { "saldo": { ... } }
 */
@Data
public class SaldoLoteResponseDTO {

    @JsonProperty("saldo")
    private SaldoLoteDTO saldo;
}
