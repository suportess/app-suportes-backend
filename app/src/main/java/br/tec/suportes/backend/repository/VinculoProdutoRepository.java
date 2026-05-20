package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.VinculoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VinculoProdutoRepository extends JpaRepository<VinculoProduto, Long> {

    Page<VinculoProduto> findAllByOrderByDtVinculoDesc(Pageable pageable);
}
