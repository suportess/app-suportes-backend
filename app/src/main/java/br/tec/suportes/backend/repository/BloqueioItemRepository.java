package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.BloqueioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloqueioItemRepository extends JpaRepository<BloqueioItem, Long> {
    List<BloqueioItem> findByLoteIdOrderById(Long loteId);
}
