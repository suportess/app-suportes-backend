package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.saldos.ConsultaSaldoItemRequest;
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
public class ConsultaSaldosService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    /**
     * Consulta saldo de cada item no Oracle via portal.
     * Para cada item chama POST /mv/api/saldos/consultar-item e retorna o JSON parseado.
     */
    public List<Map<String, Object>> consultar(String auth0Sub, List<ConsultaSaldoItemRequest> itens) {
        var creds = resolveCreds(auth0Sub);
        return itens.stream()
                .map(item -> consultarItem(creds.host(), creds.apikey(), item))
                .toList();
    }

    private Map<String, Object> consultarItem(String host, String apikey, ConsultaSaldoItemRequest item) {
        Map<String, Object> body = new HashMap<>();
        body.put("cd_produto",    item.cdProduto());
        body.put("cd_estoque",    item.cdEstoque());
        body.put("cd_fornecedor", item.cdFornecedor());
        body.put("cd_unidade",    item.cdUnidade());

        try {
            Map<String, Object> portalResponse = portalClient.consultarSaldo(host, apikey, body);

            // Portal retorna {"resultado": {"result_out": "{...json...}"}}
            @SuppressWarnings("unchecked")
            Map<String, Object> resultado = (Map<String, Object>) portalResponse.get("resultado");
            String resultJson = resultado != null ? (String) resultado.get("result_out") : null;

            if (resultJson == null || resultJson.isBlank()) {
                return Map.of("CD_PRODUTO", item.cdProduto(), "erro", "Sem resposta do Oracle");
            }

            return objectMapper.readValue(resultJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            log.error("Erro ao consultar saldo do produto {}: {}", item.cdProduto(), ex.getMessage());
            return Map.of("CD_PRODUTO", item.cdProduto(), "erro", ex.getMessage());
        }
    }

    private record Creds(String host, String apikey) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa selecionada. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui portal configurado.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }
}
