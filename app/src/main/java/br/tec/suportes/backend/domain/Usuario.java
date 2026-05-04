package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuario", schema = "public")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false, unique = true, length = 128)
    private String auth0Sub;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "nm_usuario", length = 200)
    private String nmUsuario;

    @Column(name = "picture", length = 500)
    private String picture;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo = "OPERADOR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_empresa_ativa")
    private ConfEmpresa empresaAtiva;

    @CreationTimestamp
    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private Instant dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao", nullable = false)
    private Instant dtAtualizacao;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioEmpresa> empresas = new ArrayList<>();
}
