package com.monalisamenezes.icompras.pedidos.service;

import com.monalisamenezes.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.monalisamenezes.icompras.pedidos.model.Pedido;

import com.monalisamenezes.icompras.pedidos.repository.ItemPedidoRepository;
import com.monalisamenezes.icompras.pedidos.repository.PedidoRepository;
import com.monalisamenezes.icompras.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;

    public Pedido criarPedido(@RequestBody Pedido pedido) {
        validator.validar(pedido);
        repository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());

        return pedido;
    }
}
