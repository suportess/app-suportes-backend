package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record TransferenciaRequest(

        @NotNull(message = "cabecas é obrigatório.")
        @NotEmpty(message = "cabecas não pode ser vazio.")
        @Valid
        List<CabecaItem> cabecas,

        @NotNull(message = "itens é obrigatório.")
        @NotEmpty(message = "itens não pode ser vazio.")
        @Valid
        List<FilhoItem> itens,

        // ── Metadata para persistência no histórico (todos opcionais) ──────
        Long    cdSessao,
        Integer nrLinha,
        String  cdProduto,
        String  dsProduto,
        String  cdEstoque,
        String  dsEstoque,
        String  cdFornecedor,
        String  nmFornecedor,
        String  cdUnidade,
        String  dsUnidade

) {

    /** Produto a devolver (cabeça da transferência) */
    public record CabecaItem(

            @NotNull(message = "cabecas[].cdProduto é obrigatório.")
            Long cdProduto,

            @NotNull(message = "cabecas[].qtDev é obrigatório.")
            @Positive(message = "cabecas[].qtDev deve ser positivo.")
            BigDecimal qtDev,

            @NotNull(message = "cabecas[].cdEstoque é obrigatório.")
            Long cdEstoque,

            /** null = auto (todos os fornecedores com saldo em EST_CONSIG_FORN) */
            Long cdFornecedor,

            @NotNull(message = "cabecas[].cdMotDev é obrigatório.")
            Integer cdMotDev,

            // ── Metadata para persistência no histórico (opcionais) ──────
            Integer nrLinha,
            String  dsProduto,
            String  dsEstoque,
            String  nmFornecedor,
            String  cdUnidade,
            String  dsUnidade
    ) {}

    /** Produto a receber entrada (filho da transferência) */
    public record FilhoItem(

            @NotNull(message = "itens[].cdProduto é obrigatório.")
            Long cdProduto,

            @NotNull(message = "itens[].qtEntrada é obrigatório.")
            @Positive(message = "itens[].qtEntrada deve ser positivo.")
            BigDecimal qtEntrada,

            @NotNull(message = "itens[].cdEstoque é obrigatório.")
            Long cdEstoque,

            @NotNull(message = "itens[].cdFornecedor é obrigatório.")
            Long cdFornecedor,

            @NotNull(message = "itens[].cdUnidade é obrigatório.")
            String cdUnidade,

            // ── Metadata para persistência no histórico (opcionais) ──────
            Integer nrLinha,
            String  dsProduto,
            String  dsEstoque,
            String  nmFornecedor,
            String  dsUnidade
    ) {}
}
