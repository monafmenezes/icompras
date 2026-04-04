package com.monalisamenezes.icompras.produtos.service;

import com.monalisamenezes.icompras.produtos.dto.ProdutoDTO;
import com.monalisamenezes.icompras.produtos.model.Produto;
import com.monalisamenezes.icompras.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {
    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    @DisplayName("Deve salvar um produto com sucesso e retornar o DTO com ID.")
    void shouldSaveProductSuccessfully () {
        ProdutoDTO produtoDTO = new ProdutoDTO(
                null,
                "Teclado",
                new BigDecimal("150.00"),
                "Teclado sem fio."
        );

        Produto produto = new Produto();
        produto.setCodigo(1L);
        produto.setNome(produtoDTO.nome());
        produto.setValorUnitario(produtoDTO.valorUnitario());
        produto.setDescricao(produtoDTO.descricao());

        when(repository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO result = service.salvar(produtoDTO);

        assertNotNull(result);
        assertEquals(1L, result.codigo());
        assertEquals(produtoDTO.nome(), result.nome());
        assertEquals(produtoDTO.valorUnitario(), result.valorUnitario());
        assertEquals(produtoDTO.descricao(), result.descricao());

        verify(repository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve listar o produto com o código recebido.")
    void shouldListProductByCode() {
        Long codigo = 1L;

        Produto produto = new Produto();
        produto.setCodigo(codigo);
        produto.setNome("Teclado");
        produto.setValorUnitario(new BigDecimal("150.00"));
        produto.setDescricao("Teclado sem fio.");

        when(repository.findById(codigo)).thenReturn(java.util.Optional.of(produto));

        ProdutoDTO result = service.obterPorCodigo(codigo).orElse(null);

        assertNotNull(result);
        assertEquals(codigo, result.codigo());
        assertEquals("Teclado", result.nome());

        verify(repository, times(1)).findById(codigo);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void shouldListAllProducts() {
        Produto produto1 = new Produto();
        produto1.setCodigo(1L);
        produto1.setNome("Produto 1");
        produto1.setValorUnitario(new BigDecimal("100.00"));
        produto1.setDescricao("Descrição do produto 1");

        Produto produto2 = new Produto();
        produto2.setCodigo(2L);
        produto2.setNome("Produto 2");
        produto2.setValorUnitario(new BigDecimal("200.00"));
        produto2.setDescricao("Descrição do produto 2");

        when(repository.findAll()).thenReturn(java.util.List.of(produto1, produto2));

        List<ProdutoDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(repository, times(1)).findAll();

        for (int i = 0; i < result.size(); i++) {
            assertEquals(Long.valueOf(i + 1), result.get(i).codigo());
            assertEquals("Produto " + (i + 1), result.get(i).nome());
            assertEquals(new BigDecimal((i+1) + "00.00"), result.get(i).valorUnitario());
            assertEquals("Descrição do produto " + (i + 1), result.get(i).descricao());
        }

        verify(repository, times(1)).findAll();
    }
}
