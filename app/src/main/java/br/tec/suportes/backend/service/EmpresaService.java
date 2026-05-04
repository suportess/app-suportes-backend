package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.domain.Usuario;
import br.tec.suportes.backend.domain.UsuarioEmpresa;
import br.tec.suportes.backend.dto.empresa.EmpresaDTO;
import br.tec.suportes.backend.dto.empresa.EmpresaRequest;
import br.tec.suportes.backend.dto.empresa.GerarNgrokResponse;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ConfEmpresaRepository;
import br.tec.suportes.backend.repository.UsuarioEmpresaRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import br.tec.suportes.backend.service.NgrokService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final ConfEmpresaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final NgrokService ngrokService;

    // ─── Operações user-scoped ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarPorUsuario(String auth0Sub) {
        return usuarioEmpresaRepository.findByUsuarioAuth0Sub(auth0Sub)
                .stream()
                .map(ue -> toDTO(ue.getEmpresa()))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaDTO buscarPorUsuario(String auth0Sub, Long empresaId) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        return toDTO(vinculo.getEmpresa());
    }

    @Transactional
    public EmpresaDTO cadastrar(String auth0Sub, EmpresaRequest req) {
        Usuario usuario = usuarioRepository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Faça login novamente."));

        ConfEmpresa empresa = new ConfEmpresa();
        aplicarRequest(empresa, req);
        empresa.setApikey(gerarApikey());
        repository.save(empresa);

        UsuarioEmpresa vinculo = new UsuarioEmpresa();
        vinculo.setUsuario(usuario);
        vinculo.setEmpresa(empresa);
        usuarioEmpresaRepository.save(vinculo);

        return toDTO(empresa);
    }

    @Transactional
    public EmpresaDTO atualizar(String auth0Sub, Long empresaId, EmpresaRequest req) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        aplicarRequest(vinculo.getEmpresa(), req);
        return toDTO(repository.save(vinculo.getEmpresa()));
    }

    @Transactional
    public EmpresaDTO regenerarApikey(String auth0Sub, Long empresaId) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        vinculo.getEmpresa().setApikey(gerarApikey());
        return toDTO(repository.save(vinculo.getEmpresa()));
    }

    @Transactional
    public void deletar(String auth0Sub, Long empresaId) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        usuarioEmpresaRepository.delete(vinculo);
        repository.delete(vinculo.getEmpresa());
    }

    // ─── Ngrok ───────────────────────────────────────────────────────────────

    /**
     * Gera um domínio ngrok e salva em ds_host_portal da empresa de forma atômica.
     * Retorna a empresa atualizada e o ngrokDomainId para a UI poder apagar via API depois.
     */
    @Transactional
    public GerarNgrokResponse gerarNgrokDomain(String auth0Sub, Long empresaId) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        ConfEmpresa empresa = vinculo.getEmpresa();
        var ngrok = ngrokService.gerarReservedDomain(empresa.getNmEmpresa());
        empresa.setDsHostPortal("https://" + ngrok.getDomain());
        return new GerarNgrokResponse(toDTO(repository.save(empresa)), ngrok.getId());
    }

    /**
     * Remove o domínio ngrok (via API ngrok, se ngrokId fornecido) e limpa ds_host_portal.
     */
    @Transactional
    public EmpresaDTO removerNgrokDomain(String auth0Sub, Long empresaId, String ngrokId) {
        var vinculo = usuarioEmpresaRepository.findByUsuarioAuth0SubAndEmpresaId(auth0Sub, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada para este usuário."));
        if (ngrokId != null && !ngrokId.isBlank()) {
            ngrokService.deletarReservedDomain(ngrokId);
        }
        vinculo.getEmpresa().setDsHostPortal(null);
        return toDTO(repository.save(vinculo.getEmpresa()));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void aplicarRequest(ConfEmpresa e, EmpresaRequest req) {
        e.setNmEmpresa(req.getNmEmpresa());
        e.setDsRazaoSocial(req.getDsRazaoSocial());
        e.setNrCnpj(req.getNrCnpj());
        e.setDsEmail(req.getDsEmail());
        e.setNrTelefone(req.getNrTelefone());
        e.setDsHostPortal(req.getDsHostPortal());
    }

    private String gerarApikey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes).toUpperCase();
    }

    EmpresaDTO toDTO(ConfEmpresa e) {
        return EmpresaDTO.builder()
                .id(e.getId())
                .nmEmpresa(e.getNmEmpresa())
                .dsRazaoSocial(e.getDsRazaoSocial())
                .nrCnpj(e.getNrCnpj())
                .dsEmail(e.getDsEmail())
                .nrTelefone(e.getNrTelefone())
                .apikey(e.getApikey())
                .dsHostPortal(e.getDsHostPortal())
                .snAtivo(e.getSnAtivo())
                .dtCriacao(e.getDtCriacao())
                .dtAtualizacao(e.getDtAtualizacao())
                .build();
    }
}
