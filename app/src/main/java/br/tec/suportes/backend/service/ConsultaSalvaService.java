package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.ConsultaSalva;
import br.tec.suportes.backend.dto.consulta.ConsultaSalvaDTO;
import br.tec.suportes.backend.dto.consulta.ConsultaSalvaRequest;
import br.tec.suportes.backend.exception.RecursoNaoEncontradoException;
import br.tec.suportes.backend.repository.ConsultaSalvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaSalvaService {

    private final ConsultaSalvaRepository repository;

    public ConsultaSalvaDTO salvar(String auth0Sub, ConsultaSalvaRequest req) {
        var entity = new ConsultaSalva();
        entity.setAuth0Sub(auth0Sub);
        entity.setNome(req.nome().strip());
        entity.setSqlTexto(req.sqlTexto().strip());
        return ConsultaSalvaDTO.from(repository.save(entity));
    }

    public List<ConsultaSalvaDTO> listar(String auth0Sub) {
        return repository.findByAuth0SubOrderByDtCriacaoDesc(auth0Sub)
                .stream()
                .map(ConsultaSalvaDTO::from)
                .toList();
    }

    public ConsultaSalvaDTO atualizar(String auth0Sub, Long id, ConsultaSalvaRequest req) {
        var entity = repository.findByIdAndAuth0Sub(id, auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
        entity.setNome(req.nome().strip());
        entity.setSqlTexto(req.sqlTexto().strip());
        entity.setDtAtualizacao(Instant.now());
        return ConsultaSalvaDTO.from(repository.save(entity));
    }

    public void deletar(String auth0Sub, Long id) {
        var entity = repository.findByIdAndAuth0Sub(id, auth0Sub)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
        repository.delete(entity);
    }
}
