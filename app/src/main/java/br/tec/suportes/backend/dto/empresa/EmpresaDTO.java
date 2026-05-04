package br.tec.suportes.backend.dto.empresa;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EmpresaDTO {

    private Long id;
    private String nmEmpresa;
    private String dsRazaoSocial;
    private String nrCnpj;
    private String dsEmail;
    private String nrTelefone;
    private String apikey;
    private String dsHostPortal;
    private String snAtivo;
    private Instant dtCriacao;
    private Instant dtAtualizacao;
}
