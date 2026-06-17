package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "consulta_salva", schema = "public")
public class ConsultaSalva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false)
    private String auth0Sub;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "sql_texto", nullable = false, columnDefinition = "TEXT")
    private String sqlTexto;

    @Column(name = "dt_criacao", nullable = false)
    private Instant dtCriacao = Instant.now();

    @Column(name = "dt_atualizacao")
    private Instant dtAtualizacao;
}
