package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.ItVinculoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItVinculoProdutoRepository extends JpaRepository<ItVinculoProduto, Long> {

    List<ItVinculoProduto> findByCdSessaoOrderByNrLinha(Long cdSessao);
}
