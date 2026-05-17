package br.tec.suportes.backend.dto.saldos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImportarDevolucaoItemResponse(

        /** PK gerada em importacao_devolucao_item */
        Long cdItem,

        /** PK da sessão importacao_devolucao */
        Long cdSessao,

        /** "ok" | "erro" */
        String status,

        /** Total efetivamente devolvido no Oracle */
        BigDecimal qtTotalDevolvida,

        /** Quantidade não atendida (devolução parcial) */
        BigDecimal qtNaoAtendida,

        /** Mensagem de aviso (devolução parcial) ou mensagem de erro */
        String mensagem,

        /** Array de devoluções Oracle (DEV_FOR + itens por lote) */
        List<Map<String, Object>> devolucoes,

        /** Dados Oracle da entrada (ENT_PRO + ITENT_PRO + ITLOT_ENT) */
        Map<String, Object> entrada
) {}
