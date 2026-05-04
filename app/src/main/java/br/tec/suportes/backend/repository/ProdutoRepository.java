package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    long countBySnLote(String snLote);

    long countBySnConsignado(String snConsignado);

    @Query("""
            SELECT COUNT(p) FROM Produto p
            WHERE NOT EXISTS (
                SELECT 1 FROM EstPro e
                WHERE e.id.cdProduto = p.cdProduto
                AND e.qtEstoqueAtual > 0
            )
            """)
    long countSemSaldo();
}
