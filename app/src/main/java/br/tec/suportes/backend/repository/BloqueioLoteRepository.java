package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.BloqueioLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloqueioLoteRepository extends JpaRepository<BloqueioLote, Long> {
    List<BloqueioLote> findByAuth0SubOrderByDtBloqueioDesc(String auth0Sub);
}
