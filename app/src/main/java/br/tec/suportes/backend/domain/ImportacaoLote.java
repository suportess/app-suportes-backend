package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_lote")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportacaoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false)
    private String auth0Sub;

    @Column(name = "nm_usuario", length = 200)
    private String nmUsuario;

    @Column(name = "email_usuario", length = 150)
    private String emailUsuario;

    @Column(name = "nm_empresa", length = 150)
    private String nmEmpresa;

    @Column(name = "dt_importacao", nullable = false)
    private LocalDateTime dtImportacao;

    @Column(name = "qt_produtos", nullable = false)
    @Builder.Default
    private Integer qtProdutos = 0;

    @PrePersist
    void prePersist() {
        if (dtImportacao == null) dtImportacao = LocalDateTime.now();
    }
}
