package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.domain.ImportacaoProduto;
import br.tec.suportes.backend.dto.produto.CadastroProdutoRequest;
import br.tec.suportes.backend.dto.produto.CadastroProdutoResponse;
import br.tec.suportes.backend.dto.produto.ImportacaoProdutoDTO;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ImportacaoProdutoRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CadastroProdutoService {

    private final PortalClient portalClient;
    private final UsuarioRepository usuarioRepository;
    private final ImportacaoProdutoRepository importacaoRepository;
    private final ObjectMapper objectMapper;

    /**
     * Cadastra um novo produto no Oracle DBAMV via Portal HTTP.
     * Chama POST /mv/api/produtos no portal da empresa ativa do usuário.
     * Se o produto for criado com sucesso, registra o de-para em importacao_produto.
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
        if (portalResp != null) {
            // Portal retorna {"resultado": {"cd_produto_out": "33337"}}
            Object resultadoRaw = portalResp.get("resultado");
            if (resultadoRaw instanceof java.util.Map<?, ?> resultadoMap) {
                Object outVal = resultadoMap.get("cd_produto_out");
                if (outVal != null) {
                    try { cdProduto = Long.parseLong(outVal.toString()); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }

        // Registrar de-para quando o produto foi criado com sucesso no MV
        if (cdProduto != null) {
            importacaoRepository.save(ImportacaoProduto.builder()
                    .auth0Sub(auth0Sub)
                    .cdProdutoMv(cdProduto)
                    .dsProduto(req.getDsProduto())
                    .dsComercial(req.getDsComercial())
                    .cdEspecie(req.getCdEspecie())
                    .cdClasse(req.getCdClasse())
                    .cdSubCla(req.getCdSubCla())
                    .dsSubCla(req.getDsSubCla())
                    .cdUnidade(req.getCdUnidade())
                    .snLote(req.getSnLote())
                    .snValidade(req.getSnValidade())
                    .build());
        }

        return CadastroProdutoResponse.of(req.getDsProduto(), cdProduto);
    }

    public List<ImportacaoProdutoDTO> listarImportacoes(String auth0Sub) {
        return importacaoRepository
                .findByAuth0SubOrderByDtImportacaoDesc(auth0Sub)
                .stream()
                .map(ImportacaoProdutoDTO::of)
                .toList();
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
