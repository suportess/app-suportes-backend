package br.tec.suportes.backend.dto.produto;

import br.tec.suportes.backend.domain.BloqueioLote;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BloqueioLoteDTO {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("nm_usuario")
    private final String nmUsuario;

    @JsonProperty("email_usuario")
    private final String emailUsuario;

    @JsonProperty("nm_empresa")
    private final String nmEmpresa;

    @JsonProperty("dt_bloqueio")
    private final LocalDateTime dtBloqueio;

    @JsonProperty("qt_itens")
    private final Integer qtItens;

    @JsonProperty("qt_sucesso")
    private final Integer qtSucesso;

    @JsonProperty("qt_erro")
    private final Integer qtErro;

    private BloqueioLoteDTO(BloqueioLote lote) {
        this.id          = lote.getId();
        this.nmUsuario   = lote.getNmUsuario();
        this.emailUsuario= lote.getEmailUsuario();
        this.nmEmpresa   = lote.getNmEmpresa();
        this.dtBloqueio  = lote.getDtBloqueio();
        this.qtItens     = lote.getQtItens();
        this.qtSucesso   = lote.getQtSucesso();
        this.qtErro      = lote.getQtErro();
    }

    public static BloqueioLoteDTO of(BloqueioLote lote) {
        return new BloqueioLoteDTO(lote);
    }
}
