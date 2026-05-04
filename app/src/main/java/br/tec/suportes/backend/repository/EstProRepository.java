package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.EstPro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EstProRepository extends JpaRepository<EstPro, EstPro.EstProId> {

    @Query("SELECT COUNT(e) FROM EstPro e WHERE e.qtEstoqueAtual < 100")
    long countSaldoBaixo();
}
