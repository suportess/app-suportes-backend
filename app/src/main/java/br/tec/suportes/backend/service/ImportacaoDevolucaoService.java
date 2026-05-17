package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.domain.ImportacaoDevolucao;
import br.tec.suportes.backend.domain.ImportacaoDevolucaoItem;
import br.tec.suportes.backend.dto.saldos.ImportarDevolucaoItemRequest;
import br.tec.suportes.backend.dto.saldos.ImportarDevolucaoItemResponse;
import br.tec.suportes.backend.dto.saldos.TransferenciaRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoItemRepository;
import br.tec.suportes.backend.repository.ImportacaoDevolucaoRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportacaoDevolucaoService {

    private final PortalClient                      portalClient;
    private final UsuarioRepository                 usuarioRepository;
    private final ImportacaoDevolucaoRepository     sessaoRepository;
    private final ImportacaoDevolucaoItemRepository itemRepository;
    private final ObjectMapper                      objectMapper;

    @Transactional
    public ImportarDevolucaoItemResponse processarItem(String auth0Sub, ImportarDevolucaoItemRequest req) {

        var creds = resolveCreds(auth0Sub);

        // 1. Criar ou reutilizar sessão
        ImportacaoDevolucao sessao = resolverSessao(req.cdSessao(), auth0Sub, creds);

        // 2. Persistir item com status pendente (snapshot da tela)
        ImportacaoDevolucaoItem item = ImportacaoDevolucaoItem.builder()
                .cdSessao(sessao.getId())
                .nrLinha(req.nrLinha())
                .cdProduto(req.cdProduto())
                .dsProduto(req.dsProduto())
                .cdEstoque(req.cdEstoque())
                .dsEstoque(req.dsEstoque())
                .cdFornecedor(req.cdFornecedor())
                .nmFornecedor(req.nmFornecedor())
                .cdUnidade(req.cdUnidade())
                .dsUnidade(req.dsUnidade())
                .qtDevolvida(req.qtDevolvida())
                .tpMovimento(req.tpMovimento())
                .cdMotDev(req.cdMotDev())
                .tpDevolucao(req.tpDevolucao() != null ? req.tpDevolucao() : "Z")
                .vlSaldoEstoque(req.saldoEstoque())
                .vlSaldoFicha(req.saldoFicha())
                .vlSaldoConsigForn(req.saldoConsigForn())
                .vlSaldoLotes(req.saldoLotes())
                .stExecucao("pendente")
                .build();
        item = itemRepository.save(item);

        // Atualiza contagem da sessão
        sessao.setQtItens(sessao.getQtItens() + 1);

        // 3. Despachar por tipo de movimento
        String tpMov = req.tpMovimento() != null ? req.tpMovimento().toUpperCase() : "";

        if ("DEVOLUCAO".equals(tpMov)) {
            return processarDevolucao(item, sessao, creds, req);
        } else if ("ENTRADA".equals(tpMov)) {
            return processarEntrada(item, sessao, creds, req);
        } else {
            item.setStExecucao("ignorado");
            item.setDsErro("Movimento " + req.tpMovimento() + " não processado pelo bloco Oracle.");
            item.setDtExecucao(LocalDateTime.now());
            itemRepository.save(item);
            sessaoRepository.save(sessao);
            return new ImportarDevolucaoItemResponse(
                    item.getId(), sessao.getId(), "ignorado",
                    null, null, "Movimento não suportado: " + req.tpMovimento(), null, null);
        }
    }

    // ── DEVOLUCAO ─────────────────────────────────────────────────────────────
    private ImportarDevolucaoItemResponse processarDevolucao(
            ImportacaoDevolucaoItem item, ImportacaoDevolucao sessao,
            Creds creds, ImportarDevolucaoItemRequest req) {

        // 4. Montar payload para portal-scripts
        Map<String, Object> body = new HashMap<>();
        body.put("cd_produto",    Long.parseLong(req.cdProduto()));
        body.put("qt_devolvida",  req.qtDevolvida());
        if (req.cdEstoque()    != null && !req.cdEstoque().isBlank())
            body.put("cd_estoque",    Long.parseLong(req.cdEstoque()));
        if (req.cdFornecedor() != null && !req.cdFornecedor().isBlank())
            body.put("cd_fornecedor", Long.parseLong(req.cdFornecedor()));
        if (req.cdUnidade()    != null && !req.cdUnidade().isBlank())
            body.put("cd_unidade",    req.cdUnidade());
        if (req.cdMotDev()     != null)
            body.put("cd_mot_dev",    req.cdMotDev());
        body.put("tp_devolucao", req.tpDevolucao() != null && !req.tpDevolucao().isBlank() ? req.tpDevolucao() : "Z");

        // 5. Executar bloco Oracle
        try {
            Map<String, Object> portalResponse = portalClient.devolverSaldoConsig(
                    creds.host(), creds.apikey(), body);

            // Portal retorna {"devolucao":{"result_out":"{...}"}}  (alias definido na rota V68)
            @SuppressWarnings("unchecked")
            Map<String, Object> resultado = (Map<String, Object>) portalResponse.get("devolucao");
            String resultJson = resultado != null ? (String) resultado.get("result_out") : null;

            if (resultJson == null || resultJson.isBlank()) {
                throw new RuntimeException("Portal retornou resposta vazia.");
            }

            Map<String, Object> oracle = objectMapper.readValue(
                    resultJson, new TypeReference<Map<String, Object>>() {});

            String status = (String) oracle.get("status");

            if ("ok".equals(status)) {
                BigDecimal qtTotal  = toBD(oracle.get("qt_total_devolvida"));
                BigDecimal qtNaoAt  = toBD(oracle.get("qt_nao_atendida"));
                String aviso        = (String) oracle.get("aviso");

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> devolucoes =
                        (List<Map<String, Object>>) oracle.get("devolucoes");

                item.setStExecucao("ok");
                item.setQtTotalDevolvida(qtTotal);
                item.setQtNaoAtendida(qtNaoAt);
                item.setJsonResultado(resultJson);
                item.setDtExecucao(LocalDateTime.now());
                itemRepository.save(item);

                sessao.setQtSucesso(sessao.getQtSucesso() + 1);
                sessaoRepository.save(sessao);

                return new ImportarDevolucaoItemResponse(
                        item.getId(), sessao.getId(), "ok",
                        qtTotal, qtNaoAt, aviso, devolucoes, null);

            } else {
                String msg = (String) oracle.getOrDefault("mensagem", "Erro desconhecido no Oracle.");
                item.setStExecucao("erro");
                item.setDsErro(truncate(msg, 2000));
                item.setJsonResultado(resultJson);
                item.setDtExecucao(LocalDateTime.now());
                itemRepository.save(item);

                sessao.setQtErro(sessao.getQtErro() + 1);
                sessaoRepository.save(sessao);

                return new ImportarDevolucaoItemResponse(
                        item.getId(), sessao.getId(), "erro",
                        null, null, msg, null, null);
            }

        } catch (Exception ex) {
            log.error("Erro ao executar devolução Oracle para produto {}: {}", req.cdProduto(), ex.getMessage());
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            item.setStExecucao("erro");
            item.setDsErro(truncate(msg, 2000));
            item.setDtExecucao(LocalDateTime.now());
            itemRepository.save(item);

            sessao.setQtErro(sessao.getQtErro() + 1);
            sessaoRepository.save(sessao);

            return new ImportarDevolucaoItemResponse(
                    item.getId(), sessao.getId(), "erro",
                    null, null, msg, null, null);
        }
    }

    // ── ENTRADA ───────────────────────────────────────────────────────────────
    private ImportarDevolucaoItemResponse processarEntrada(
            ImportacaoDevolucaoItem item, ImportacaoDevolucao sessao,
            Creds creds, ImportarDevolucaoItemRequest req) {

        Map<String, Object> body = new HashMap<>();
        body.put("cd_produto",    Long.parseLong(req.cdProduto()));
        body.put("cd_estoque",    Long.parseLong(req.cdEstoque()));
        body.put("cd_fornecedor", Long.parseLong(req.cdFornecedor()));
        body.put("cd_unidade",    req.cdUnidade());
        body.put("qt_entrada",    req.qtDevolvida());

        try {
            Map<String, Object> portalResponse = portalClient.darEntradaProduto(
                    creds.host(), creds.apikey(), body);

            // Portal retorna {"entrada":{"result_out":"{...}"}}  (alias definido na rota V73)
            @SuppressWarnings("unchecked")
            Map<String, Object> resultado = (Map<String, Object>) portalResponse.get("entrada");
            String resultJson = resultado != null ? (String) resultado.get("result_out") : null;

            if (resultJson == null || resultJson.isBlank()) {
                throw new RuntimeException("Portal retornou resposta vazia.");
            }

            Map<String, Object> oracle = objectMapper.readValue(
                    resultJson, new TypeReference<Map<String, Object>>() {});

            String status = (String) oracle.get("status");

            if ("ok".equals(status)) {
                BigDecimal qtEntrada = toBD(oracle.get("qt_entrada"));

                item.setStExecucao("ok");
                item.setQtTotalDevolvida(qtEntrada);
                item.setJsonResultado(resultJson);
                item.setDtExecucao(LocalDateTime.now());
                itemRepository.save(item);

                sessao.setQtSucesso(sessao.getQtSucesso() + 1);
                sessaoRepository.save(sessao);

                return new ImportarDevolucaoItemResponse(
                        item.getId(), sessao.getId(), "ok",
                        qtEntrada, null, null, null, oracle);

            } else {
                String msg = (String) oracle.getOrDefault("mensagem", "Erro desconhecido no Oracle.");
                item.setStExecucao("erro");
                item.setDsErro(truncate(msg, 2000));
                item.setJsonResultado(resultJson);
                item.setDtExecucao(LocalDateTime.now());
                itemRepository.save(item);

                sessao.setQtErro(sessao.getQtErro() + 1);
                sessaoRepository.save(sessao);

                return new ImportarDevolucaoItemResponse(
                        item.getId(), sessao.getId(), "erro",
                        null, null, msg, null, null);
            }

        } catch (Exception ex) {
            log.error("Erro ao executar entrada Oracle para produto {}: {}", req.cdProduto(), ex.getMessage());
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            item.setStExecucao("erro");
            item.setDsErro(truncate(msg, 2000));
            item.setDtExecucao(LocalDateTime.now());
            itemRepository.save(item);

            sessao.setQtErro(sessao.getQtErro() + 1);
            sessaoRepository.save(sessao);

            return new ImportarDevolucaoItemResponse(
                    item.getId(), sessao.getId(), "erro",
                    null, null, msg, null, null);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Persiste uma transferência de grupo como UM único item na sessão.
     * Cria nova sessão se cdSessao == null.
     *
     * @return ID da sessão usada/criada
     */
    @Transactional
    public Long persistirTransferencia(String auth0Sub, TransferenciaRequest req, Map<String, Object> oracle) {
        Creds creds = resolveCreds(auth0Sub);
        ImportacaoDevolucao sessao = resolverSessao(req.cdSessao(), auth0Sub, creds);

        String status = (String) oracle.getOrDefault("status", "erro");
        String stExecucao = "ok".equals(status) ? "ok" : "erro";

        String jsonResult = null;
        try { jsonResult = objectMapper.writeValueAsString(oracle); } catch (Exception ignored) {}

        String dsErro = null;
        if (!"ok".equals(status)) {
            dsErro = truncate((String) oracle.getOrDefault("mensagem", "Erro na transferencia Oracle."), 2000);
        }

        int count = 0;
        int okCount = 0;

        // ── Um item por cabeça (tp_movimento = TRANSFERENCIA) ────────────────
        for (TransferenciaRequest.CabecaItem cab : req.cabecas()) {
            ImportacaoDevolucaoItem item = ImportacaoDevolucaoItem.builder()
                    .cdSessao(sessao.getId())
                    .nrLinha(cab.nrLinha())
                    .cdProduto(cab.cdProduto() != null ? cab.cdProduto().toString() : null)
                    .dsProduto(cab.dsProduto())
                    .cdEstoque(cab.cdEstoque() != null ? cab.cdEstoque().toString() : null)
                    .dsEstoque(cab.dsEstoque())
                    .cdFornecedor(cab.cdFornecedor() != null ? cab.cdFornecedor().toString() : null)
                    .nmFornecedor(cab.nmFornecedor())
                    .cdUnidade(cab.cdUnidade())
                    .dsUnidade(cab.dsUnidade())
                    .qtDevolvida(cab.qtDev())
                    .tpMovimento("TRANSFERENCIA")
                    .stExecucao(stExecucao)
                    .dsErro(dsErro)
                    .jsonResultado(jsonResult)
                    .dtExecucao(LocalDateTime.now())
                    .build();
            itemRepository.save(item);
            count++;
            if ("ok".equals(stExecucao)) okCount++;
        }

        // ── Um item por filho (tp_movimento = ITEM_TRANSF) ───────────────────
        for (TransferenciaRequest.FilhoItem filho : req.itens()) {
            ImportacaoDevolucaoItem item = ImportacaoDevolucaoItem.builder()
                    .cdSessao(sessao.getId())
                    .nrLinha(filho.nrLinha())
                    .cdProduto(filho.cdProduto() != null ? filho.cdProduto().toString() : null)
                    .dsProduto(filho.dsProduto())
                    .cdEstoque(filho.cdEstoque() != null ? filho.cdEstoque().toString() : null)
                    .dsEstoque(filho.dsEstoque())
                    .cdFornecedor(filho.cdFornecedor() != null ? filho.cdFornecedor().toString() : null)
                    .nmFornecedor(filho.nmFornecedor())
                    .cdUnidade(filho.cdUnidade())
                    .dsUnidade(filho.dsUnidade())
                    .qtDevolvida(filho.qtEntrada())
                    .tpMovimento("ITEM_TRANSF")
                    .stExecucao(stExecucao)
                    .dsErro(dsErro)
                    .jsonResultado(jsonResult)
                    .dtExecucao(LocalDateTime.now())
                    .build();
            itemRepository.save(item);
            count++;
            if ("ok".equals(stExecucao)) okCount++;
        }

        sessao.setQtItens(sessao.getQtItens() + count);
        if ("ok".equals(status)) sessao.setQtSucesso(sessao.getQtSucesso() + okCount);
        else                     sessao.setQtErro(sessao.getQtErro() + count);
        sessaoRepository.save(sessao);

        return sessao.getId();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ImportacaoDevolucao resolverSessao(Long cdSessao, String auth0Sub, Creds creds) {
        if (cdSessao != null) {
            return sessaoRepository.findById(cdSessao)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Sessão de importação não encontrada: " + cdSessao));
        }
        return sessaoRepository.save(ImportacaoDevolucao.builder()
                .auth0Sub(auth0Sub)
                .nmUsuario(creds.nmUsuario())
                .emailUsuario(creds.emailUsuario())
                .nmEmpresa(creds.nmEmpresa())
                .build());
    }

    private record Creds(String host, String apikey, String nmEmpresa, String nmUsuario, String emailUsuario) {}

    private Creds resolveCreds(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException("Empresa ativa sem portal configurado.");
        return new Creds(empresa.getDsHostPortal(), empresa.getApikey(), empresa.getNmEmpresa(),
                usuario.getNmUsuario(), usuario.getEmail());
    }

    private static BigDecimal toBD(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return null; }
    }

    private static String truncate(String s, int max) {
        return s != null && s.length() > max ? s.substring(0, max) : s;
    }
}
