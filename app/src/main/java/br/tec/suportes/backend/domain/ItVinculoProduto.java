package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "itvinculo_produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItVinculoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cd_sessao", nullable = false)
    private Long cdSessao;

    @Column(name = "nr_linha", nullable = false)
    private Integer nrLinha;

    @Column(name = "cd_produto_antigo", length = 50)
    private String cdProdutoAntigo;

    @Column(name = "ds_produto_antigo", length = 300)
    private String dsProdutoAntigo;

    @Column(name = "cd_produto_novo", length = 50)
    private String cdProdutoNovo;

    @Column(name = "ds_produto_novo", length = 300)
    private String dsProdutoNovo;

    @Column(name = "cd_procedimento_sus", length = 10)
    private String cdProcedimentoSus;
}
