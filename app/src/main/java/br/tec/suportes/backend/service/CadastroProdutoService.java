package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.produto.CadastroProdutoRequest;
import br.tec.suportes.backend.dto.produto.CadastroProdutoResponse;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CadastroProdutoService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    /**
     * Cadastra um novo produto no Oracle DBAMV via Portal HTTP.
     * Chama POST /mv/api/produtos no portal da empresa ativa do usuário.
     */
    public CadastroProdutoResponse cadastrar(String auth0Sub, CadastroProdutoRequest req) {
        var c = resolveCreds(auth0Sub);

        Map<String, Object> body = new HashMap<>();
        body.put("ds_produto",              req.getDsProduto());
        body.put("ds_comercial",            req.getDsComercial());
        body.put("ds_especificacao",        req.getDsEspecificacao());
        body.put("sn_lote",                 req.getSnLote());
        body.put("sn_validade",             req.getSnValidade());
        body.put("sn_medicamento",          req.getSnMedicamento());
        body.put("sn_consignado",           req.getSnConsignado());
        body.put("tp_sexo",                 req.getTpSexo());
        body.put("cd_especie",              req.getCdEspecie());
        body.put("cd_classe",               req.getCdClasse());
        body.put("cd_sub_cla",              req.getCdSubCla());
        body.put("ds_sub_cla",              req.getDsSubCla());
        body.put("cd_unidade",              req.getCdUnidade());
        body.put("empresas_json",           toJson(req.getEmpresas()));
        body.put("cd_tip_ativ",             req.getCdTipAtiv());
        body.put("cd_pro_fat",              req.getCdProFat());
        body.put("cd_pro_fat_sus",          req.getCdProFatSus());
        body.put("cd_procedimento_sus",     req.getCdProcedimentoSus());
        body.put("cd_fornecedor_principal", req.getCdFornecedorPrincipal());

        var portalResp = portalClient.cadastrarProduto(c.host(), c.apikey(), body);

        Long cdProduto = null;
        if (portalResp != null && portalResp.get("cd_produto_out") != null) {
            try { cdProduto = Long.parseLong(portalResp.get("cd_produto_out").toString()); }
            catch (NumberFormatException ignored) {}
        }

        return CadastroProdutoResponse.of(req.getDsProduto(), cdProduto);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private record Creds(String host, String apikey) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa selecionada. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui portal configurado. Configure o domínio ngrok primeiro.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao serializar JSON: " + e.getMessage(), e);
        }
    }
}
