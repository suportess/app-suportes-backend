package br.tec.suportes.backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "auth0Sub é obrigatório")
    @Size(max = 128)
    private String auth0Sub;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Size(max = 150)
    private String email;

    @Size(max = 200)
    private String nmUsuario;

    @Size(max = 500)
    private String picture;
}
