package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.saldos.TransferenciaRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportacaoTransferenciaService {

    private final PortalClient    portalClient;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper    objectMapper;

    /**
     * Executa a transferência: devolução das cabeças + entrada dos filhos em uma única
     * transação Oracle atômica (ROLLBACK total em caso de qualquer erro).
     *
     * @return mapa com o resultado Oracle (status, progresso, fase1_devolucoes, fase2_entradas)
     */
    public Map<String, Object> processar(String auth0Sub, TransferenciaRequest req) {

        Creds creds = resolveCreds(auth0Sub);

        // Serializa listas para strings JSON que o Oracle lê via JSON_TABLE
        String cabecasJson;
        String itensJson;
        try {
            List<Map<String, Object>> cabecasMaps = req.cabecas().stream()
                    .map(c -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("cd_produto",    c.cdProduto());
                        m.put("qt_dev",        c.qtDev());
                        m.put("cd_estoque",    c.cdEstoque());
                        m.put("cd_fornecedor", c.cdFornecedor()); // null permitido
                        m.put("cd_mot_dev",    c.cdMotDev());
                        return m;
                    })
                    .toList();

            List<Map<String, Object>> itensMaps = req.itens().stream()
                    .map(i -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("cd_produto",    i.cdProduto());
                        m.put("qt_entrada",    i.qtEntrada());
                        m.put("cd_estoque",    i.cdEstoque());
                        m.put("cd_fornecedor", i.cdFornecedor());
                        m.put("cd_unidade",    i.cdUnidade());
                        m.put("cd_lote",       i.cdLote());
                        m.put("dt_validade",   i.dtValidade());
                        return m;
                    })
                    .toList();

            cabecasJson = objectMapper.writeValueAsString(cabecasMaps);
            itensJson   = objectMapper.writeValueAsString(itensMaps);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar payload: " + e.getMessage(), e);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("cabecas_json", cabecasJson);
        body.put("itens_json",   itensJson);

        try {
            Map<String, Object> portalResponse = portalClient.transferenciaProduto(
                    creds.host(), creds.apikey(), body);

            // Portal retorna {"transferencia": {"result_out": "{...}"}}
            @SuppressWarnings("unchecked")
            Map<String, Object> resultado = (Map<String, Object>) portalResponse.get("transferencia");
            String resultJson = resultado != null ? (String) resultado.get("result_out") : null;

            if (resultJson == null || resultJson.isBlank()) {
                throw new RuntimeException("Portal retornou resposta vazia.");
            }

            return objectMapper.readValue(resultJson, new TypeReference<Map<String, Object>>() {});

        } catch (Exception ex) {
            log.error("Erro ao executar transferência Oracle: {}", ex.getMessage());
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            return Map.of("status", "erro", "mensagem", msg);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private record Creds(String host, String apikey) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException("Empresa ativa sem portal configurado.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }
}
