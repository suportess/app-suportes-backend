package br.tec.suportes.backend.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProdutoRequest {

    @NotBlank(message = "nm_produto é obrigatório")
    @Size(max = 100, message = "nm_produto deve ter no máximo 100 caracteres")
    private String nmProduto;

    @NotBlank
    @Pattern(regexp = "^[SN]$", message = "sn_consignado deve ser S ou N")
    private String snConsignado;

    @NotBlank
    @Pattern(regexp = "^[SN]$", message = "sn_lote deve ser S ou N")
    private String snLote;

    @NotBlank
    @Pattern(regexp = "^[SN]$", message = "sn_validade deve ser S ou N")
    private String snValidade;
}
