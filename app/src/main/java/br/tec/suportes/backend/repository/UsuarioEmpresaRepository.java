package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.UsuarioEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresa, Long> {

    List<UsuarioEmpresa> findByUsuarioAuth0Sub(String auth0Sub);

    List<UsuarioEmpresa> findByUsuarioId(Long usuarioId);

    Optional<UsuarioEmpresa> findByUsuarioAuth0SubAndEmpresaId(String auth0Sub, Long empresaId);

    boolean existsByUsuarioAuth0SubAndEmpresaId(String auth0Sub, Long empresaId);

    boolean existsByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    void deleteByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);
}

