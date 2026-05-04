package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ConfEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfEmpresaRepository extends JpaRepository<ConfEmpresa, Long> {

    Optional<ConfEmpresa> findFirstBy();
}
