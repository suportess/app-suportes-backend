package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.OperacaoBaixaConsignado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperacaoBaixaConsignadoRepository extends JpaRepository<OperacaoBaixaConsignado, Long> {
    Page<OperacaoBaixaConsignado> findByEmpresaId(Long empresaId, Pageable pageable);
}
