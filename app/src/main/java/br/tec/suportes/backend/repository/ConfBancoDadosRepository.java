package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ConfBancoDados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfBancoDadosRepository extends JpaRepository<ConfBancoDados, Long> {

    Optional<ConfBancoDados> findByEmpresaId(Long empresaId);

    boolean existsByEmpresaId(Long empresaId);
}
