package br.tec.suportes.backend.dto.empresa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaRequest {

    @NotBlank(message = "nmEmpresa é obrigatório")
    @Size(max = 150, message = "nmEmpresa deve ter no máximo 150 caracteres")
    private String nmEmpresa;

    @Size(max = 200)
    private String dsRazaoSocial;

    @Pattern(regexp = "^(\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2})?$",
             message = "nrCnpj inválido")
    private String nrCnpj;

    @Email(message = "dsEmail inválido")
    @Size(max = 150)
    private String dsEmail;

    @Size(max = 20)
    private String nrTelefone;

    @Size(max = 255, message = "dsHostPortal deve ter no máximo 255 caracteres")
    private String dsHostPortal;
}
