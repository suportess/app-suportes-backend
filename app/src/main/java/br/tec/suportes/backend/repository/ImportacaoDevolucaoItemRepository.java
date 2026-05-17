package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ImportacaoDevolucaoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportacaoDevolucaoItemRepository extends JpaRepository<ImportacaoDevolucaoItem, Long> {
    List<ImportacaoDevolucaoItem> findByCdSessaoOrderByNrLinha(Long cdSessao);
}
