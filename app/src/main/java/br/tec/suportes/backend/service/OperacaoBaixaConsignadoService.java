package br.tec.suportes.backend.service;

import br.tec.suportes.backend.domain.*;
import br.tec.suportes.backend.dto.PagedResponse;
import br.tec.suportes.backend.dto.operacaobaixa.OperacaoBaixaConsignadoDTO;
import br.tec.suportes.backend.dto.operacaobaixa.OperacaoBaixaConsignadoRequest;
import br.tec.suportes.backend.repository.OperacaoBaixaConsignadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperacaoBaixaConsignadoService {

    private final OperacaoBaixaConsignadoRepository repository;

    @Transactional
    public OperacaoBaixaConsignadoDTO salvar(String auth0Sub, Long empresaId, OperacaoBaixaConsignadoRequest req) {
        var operacao = new OperacaoBaixaConsignado();
        operacao.setAuth0Sub(auth0Sub);
        operacao.setEmpresaId(empresaId);
        operacao.setCdMultiEmpresa(req.getCdMultiEmpresa());
        operacao.setCdEstoque(req.getCdEstoque());
        operacao.setDsEstoque(req.getDsEstoque());

        for (var origemReq : req.getOrigens()) {
            var origem = new OperacaoBaixaConsignadoOrigem();
            origem.setOperacao(operacao);
            origem.setCdProduto(origemReq.getCdProduto());
            origem.setDsProduto(origemReq.getDsProduto());
            origem.setQtEstoqueAtual(origemReq.getQtEstoqueAtual());
            operacao.getOrigens().add(origem);
        }

        for (var itemReq : req.getItens()) {
            var item = new OperacaoBaixaConsignadoItem();
            item.setOperacao(operacao);
            item.setCdProduto(itemReq.getCdProduto());
            item.setDsProduto(itemReq.getDsProduto());
            item.setSnLote(itemReq.getSnLote() != null ? itemReq.getSnLote() : "N");
            item.setSnValidade(itemReq.getSnValidade() != null ? itemReq.getSnValidade() : "N");

            for (var linhaReq : itemReq.getLinhas()) {
                var linha = new OperacaoBaixaConsignadoLinha();
                linha.setItem(item);
                linha.setCdFornecedor(linhaReq.getCdFornecedor());
                linha.setNmFornecedor(linhaReq.getNmFornecedor());
                linha.setQuantidade(linhaReq.getQuantidade());
                linha.setLote(linhaReq.getLote());
                linha.setValidade(linhaReq.getValidade());
                item.getLinhas().add(linha);
            }

            operacao.getItens().add(item);
        }

        return OperacaoBaixaConsignadoDTO.from(repository.save(operacao));
    }

    @Transactional(readOnly = true)
    public PagedResponse<OperacaoBaixaConsignadoDTO> listar(Long empresaId, int page, int pageSize) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "dtCriacao"));
        var result   = repository.findByEmpresaId(empresaId, pageable);
        var response = new PagedResponse<OperacaoBaixaConsignadoDTO>();
        response.setDados(result.getContent().stream().map(OperacaoBaixaConsignadoDTO::from).toList());
        response.setPagina(page);
        response.setTamanhoPagina(pageSize);
        response.setTotal(result.getTotalElements());
        return response;
    }

    @Transactional
    public void deletar(Long empresaId, Long id) {
        var operacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada."));
        if (!operacao.getEmpresaId().equals(empresaId)) {
            throw new IllegalArgumentException("Operação não pertence a esta empresa.");
        }
        if ("CONCLUIDO".equals(operacao.getStatus())) {
            throw new IllegalStateException("Não é possível excluir uma operação já concluída.");
        }
        repository.delete(operacao);
    }

    @Transactional
    public OperacaoBaixaConsignadoDTO atualizar(Long empresaId, Long id, OperacaoBaixaConsignadoRequest req) {
        var operacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada."));
        if (!operacao.getEmpresaId().equals(empresaId)) {
            throw new IllegalArgumentException("Operação não pertence a esta empresa.");
        }
        if ("CONCLUIDO".equals(operacao.getStatus())) {
            throw new IllegalStateException("Não é possível editar uma operação já concluída.");
        }

        operacao.setCdMultiEmpresa(req.getCdMultiEmpresa());
        operacao.setCdEstoque(req.getCdEstoque());
        operacao.setDsEstoque(req.getDsEstoque());

        operacao.getOrigens().clear();
        for (var origemReq : req.getOrigens()) {
            var origem = new OperacaoBaixaConsignadoOrigem();
            origem.setOperacao(operacao);
            origem.setCdProduto(origemReq.getCdProduto());
            origem.setDsProduto(origemReq.getDsProduto());
            origem.setQtEstoqueAtual(origemReq.getQtEstoqueAtual());
            operacao.getOrigens().add(origem);
        }

        operacao.getItens().clear();
        for (var itemReq : req.getItens()) {
            var item = new OperacaoBaixaConsignadoItem();
            item.setOperacao(operacao);
            item.setCdProduto(itemReq.getCdProduto());
            item.setDsProduto(itemReq.getDsProduto());
            item.setSnLote(itemReq.getSnLote() != null ? itemReq.getSnLote() : "N");
            item.setSnValidade(itemReq.getSnValidade() != null ? itemReq.getSnValidade() : "N");
            for (var linhaReq : itemReq.getLinhas()) {
                var linha = new OperacaoBaixaConsignadoLinha();
                linha.setItem(item);
                linha.setCdFornecedor(linhaReq.getCdFornecedor());
                linha.setNmFornecedor(linhaReq.getNmFornecedor());
                linha.setQuantidade(linhaReq.getQuantidade());
                linha.setLote(linhaReq.getLote());
                linha.setValidade(linhaReq.getValidade());
                item.getLinhas().add(linha);
            }
            operacao.getItens().add(item);
        }

        return OperacaoBaixaConsignadoDTO.from(repository.save(operacao));
    }
}
