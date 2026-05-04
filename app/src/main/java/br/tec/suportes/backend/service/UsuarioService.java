package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ConfEmpresa;
import br.tec.suportes.backend.domain.Usuario;
import br.tec.suportes.backend.domain.UsuarioEmpresa;
import br.tec.suportes.backend.dto.empresa.EmpresaDTO;
import br.tec.suportes.backend.dto.usuario.UsuarioDTO;
import br.tec.suportes.backend.dto.usuario.UsuarioEmpresaDTO;
import br.tec.suportes.backend.dto.usuario.UsuarioRequest;
import br.tec.suportes.backend.exception.AcessoNegadoException;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ConfEmpresaRepository;
import br.tec.suportes.backend.repository.UsuarioEmpresaRepository;
import br.tec.suportes.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository       repository;
    private final UsuarioEmpresaRepository vinculoRepository;
    private final ConfEmpresaRepository   empresaRepository;

    /** Retorna o usuário existente ou cria um novo a partir do perfil Auth0. */
    @Transactional
    public UsuarioDTO findOrCreate(UsuarioRequest req) {
        return repository.findByAuth0Sub(req.getAuth0Sub())
                .map(u -> {
                    u.setEmail(req.getEmail());
                    u.setNmUsuario(req.getNmUsuario());
                    u.setPicture(req.getPicture());
                    return toDTO(repository.save(u));
                })
                .orElseGet(() -> {
                    var novo = new Usuario();
                    novo.setAuth0Sub(req.getAuth0Sub());
                    novo.setEmail(req.getEmail());
                    novo.setNmUsuario(req.getNmUsuario());
                    novo.setPicture(req.getPicture());
                    novo.setTipo("OPERADOR");
                    return toDTO(repository.save(novo));
                });
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorSub(String auth0Sub) {
        return repository.findByAuth0Sub(auth0Sub)
                .map(this::toDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos(String auth0Sub) {
        Usuario caller = resolveCallerOrThrow(auth0Sub);
        if ("OPERADOR".equals(caller.getTipo())) {
            return List.of(toDTO(caller));
        }
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(String auth0Sub, Long id) {
        Usuario caller = resolveCallerOrThrow(auth0Sub);
        if ("OPERADOR".equals(caller.getTipo()) && !caller.getId().equals(id)) {
            throw new AcessoNegadoException("Acesso negado.");
        }
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    /** Vincula uma empresa ao usuário. Idempotente — ignora se já vinculado. */
    @Transactional
    public UsuarioDTO vincularEmpresa(String auth0Sub, Long usuarioId, Long empresaId) {
        Usuario caller = resolveCallerOrThrow(auth0Sub);
        if ("OPERADOR".equals(caller.getTipo())) {
            throw new AcessoNegadoException("Usuários do tipo OPERADOR não podem vincular empresas.");
        }
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        ConfEmpresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));

        if (!vinculoRepository.existsByUsuarioIdAndEmpresaId(usuarioId, empresaId)) {
            var vinculo = new UsuarioEmpresa();
            vinculo.setUsuario(usuario);
            vinculo.setEmpresa(empresa);
            vinculoRepository.save(vinculo);
        }
        return toDTO(repository.findById(usuarioId).orElseThrow());
    }

    /** Remove o vínculo de uma empresa ao usuário. */
    @Transactional
    public void desvincularEmpresa(String auth0Sub, Long usuarioId, Long empresaId) {
        Usuario caller = resolveCallerOrThrow(auth0Sub);
        if ("OPERADOR".equals(caller.getTipo())) {
            throw new AcessoNegadoException("Usuários do tipo OPERADOR não podem desvincular empresas.");
        }
        if (!repository.existsById(usuarioId))
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        vinculoRepository.deleteByUsuarioIdAndEmpresaId(usuarioId, empresaId);
    }

    /** Define a empresa ativa do usuário autenticado. Valida se está vinculado. */
    @Transactional
    public UsuarioDTO atualizarEmpresaAtiva(String auth0Sub, Long empresaId) {
        Usuario usuario = repository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        if (!vinculoRepository.existsByUsuarioIdAndEmpresaId(usuario.getId(), empresaId)) {
            throw new RecursoNaoEncontradoException("Empresa não vinculada a este usuário.");
        }
        ConfEmpresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        usuario.setEmpresaAtiva(empresa);
        return toDTO(repository.save(usuario));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Usuario resolveCallerOrThrow(String auth0Sub) {
        return repository.findByAuth0Sub(auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    public UsuarioDTO toDTO(Usuario u) {
        List<UsuarioEmpresaDTO> empresas = vinculoRepository.findByUsuarioId(u.getId())
                .stream()
                .map(v -> UsuarioEmpresaDTO.builder()
                        .id(v.getId())
                        .empresaId(v.getEmpresa().getId())
                        .nmEmpresa(v.getEmpresa().getNmEmpresa())
                        .dtVinculo(v.getDtVinculo())
                        .build())
                .toList();

        EmpresaDTO ativa = null;
        if (u.getEmpresaAtiva() != null) {
            ConfEmpresa e = u.getEmpresaAtiva();
            ativa = EmpresaDTO.builder()
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

        return UsuarioDTO.builder()
                .id(u.getId())
                .auth0Sub(u.getAuth0Sub())
                .email(u.getEmail())
                .nmUsuario(u.getNmUsuario())
                .picture(u.getPicture())
                .tipo(u.getTipo())
                .dtCriacao(u.getDtCriacao())
                .dtAtualizacao(u.getDtAtualizacao())
                .empresaAtiva(ativa)
                .empresas(empresas)
                .build();
    }
}

