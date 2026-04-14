package com.monalisamenezes.icompras.produtos.controller;

import com.monalisamenezes.icompras.produtos.dto.ProdutoDTO;
import com.monalisamenezes.icompras.produtos.model.Produto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.monalisamenezes.icompras.produtos.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoService service;

    @PostMapping
    public ResponseEntity<ProdutoDTO> salvar(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO produto = service.salvar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        List<ProdutoDTO> lista = service.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Produto> findId(@PathVariable Long codigo) {
        return service.obterPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable Long codigo) {
        service.delete(codigo);
        return ResponseEntity.noContent().build();
    }


}
