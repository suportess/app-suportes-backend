package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportacaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false)
    private String auth0Sub;

    @Column(name = "cd_produto_mv", nullable = false)
    private Long cdProdutoMv;

    @Column(name = "ds_produto", nullable = false)
    private String dsProduto;

    @Column(name = "ds_comercial")
    private String dsComercial;

    @Column(name = "cd_especie")
    private Integer cdEspecie;

    @Column(name = "cd_classe")
    private Integer cdClasse;

    @Column(name = "cd_sub_cla")
    private Integer cdSubCla;

    @Column(name = "ds_sub_cla")
    private String dsSubCla;

    @Column(name = "cd_unidade")
    private String cdUnidade;

    @Column(name = "sn_lote", nullable = false)
    private String snLote;

    @Column(name = "sn_validade", nullable = false)
    private String snValidade;

    @Column(name = "dt_importacao", nullable = false)
    private LocalDateTime dtImportacao;

    @PrePersist
    void prePersist() {
        if (dtImportacao == null) dtImportacao = LocalDateTime.now();
    }
}
