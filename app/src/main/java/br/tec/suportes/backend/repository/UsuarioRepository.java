package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByAuth0Sub(String auth0Sub);
}
