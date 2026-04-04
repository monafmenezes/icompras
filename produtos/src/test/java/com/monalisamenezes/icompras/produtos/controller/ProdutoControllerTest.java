package com.monalisamenezes.icompras.produtos.controller;

import com.monalisamenezes.icompras.produtos.dto.ProdutoDTO;
import com.monalisamenezes.icompras.produtos.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;


    @Test
    @DisplayName("Deve retornar 400 bad Request quando o preço for zero.")
    void shouldReturnBadRequestWhenPriceIsZero() throws Exception {
        String body = """
                {
                    "nome": "Teclado",
                    "valorUnitario": 0.00
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 bad Request quando o nome for nulo ou string vazia.")
    void shouldReturnBadRequestWhenNameIsNullOrBlank() throws Exception {
        String body = """
                {
                    "nome": "",
                    "valorUnitario": 1,
                    "descricao": "Teclado sem fio."
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Deve retornar 400 bad Request quando o valor unitário for nulo.")
    void shouldReturnBadRequestWhenValueIsNull() throws Exception {
        String body = """
                {
                    "nome": "Teclado",
                    "valorUnitario": null
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 bad Request quando o valor unitário for negativo.")
    void shouldReturnBadRequestWhenValueIsNegative() throws Exception {
        String body = """
                {
                    "nome": "Teclado",
                    "valorUnitario": -1
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar uma listagem de produtos")
    void shouldReturnStatusOk() throws Exception {
        ProdutoDTO p1 = new ProdutoDTO(1L, "Teclado", new BigDecimal("180.00"), null);
        when(produtoService.findAll()).thenReturn(List.of(p1));

        mockMvc.perform(get("/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Teclado"));
    }

    @Test
    @DisplayName("Deve retornar 201 ao criar um novo produto")
    void shouldReturnStatusCreated() throws Exception {
        ProdutoDTO retorno = new ProdutoDTO(1L, "Teclado", new BigDecimal("180.00"), null);
        when(produtoService.salvar(any())).thenReturn(retorno);

        String body = """
                {
                    "nome": "Teclado",
                    "valorUnitario": 180.00
                }
                """;
        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar um produto por código existente")
    void shouldReturnProductWhenIdExists() throws Exception {
        Long codigo = 1L;
        ProdutoDTO p1 = new ProdutoDTO(codigo, "Teclado", new BigDecimal("180.00"), "Sem fio");

        when(produtoService.obterPorCodigo(codigo)).thenReturn(java.util.Optional.of(p1));

        mockMvc.perform(get("/produtos/{codigo}", codigo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigo))
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao buscar um produto por código inexistente")
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        Long codigoInexistente = 99L;

        when(produtoService.obterPorCodigo(codigoInexistente)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/produtos/{codigo}", codigoInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
