package br.tec.suportes.backend.dto.usuario;

import br.tec.suportes.backend.dto.empresa.EmpresaDTO;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UsuarioDTO {

    private Long id;
    private String auth0Sub;
    private String email;
    private String nmUsuario;
    private String picture;
    private String tipo;
    private Instant dtCriacao;
    private Instant dtAtualizacao;

    /** Empresa em que o usuário está atuando no momento. */
    private EmpresaDTO empresaAtiva;

    /** Empresas vinculadas ao usuário. */
    private List<UsuarioEmpresaDTO> empresas;
}

