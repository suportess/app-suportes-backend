package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ConsultaSalva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultaSalvaRepository extends JpaRepository<ConsultaSalva, Long> {
    List<ConsultaSalva> findByAuth0SubOrderByDtCriacaoDesc(String auth0Sub);
    Optional<ConsultaSalva> findByIdAndAuth0Sub(Long id, String auth0Sub);
}
