package br.tec.suportes.backend.dto.produto;

import br.tec.suportes.backend.domain.ImportacaoLote;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportacaoLoteDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nm_usuario")
    private String nmUsuario;

    @JsonProperty("email_usuario")
    private String emailUsuario;

    @JsonProperty("nm_empresa")
    private String nmEmpresa;

    @JsonProperty("dt_importacao")
    private LocalDateTime dtImportacao;

    @JsonProperty("qt_produtos")
    private Integer qtProdutos;

    public static ImportacaoLoteDTO of(ImportacaoLote e) {
        var dto = new ImportacaoLoteDTO();
        dto.id            = e.getId();
        dto.nmUsuario     = e.getNmUsuario();
        dto.emailUsuario  = e.getEmailUsuario();
        dto.nmEmpresa     = e.getNmEmpresa();
        dto.dtImportacao  = e.getDtImportacao();
        dto.qtProdutos    = e.getQtProdutos();
        return dto;
    }
}
