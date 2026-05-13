package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operacao_baixa_consignado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBaixaConsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false)
    private String auth0Sub;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cd_multi_empresa", nullable = false)
    private Long cdMultiEmpresa;

    @Column(name = "cd_estoque", nullable = false)
    private Long cdEstoque;

    @Column(name = "ds_estoque")
    private String dsEstoque;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dtCriacao;

    @Column(name = "dt_conclusao")
    private LocalDateTime dtConclusao;

    @OneToMany(mappedBy = "operacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OperacaoBaixaConsignadoOrigem> origens = new ArrayList<>();

    @OneToMany(mappedBy = "operacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OperacaoBaixaConsignadoItem> itens = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (status == null) status = "PENDENTE";
        if (dtCriacao == null) dtCriacao = LocalDateTime.now();
    }
}
