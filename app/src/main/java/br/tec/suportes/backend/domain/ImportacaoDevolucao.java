package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_devolucao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportacaoDevolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_sub", nullable = false, length = 255)
    private String auth0Sub;

    @Column(name = "nm_usuario", length = 200)
    private String nmUsuario;

    @Column(name = "email_usuario", length = 150)
    private String emailUsuario;

    @Column(name = "nm_empresa", length = 150)
    private String nmEmpresa;

    @Column(name = "dt_importacao", nullable = false)
    private LocalDateTime dtImportacao;

    @Column(name = "qt_itens", nullable = false)
    @Builder.Default
    private Integer qtItens = 0;

    @Column(name = "qt_sucesso", nullable = false)
    @Builder.Default
    private Integer qtSucesso = 0;

    @Column(name = "qt_erro", nullable = false)
    @Builder.Default
    private Integer qtErro = 0;

    @PrePersist
    void prePersist() {
        if (dtImportacao == null) dtImportacao = LocalDateTime.now();
    }
}
