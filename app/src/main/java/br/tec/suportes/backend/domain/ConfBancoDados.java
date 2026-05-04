package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "conf_banco_dados", schema = "public")
public class ConfBancoDados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false, unique = true)
    private ConfEmpresa empresa;

    @Column(name = "ds_driver", nullable = false, length = 20)
    private String dsDriver;

    @Column(name = "ds_host", nullable = false, length = 255)
    private String dsHost;

    @Column(name = "nr_porta", nullable = false)
    private Integer nrPorta;

    @Column(name = "nm_banco", nullable = false, length = 150)
    private String nmBanco;

    @Column(name = "nm_usuario", nullable = false, length = 150)
    private String nmUsuario;

    @Column(name = "ds_senha", nullable = false, length = 255)
    private String dsSenha;

    @Column(name = "nr_max_open_conns", nullable = false)
    private Integer nrMaxOpenConns = 10;

    @Column(name = "nr_max_idle_conns", nullable = false)
    private Integer nrMaxIdleConns = 5;

    @Column(name = "nr_conn_max_lifetime", nullable = false)
    private Integer nrConnMaxLifetime = 90;

    @Column(name = "nr_conn_max_idle_time", nullable = false)
    private Integer nrConnMaxIdleTime = 30;

    @Column(name = "ds_portal_key", nullable = false, length = 100)
    private String dsPortalKey;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao", nullable = false)
    private Instant dtAtualizacao;
}
