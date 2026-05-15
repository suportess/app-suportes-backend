package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ImportacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportacaoProdutoRepository extends JpaRepository<ImportacaoProduto, Long> {

    List<ImportacaoProduto> findByAuth0SubOrderByDtImportacaoDesc(String auth0Sub);

    List<ImportacaoProduto> findAllByOrderByDtImportacaoDesc();

    List<ImportacaoProduto> findByLoteIdOrderByDtImportacaoAsc(Long loteId);
}
