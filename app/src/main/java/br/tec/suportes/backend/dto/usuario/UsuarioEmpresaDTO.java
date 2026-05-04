package br.tec.suportes.backend.dto.usuario;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UsuarioEmpresaDTO {

    private Long id;
    private Long empresaId;
    private String nmEmpresa;
    private Instant dtVinculo;
}
