package br.tec.suportes.backend.dto.transferencia;

import br.tec.suportes.backend.domain.TransferenciaConsignado;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransferenciaConsignadoDTO {

    private Long id;
    private String status;
    private Long cdMultiEmpresa;
    private Long cdEstoque;
    private String dsEstoque;
    private Long cdProdutoDev;
    private String dsProdutoDev;
    private Long cdEntPro;
    private String cdLoteDev;
    private String dtValidadeDev;
    private BigDecimal qtDevolvida;
    private Long cdProdutoEnt;
    private String dsProdutoEnt;
    private String cdLoteEnt;
    private String dtValidadeEnt;
    private BigDecimal qtEntrada;
    private String dtDevolucao;
    private Instant dtCriacao;
    private Instant dtConclusao;

    public static TransferenciaConsignadoDTO from(TransferenciaConsignado e) {
        TransferenciaConsignadoDTO d = new TransferenciaConsignadoDTO();
        d.id             = e.getId();
        d.status         = e.getStatus();
        d.cdMultiEmpresa = e.getCdMultiEmpresa();
        d.cdEstoque      = e.getCdEstoque();
        d.dsEstoque      = e.getDsEstoque();
        d.cdProdutoDev   = e.getCdProdutoDev();
        d.dsProdutoDev   = e.getDsProdutoDev();
        d.cdEntPro       = e.getCdEntPro();
        d.cdLoteDev      = e.getCdLoteDev();
        d.dtValidadeDev  = e.getDtValidadeDev();
        d.qtDevolvida    = e.getQtDevolvida();
        d.cdProdutoEnt   = e.getCdProdutoEnt();
        d.dsProdutoEnt   = e.getDsProdutoEnt();
        d.cdLoteEnt      = e.getCdLoteEnt();
        d.dtValidadeEnt  = e.getDtValidadeEnt();
        d.qtEntrada      = e.getQtEntrada();
        d.dtDevolucao    = e.getDtDevolucao();
        d.dtCriacao      = e.getDtCriacao();
        d.dtConclusao    = e.getDtConclusao();
        return d;
    }
}
