package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operacao_baixa_consignado_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBaixaConsignadoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacao_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OperacaoBaixaConsignado operacao;

    @Column(name = "cd_produto", nullable = false)
    private Long cdProduto;

    @Column(name = "ds_produto")
    private String dsProduto;

    @Column(name = "sn_lote", nullable = false)
    private String snLote;

    @Column(name = "sn_validade", nullable = false)
    private String snValidade;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OperacaoBaixaConsignadoLinha> linhas = new ArrayList<>();
}
