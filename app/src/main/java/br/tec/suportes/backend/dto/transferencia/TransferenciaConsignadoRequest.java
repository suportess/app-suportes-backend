package br.tec.suportes.backend.dto.transferencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferenciaConsignadoRequest {

    @NotNull
    private Long cdMultiEmpresa;

    @NotNull
    private Long cdEstoque;

    private String dsEstoque;

    @NotNull
    private Long cdProdutoDev;

    private String dsProdutoDev;

    @NotNull
    private Long cdEntPro;

    private String cdLoteDev;

    private String dtValidadeDev;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal qtDevolvida;

    @NotNull
    private Long cdProdutoEnt;

    private String dsProdutoEnt;

    private String cdLoteEnt;

    private String dtValidadeEnt;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal qtEntrada;

    @NotBlank
    private String dtDevolucao;
}
