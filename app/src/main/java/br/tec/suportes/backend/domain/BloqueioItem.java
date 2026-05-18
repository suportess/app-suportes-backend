package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueio_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_lote", nullable = false)
    private BloqueioLote lote;

    @Column(name = "cd_produto", nullable = false)
    private Long cdProduto;

    @Column(name = "acao", nullable = false, length = 15)
    private String acao;

    @Column(name = "sn_sucesso", nullable = false)
    @Builder.Default
    private Boolean snSucesso = false;

    @Column(name = "ds_erro", columnDefinition = "TEXT")
    private String dsErro;

    @Column(name = "dt_operacao", nullable = false)
    private LocalDateTime dtOperacao;

    @PrePersist
    void prePersist() {
        if (dtOperacao == null) dtOperacao = LocalDateTime.now();
    }
}
