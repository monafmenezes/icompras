package com.monalisamenezes.icompras.produtos.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.monalisamenezes.icompras.produtos.dto.ProdutoDTO;
import lombok.RequiredArgsConstructor;
import com.monalisamenezes.icompras.produtos.model.Produto;
import org.springframework.stereotype.Service;
import com.monalisamenezes.icompras.produtos.repository.ProdutoRepository;

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

        Produto produtoSalvo = repository.save(produto);

        System.out.println(produtoSalvo);

        return new ProdutoDTO(
                produtoSalvo.getCodigo(),
                produtoSalvo.getNome(),
                produtoSalvo.getValorUnitario(),
                produtoSalvo.getDescricao()
        );
    }

    public Optional<ProdutoDTO> obterPorCodigo(Long codigo) {

        return repository.findById(codigo)
                .map(p -> new ProdutoDTO(p.getCodigo(), p.getNome(), p.getValorUnitario(), p.getDescricao()));
    }

    public List<ProdutoDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(p -> new ProdutoDTO(p.getCodigo(), p.getNome(), p.getValorUnitario(), p.getDescricao()))
                .toList();
    }
}
