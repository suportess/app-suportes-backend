package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "produto", schema = "public")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_produto")
    private Long cdProduto;

    @Column(name = "nm_produto", nullable = false, length = 100)
    private String nmProduto;

    @Column(name = "sn_consignado", nullable = false, length = 1)
    private String snConsignado;

    @Column(name = "sn_lote", nullable = false, length = 1)
    private String snLote;

    @Column(name = "sn_validade", nullable = false, length = 1)
    private String snValidade;
}
