package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "est_pro", schema = "public")
public class EstPro {

    @EmbeddedId
    private EstProId id;

    @Column(name = "qt_estoque_atual", nullable = false)
    private BigDecimal qtEstoqueAtual;

    @Embeddable
    @Getter
    @Setter
    public static class EstProId implements java.io.Serializable {

        @Column(name = "cd_estoque")
        private Long cdEstoque;

        @Column(name = "cd_produto")
        private Long cdProduto;
    }
}
