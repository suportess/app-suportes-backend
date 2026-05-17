package br.tec.suportes.backend.dto.saldos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ImportarDevolucaoItemRequest(

        /** ID da sessão já criada (null = criar nova sessão) */
        Long cdSessao,

        /** Número da linha na planilha (1-based) */
        @NotNull(message = "nrLinha é obrigatório.")
        Integer nrLinha,

        // ── Dados da tela ─────────────────────────────────────────────────
        @NotBlank(message = "cdProduto é obrigatório.")
        String cdProduto,

        String dsProduto,
        String cdEstoque,
        String dsEstoque,
        String cdFornecedor,
        String nmFornecedor,
        String cdUnidade,
        String dsUnidade,

        @NotNull(message = "qtDevolvida é obrigatório.")
        @Positive(message = "qtDevolvida deve ser positivo.")
        BigDecimal qtDevolvida,

        /** DEVOLUCAO | ENTRADA | BAIXA | TRANSFERENCIA */
        @NotBlank(message = "tpMovimento é obrigatório.")
        String tpMovimento,

        /** Código do motivo de devolução (default 8 = Produto não utilizado) */
        Integer cdMotDev,

        /** C = Consignado/Compra, Z = Devolução direta (default C) */
        String tpDevolucao,

        // ── Saldos MV já consultados e exibidos na tela ──────────────────
        BigDecimal saldoEstoque,
        BigDecimal saldoFicha,
        BigDecimal saldoConsigForn,
        BigDecimal saldoLotes
) {}
