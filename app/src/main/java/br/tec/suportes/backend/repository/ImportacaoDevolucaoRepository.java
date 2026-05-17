package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ImportacaoDevolucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportacaoDevolucaoRepository extends JpaRepository<ImportacaoDevolucao, Long> {

    List<ImportacaoDevolucao> findTop50ByOrderByDtImportacaoDesc();

    Page<ImportacaoDevolucao> findAllByOrderByDtImportacaoDesc(Pageable pageable);
}
