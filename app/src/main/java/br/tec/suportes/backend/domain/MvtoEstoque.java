package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "mvto_estoque", schema = "public")
public class MvtoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_mvto_estoque")
    private Long cdMvtoEstoque;

    @Column(name = "cd_estoque", nullable = false)
    private Long cdEstoque;

    @Column(name = "tp_mvto_estoque", nullable = false, length = 1)
    private String tpMvtoEstoque;

    @Column(name = "dt_mvto_estoque", nullable = false)
    private Instant dtMvtoEstoque;

    @Column(name = "cd_paciente")
    private Long cdPaciente;

    @Column(name = "cd_setor")
    private Long cdSetor;

    @Column(name = "cd_estoque_destino")
    private Long cdEstoqueDestino;

    @Column(name = "cd_empresa_destino")
    private Long cdEmpresaDestino;

    @Column(name = "cd_baixa")
    private Long cdBaixa;

    @Column(name = "ds_observacao", length = 500)
    private String dsObservacao;

    @Column(name = "dt_conclusao")
    private Instant dtConclusao;
}
