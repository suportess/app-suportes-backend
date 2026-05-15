package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ImportacaoLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportacaoLoteRepository extends JpaRepository<ImportacaoLote, Long> {

    List<ImportacaoLote> findByAuth0SubOrderByDtImportacaoDesc(String auth0Sub);

    List<ImportacaoLote> findAllByOrderByDtImportacaoDesc();
}
