package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.vinculo.VincularSusRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VincularSusService {

    private final PortalClient           portalClient;
    private final VinculoProdutoService  vinculoProdutoService;
    private final UsuarioRepository      usuarioRepository;
    private final ObjectMapper           objectMapper;

    /**
     * Envia os pares (produto-pai → produto-filho) para o Oracle via portal-scripts,
     * persiste o cabeçalho e os itens no Postgres e retorna o resultado completo.
     *
     * @return Map com: sessao_id, status, vinculos[]
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> vincular(String auth0Sub, VincularSusRequest req) {

        var creds = resolveCreds(auth0Sub);

        // Monta o body para o portal: vinculos_json = JSON array serializado
        // O portal-scripts espera vinculos_json como string no body
        var vinculosMaps = req.getVinculos().stream()
                .map(i -> Map.<String, Object>of(
                        "cd_produto_antigo", i.getCdProdutoAntigo(),
                        "cd_produto_novo",   i.getCdProdutoNovo()
                ))
                .toList();

        // Serializa para string JSON (o comando Oracle recebe como VARCHAR2)
        String vinculosJson = serializeToJson(vinculosMaps);

        Map<String, Object> body = Map.of("vinculos_json", vinculosJson);

        // Chama portal → Oracle
        Map<String, Object> portalResult = portalClient.vincularSusProduto(creds.host(), creds.apikey(), body);

        // Portal retorna {"vincular": {"result_out": "...json string..."}}
        // O pipeline envolve com o alias do passo ("vincular") — precisa desembrulhar
        @SuppressWarnings("unchecked")
        Map<String, Object> vincularMap = (Map<String, Object>) portalResult.get("vincular");
        if (vincularMap == null) {
            log.error("Portal não retornou chave 'vincular'. resposta completa={}", portalResult);
            throw new IllegalStateException("Resposta inesperada do portal: chave 'vincular' ausente.");
        }
        String resultOutStr = (String) vincularMap.get("result_out");
        if (resultOutStr == null || resultOutStr.isBlank()) {
            log.error("Portal retornou result_out vazio. vincularMap={}", vincularMap);
            throw new IllegalStateException("Portal retornou resposta vazia do Oracle.");
        }

        Map<String, Object> oracleResult;
        try {
            oracleResult = objectMapper.readValue(resultOutStr, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Falha ao parsear result_out do Oracle: {}", resultOutStr);
            throw new IllegalStateException("Resposta inválida do Oracle: " + e.getMessage());
        }

        // Checa status do retorno Oracle
        String status = String.valueOf(oracleResult.getOrDefault("status", "erro"));
        if (!"ok".equals(status)) {
            String mensagem = String.valueOf(oracleResult.getOrDefault("mensagem", "Erro desconhecido."));
            log.error("Oracle retornou erro no vincular-sus: {}", mensagem);
            throw new IllegalStateException("Falha ao vincular SUS no Oracle: " + mensagem);
        }

        // Persiste no Postgres
        List<Map<String, Object>> vinculos =
                (List<Map<String, Object>>) oracleResult.getOrDefault("vinculos", List.of());

        Long sessaoId = vinculoProdutoService.persistirVinculos(auth0Sub, vinculos);

        log.info("Vínculo SUS concluído: sessao={} qt={}", sessaoId, vinculos.size());

        // Retorna resultado enriquecido com o id da sessão
        return Map.of(
                "sessao_id", sessaoId != null ? sessaoId : 0,
                "status",    "ok",
                "vinculos",  vinculos
        );
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private record Creds(String host, String apikey) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui portal configurado.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }

    /** Serializa lista de maps para JSON array sem dependência de Jackson no service layer. */
    private static String serializeToJson(List<Map<String, Object>> list) {
        var sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Map<String, Object> m = list.get(i);
            sb.append("{\"cd_produto_antigo\":").append(m.get("cd_produto_antigo"))
              .append(",\"cd_produto_novo\":").append(m.get("cd_produto_novo"))
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}
