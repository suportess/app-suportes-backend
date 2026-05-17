package br.tec.suportes.backend.dto.saldos;

import br.tec.suportes.backend.domain.ImportacaoDevolucao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImportacaoSaldosHistoricoDTO {

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

    @JsonProperty("qt_itens")
    private Integer qtItens;

    @JsonProperty("qt_sucesso")
    private Integer qtSucesso;

    @JsonProperty("qt_erro")
    private Integer qtErro;

    public static ImportacaoSaldosHistoricoDTO of(ImportacaoDevolucao e) {
        var dto = new ImportacaoSaldosHistoricoDTO();
        dto.id           = e.getId();
        dto.nmUsuario    = e.getNmUsuario();
        dto.emailUsuario = e.getEmailUsuario();
        dto.nmEmpresa    = e.getNmEmpresa();
        dto.dtImportacao = e.getDtImportacao();
        dto.qtItens      = e.getQtItens();
        dto.qtSucesso    = e.getQtSucesso();
        dto.qtErro       = e.getQtErro();
        return dto;
    }
}
