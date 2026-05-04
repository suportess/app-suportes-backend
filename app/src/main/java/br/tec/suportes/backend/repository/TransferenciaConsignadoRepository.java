package br.tec.suportes.backend.repository;

import br.tec.suportes.backend.domain.TransferenciaConsignado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaConsignadoRepository extends JpaRepository<TransferenciaConsignado, Long> {

    Page<TransferenciaConsignado> findByEmpresaIdOrderByDtCriacaoDesc(Long empresaId, Pageable pageable);
}
