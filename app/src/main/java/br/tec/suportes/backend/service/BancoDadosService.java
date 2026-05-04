package br.tec.suportes.backend.service;

import br.tec.suportes.backend.client.PortalClient;
import br.tec.suportes.backend.domain.ConfBancoDados;
import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.dto.banco.BancoDadosDTO;
import br.tec.suportes.backend.dto.banco.BancoDadosRequest;
import br.tec.suportes.backend.dto.banco.PortalStatusDTO;
import br.tec.suportes.backend.exception.PortalClientException;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ConfBancoDadosRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BancoDadosService {

    /** Chave usada no portal para identificar a conexão de banco. Sempre esta. */
    private static final String PORTAL_DB_KEY = "oracle-prod";

    private final ConfBancoDadosRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PortalClient portalClient;

    // ─── Status do portal ──────────────────────────────────────────────────────

    /**
     * Verifica o status do portal da empresa ativa do usuário.
     * Se o portal estiver indisponível, retorna PortalStatusDTO com portalAtivo=false.
     */
    public PortalStatusDTO verificarStatus(String auth0Sub) {
        var empresa = resolverEmpresa(auth0Sub);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = portalClient.verificarStatus(empresa.getDsHostPortal());
            String status = String.valueOf(body.getOrDefault("status", "UNKNOWN"));
            String uptime = (String) body.get("uptime");
            boolean ativo = "UP".equalsIgnoreCase(status);

            // Verifica se o banco oracle-prod já está registrado no portal
            Object databases = body.get("databases");
            boolean bancoCadastrado = false;
            if (databases instanceof Map<?, ?> dbMap) {
                bancoCadastrado = dbMap.containsKey(PORTAL_DB_KEY);
            }
            return new PortalStatusDTO(ativo, status, uptime, bancoCadastrado);
        } catch (PortalClientException ex) {
            return new PortalStatusDTO(false, "DOWN", null, false);
        }
    }

    // ─── Busca ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<BancoDadosDTO> buscarPorUsuario(String auth0Sub) {
        var empresa = resolverEmpresa(auth0Sub);
        return repository.findByEmpresaId(empresa.getId()).map(this::toDTO);
    }

    // ─── Salvar (apenas cria — não existe update; para reconfigurar: remover e cadastrar novamente) ──

    @Transactional
    public BancoDadosDTO salvar(String auth0Sub, BancoDadosRequest req) {
        var empresa = resolverEmpresa(auth0Sub);

        if (repository.findByEmpresaId(empresa.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Banco de dados já configurado. Remova o cadastro atual para reconfigurar.");
        }

        var conf = new ConfBancoDados();
        conf.setEmpresa(empresa);
        aplicarRequest(conf, req);

        var payload = montarPayload(req);
        portalClient.registrarDatabase(empresa.getDsHostPortal(), empresa.getApikey(), payload);

        return toDTO(repository.save(conf));
    }

    // ─── Remover ──────────────────────────────────────────────────────────────

    @Transactional
    public void remover(String auth0Sub) {
        var empresa = resolverEmpresa(auth0Sub);
        var conf = repository.findByEmpresaId(empresa.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Banco de dados não configurado."));

        portalClient.removerDatabase(empresa.getDsHostPortal(), empresa.getApikey(), PORTAL_DB_KEY);
        repository.delete(conf);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ConfEmpresa resolverEmpresa(String auth0Sub) {
        var usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = Optional.ofNullable(usuario.getEmpresaAtiva())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma empresa ativa selecionada. Selecione uma empresa no menu superior."));
        if (empresa.getDsHostPortal() == null || empresa.getDsHostPortal().isBlank())
            throw new RecursoNaoEncontradoException(
                    "A empresa ativa não possui portal configurado. Configure o domínio ngrok primeiro.");
        return empresa;
    }

    private void aplicarRequest(ConfBancoDados conf, BancoDadosRequest req) {
        conf.setDsDriver(req.dsDriver());
        conf.setDsHost(req.dsHost());
        conf.setNrPorta(req.nrPorta());
        conf.setNmBanco(req.nmBanco());
        conf.setNmUsuario(req.nmUsuario());
        conf.setDsSenha(req.dsSenha());
        conf.setNrMaxOpenConns(req.nrMaxOpenConns());
        conf.setNrMaxIdleConns(req.nrMaxIdleConns());
        conf.setNrConnMaxLifetime(req.nrConnMaxLifetime());
        conf.setNrConnMaxIdleTime(req.nrConnMaxIdleTime());
        conf.setDsPortalKey(PORTAL_DB_KEY);
    }

    private Map<String, Object> montarPayload(BancoDadosRequest req) {
        var pool = new LinkedHashMap<String, Object>();
        pool.put("maxOpenConns",       req.nrMaxOpenConns());
        pool.put("maxIdleConns",       req.nrMaxIdleConns());
        pool.put("connMaxLifetimeSec", req.nrConnMaxLifetime());
        pool.put("connMaxIdleTimeSec", req.nrConnMaxIdleTime());

        var payload = new LinkedHashMap<String, Object>();
        payload.put("key",      PORTAL_DB_KEY);
        payload.put("driver",   req.dsDriver());
        payload.put("host",     req.dsHost());
        payload.put("port",     req.nrPorta());
        payload.put("dbName",   req.nmBanco());
        payload.put("user",     req.nmUsuario());
        payload.put("password", req.dsSenha());
        payload.put("pool",     pool);
        return payload;
    }

    private BancoDadosDTO toDTO(ConfBancoDados c) {
        return new BancoDadosDTO(
                c.getId(),
                c.getDsDriver(),
                c.getDsHost(),
                c.getNrPorta(),
                c.getNmBanco(),
                c.getNmUsuario(),
                c.getNrMaxOpenConns(),
                c.getNrMaxIdleConns(),
                c.getNrConnMaxLifetime(),
                c.getNrConnMaxIdleTime(),
                c.getDtCriacao(),
                c.getDtAtualizacao()
        );
    }
}
