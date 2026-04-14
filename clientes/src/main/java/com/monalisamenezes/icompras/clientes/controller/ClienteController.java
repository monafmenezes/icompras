package com.monalisamenezes.icompras.clientes.controller;

import com.monalisamenezes.icompras.clientes.dto.ClienteDTO;
import com.monalisamenezes.icompras.clientes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteDTO> salvar(@RequestBody ClienteDTO ClienteDTO) {
        ClienteDTO produto = service.store(ClienteDTO);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ClienteDTO> findByCodigo(@PathVariable Long codigo) {
        return service.findById(codigo).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> findAll() {
        List<ClienteDTO> lista = service.findAll();

        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable Long codigo) {
        service.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
