package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "operacao_baixa_consignado_linha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBaixaConsignadoLinha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OperacaoBaixaConsignadoItem item;

    @Column(name = "cd_fornecedor", nullable = false)
    private Long cdFornecedor;

    @Column(name = "nm_fornecedor")
    private String nmFornecedor;

    @Column(name = "quantidade", nullable = false)
    private BigDecimal quantidade;

    @Column(name = "lote")
    private String lote;

    @Column(name = "validade")
    private String validade;
}
