package com.monalisamenezes.icompras.pedidos.controller;

import com.monalisamenezes.icompras.pedidos.controller.dto.RecebimentoCallbackRecebimentoDTO;
import com.monalisamenezes.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos/callback-pagamentos")
@RequiredArgsConstructor
public class RecebimentoCallbackPagamentoController {

    private final PedidoService service;

    @PostMapping
    public ResponseEntity<Object> atualizarStatusPagamento(
            @RequestBody RecebimentoCallbackRecebimentoDTO body,
            @RequestHeader(required = true, name = "apiKey") String apiKey) {

        service.atualizarStatusPagamento(
                body.codigo(),
                body.chavePagamento(),
                body.status(),
                body.observacoes()
        );

        return ResponseEntity.ok().build();
    }
}
