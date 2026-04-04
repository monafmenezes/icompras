package com.monalisamenezes.icompras.pedidos.controller;


import com.monalisamenezes.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.monalisamenezes.icompras.pedidos.controller.dto.mappers.PedidoMapper;
import com.monalisamenezes.icompras.pedidos.model.Pedido;
import com.monalisamenezes.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService service;
    private final PedidoMapper mapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO novoPedidoDTO) {
        Pedido pedido = mapper.map(novoPedidoDTO);
        Pedido novoPedido = service.criarPedido(pedido);
        return ResponseEntity.ok(novoPedido.getCodigo());
    }
}
