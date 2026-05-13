package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "operacao_baixa_consignado_origem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBaixaConsignadoOrigem {

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

    @Column(name = "qt_estoque_atual")
    private BigDecimal qtEstoqueAtual;
}
