package com.monalisamenezes.icompras.produtos.service;

import com.monalisamenezes.icompras.produtos.dto.ProdutoDTO;
import com.monalisamenezes.icompras.produtos.model.Produto;
import com.monalisamenezes.icompras.produtos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoDTO salvar(ProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setValorUnitario(dto.valorUnitario());

        produto.setDescricao(dto.descricao());

        produto.setAtivo(true);

        Produto produtoSalvo = repository.save(produto);

        return new ProdutoDTO(
                produtoSalvo.getCodigo(),
                produtoSalvo.getNome(),
                produtoSalvo.getValorUnitario(),
                produtoSalvo.getAtivo(),
                produtoSalvo.getDescricao()
        );
    }

    public Optional<Produto> obterPorCodigo(Long codigo) {
        return repository.findById(codigo);
    }

    public List<ProdutoDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(p -> new ProdutoDTO(
                        p.getCodigo(),
                        p.getNome(),
                        p.getValorUnitario(),
                        p.getAtivo(),
                        p.getDescricao()))
                .toList();
    }

    public void delete(Long codigo) {
        Produto produto = repository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado com o código: " + codigo
                ));

        produto.setAtivo(false);

        repository.save(produto);
    }
}