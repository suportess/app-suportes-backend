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
@Table(name = "conf_empresa", schema = "public")
public class ConfEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nm_empresa", nullable = false, length = 150)
    private String nmEmpresa;

    @Column(name = "ds_razao_social", length = 200)
    private String dsRazaoSocial;

    @Column(name = "nr_cnpj", length = 18)
    private String nrCnpj;

    @Column(name = "ds_email", length = 150)
    private String dsEmail;

    @Column(name = "nr_telefone", length = 20)
    private String nrTelefone;

    @Column(name = "apikey", nullable = false, length = 64)
    private String apikey;

    @Column(name = "ds_host_portal", length = 255)
    private String dsHostPortal;

    @Column(name = "sn_ativo", nullable = false, length = 1)
    private String snAtivo = "S";

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao", nullable = false)
    private Instant dtAtualizacao;
}
