package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.TransferenciaConsignado;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.transferencia.TransferenciaConsignadoDTO;
import br.tec.suportes.backend.dto.transferencia.TransferenciaConsignadoRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.portal.service.PortalMvService;
import br.tec.suportes.backend.repository.TransferenciaConsignadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferenciaConsignadoService {

    private final TransferenciaConsignadoRepository repository;
    private final PortalMvService portalMvService;

    /** Salva como PENDENTE (passo 3 → passo 4). */
    @Transactional
    public TransferenciaConsignadoDTO salvar(
            String auth0Sub, Long empresaId,
            TransferenciaConsignadoRequest req
    ) {
        TransferenciaConsignado entity = new TransferenciaConsignado();
        entity.setAuth0Sub(auth0Sub);
        entity.setEmpresaId(empresaId);
        entity.setStatus("PENDENTE");
        entity.setCdMultiEmpresa(req.getCdMultiEmpresa());
        entity.setCdEstoque(req.getCdEstoque());
        entity.setDsEstoque(req.getDsEstoque());
        entity.setCdProdutoDev(req.getCdProdutoDev());
        entity.setDsProdutoDev(req.getDsProdutoDev());
        entity.setCdEntPro(req.getCdEntPro());
        entity.setCdLoteDev(req.getCdLoteDev());
        entity.setDtValidadeDev(req.getDtValidadeDev());
        entity.setQtDevolvida(req.getQtDevolvida());
        entity.setCdProdutoEnt(req.getCdProdutoEnt());
        entity.setDsProdutoEnt(req.getDsProdutoEnt());
        entity.setCdLoteEnt(req.getCdLoteEnt());
        entity.setDtValidadeEnt(req.getDtValidadeEnt());
        entity.setQtEntrada(req.getQtEntrada());
        entity.setDtDevolucao(req.getDtDevolucao());
        return TransferenciaConsignadoDTO.from(repository.save(entity));
    }

    /** Lista paginada para a empresa (20/página). */
    public PagedResponse<TransferenciaConsignadoDTO> listar(Long empresaId, int page, int pageSize) {
        var pg = repository.findByEmpresaIdOrderByDtCriacaoDesc(
                empresaId, PageRequest.of(page - 1, pageSize));
        List<TransferenciaConsignadoDTO> dados = pg.getContent()
                .stream().map(TransferenciaConsignadoDTO::from).toList();
        PagedResponse<TransferenciaConsignadoDTO> res = new PagedResponse<>();
        res.setDados(dados);
        res.setPagina(page);
        res.setTamanhoPagina(pageSize);
        res.setTotal(pg.getTotalElements());
        return res;
    }

    /** Executa a operação no portal e marca como CONCLUIDO. */
    @Transactional
    public TransferenciaConsignadoDTO concluir(
            String auth0Sub, Long empresaId, Long id
    ) {
        TransferenciaConsignado entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Transferência não encontrada: " + id));

        if (!"PENDENTE".equals(entity.getStatus())) {
            throw new IllegalStateException("Transferência já foi concluída ou está em estado inválido.");
        }

        // Chama o portal — lança exceção em caso de erro (rollback automático)
        portalMvService.devolverProdutoConsignado(
                auth0Sub, empresaId,
                entity.getCdEstoque(),
                entity.getCdMultiEmpresa(),
                entity.getCdEntPro(),
                entity.getCdProdutoDev(),
                entity.getCdLoteDev(),
                entity.getDtValidadeDev(),
                entity.getQtDevolvida(),
                entity.getCdProdutoEnt(),
                entity.getCdLoteEnt(),
                entity.getDtValidadeEnt(),
                entity.getQtEntrada(),
                entity.getDtDevolucao()
        );

        entity.setStatus("CONCLUIDO");
        entity.setDtConclusao(Instant.now());
        return TransferenciaConsignadoDTO.from(repository.save(entity));
    }
}
