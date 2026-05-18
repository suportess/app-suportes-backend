package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueio_lote")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueioLote {

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

    @Column(name = "dt_bloqueio", nullable = false)
    private LocalDateTime dtBloqueio;

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
        if (dtBloqueio == null) dtBloqueio = LocalDateTime.now();
    }
}
