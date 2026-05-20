package br.tec.suportes.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vinculo_produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VinculoProduto {

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

    @Column(name = "dt_vinculo", nullable = false)
    private LocalDateTime dtVinculo;

    @Column(name = "qt_vinculos", nullable = false)
    @Builder.Default
    private Integer qtVinculos = 0;

    @PrePersist
    void prePersist() {
        if (dtVinculo == null) dtVinculo = LocalDateTime.now();
    }
}
