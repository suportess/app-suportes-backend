package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.MvtoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MvtoEstoqueRepository extends JpaRepository<MvtoEstoque, Long> {

    @Query("""
            SELECT m FROM MvtoEstoque m
            WHERE m.dtMvtoEstoque >= :inicio AND m.dtMvtoEstoque < :fim
            ORDER BY m.dtMvtoEstoque DESC
            """)
    List<MvtoEstoque> findByDia(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query("""
            SELECT COUNT(m) FROM MvtoEstoque m
            WHERE m.dtMvtoEstoque >= :inicio AND m.dtMvtoEstoque < :fim
            """)
    long countByDia(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    long countByDtConclusaoIsNull();

    List<MvtoEstoque> findTop10ByOrderByDtMvtoEstoqueDesc();
}
