package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transferencia_consignado", schema = "public")
public class TransferenciaConsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false)
    private String auth0Sub;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // -- Origem -----------------------------------------------------------------

    @Column(name = "cd_multi_empresa", nullable = false)
    private Long cdMultiEmpresa;

    @Column(name = "cd_estoque", nullable = false)
    private Long cdEstoque;

    @Column(name = "ds_estoque")
    private String dsEstoque;

    @Column(name = "cd_produto_dev", nullable = false)
    private Long cdProdutoDev;

    @Column(name = "ds_produto_dev")
    private String dsProdutoDev;

    @Column(name = "cd_ent_pro", nullable = false)
    private Long cdEntPro;

    @Column(name = "cd_lote_dev")
    private String cdLoteDev;

    @Column(name = "dt_validade_dev")
    private String dtValidadeDev;

    @Column(name = "qt_devolvida", nullable = false)
    private BigDecimal qtDevolvida;

    // -- Destino ----------------------------------------------------------------

    @Column(name = "cd_produto_ent", nullable = false)
    private Long cdProdutoEnt;

    @Column(name = "ds_produto_ent")
    private String dsProdutoEnt;

    @Column(name = "cd_lote_ent")
    private String cdLoteEnt;

    @Column(name = "dt_validade_ent")
    private String dtValidadeEnt;

    @Column(name = "qt_entrada", nullable = false)
    private BigDecimal qtEntrada;

    // -- Operação ---------------------------------------------------------------

    @Column(name = "dt_devolucao", nullable = false)
    private String dtDevolucao;

    // -- Auditoria --------------------------------------------------------------

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant dtCriacao;

    @Column(name = "dt_conclusao")
    private Instant dtConclusao;

    @PrePersist
    void prePersist() {
        if (dtCriacao == null) dtCriacao = Instant.now();
        if (status == null) status = "PENDENTE";
    }
}
