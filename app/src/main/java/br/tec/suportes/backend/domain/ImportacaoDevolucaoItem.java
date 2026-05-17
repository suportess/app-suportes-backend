package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_devolucao_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportacaoDevolucaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cd_sessao", nullable = false)
    private Long cdSessao;

    @Column(name = "nr_linha", nullable = false)
    private Integer nrLinha;

    // ── Dados da tela ─────────────────────────────────────────────────────────

    @Column(name = "cd_produto", length = 50)
    private String cdProduto;

    @Column(name = "ds_produto", length = 300)
    private String dsProduto;

    @Column(name = "cd_estoque", length = 50)
    private String cdEstoque;

    @Column(name = "ds_estoque", length = 200)
    private String dsEstoque;

    @Column(name = "cd_fornecedor", length = 50)
    private String cdFornecedor;

    @Column(name = "nm_fornecedor", length = 300)
    private String nmFornecedor;

    @Column(name = "cd_unidade", length = 50)
    private String cdUnidade;

    @Column(name = "ds_unidade", length = 100)
    private String dsUnidade;

    @Column(name = "qt_devolvida", precision = 18, scale = 4)
    private BigDecimal qtDevolvida;

    @Column(name = "tp_movimento", length = 30)
    private String tpMovimento;

    @Column(name = "cd_mot_dev")
    private Integer cdMotDev;

    @Column(name = "tp_devolucao", length = 1)
    @Builder.Default
    private String tpDevolucao = "Z";

    // ── Saldos MV exibidos na tela ────────────────────────────────────────────

    @Column(name = "vl_saldo_estoque", precision = 18, scale = 4)
    private BigDecimal vlSaldoEstoque;

    @Column(name = "vl_saldo_ficha", precision = 18, scale = 4)
    private BigDecimal vlSaldoFicha;

    @Column(name = "vl_saldo_consig_forn", precision = 18, scale = 4)
    private BigDecimal vlSaldoConsigForn;

    @Column(name = "vl_saldo_lotes", precision = 18, scale = 4)
    private BigDecimal vlSaldoLotes;

    // ── Resultado Oracle ──────────────────────────────────────────────────────

    @Column(name = "st_execucao", length = 10)
    @Builder.Default
    private String stExecucao = "pendente";

    @Column(name = "qt_total_devolvida", precision = 18, scale = 4)
    private BigDecimal qtTotalDevolvida;

    @Column(name = "qt_nao_atendida", precision = 18, scale = 4)
    private BigDecimal qtNaoAtendida;

    @Column(name = "ds_erro", length = 2000)
    private String dsErro;

    @Column(name = "json_resultado", columnDefinition = "TEXT")
    private String jsonResultado;

    @Column(name = "dt_execucao")
    private LocalDateTime dtExecucao;
}
