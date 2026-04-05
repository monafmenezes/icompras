package com.monalisamenezes.icompras.pedidos.service;

import com.monalisamenezes.icompras.pedidos.client.ServicoBancarioClient;
import com.monalisamenezes.icompras.pedidos.model.Pedido;

import com.monalisamenezes.icompras.pedidos.repository.ItemPedidoRepository;
import com.monalisamenezes.icompras.pedidos.repository.PedidoRepository;
import com.monalisamenezes.icompras.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido criarPedido(@RequestBody Pedido pedido) {
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        String chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        repository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }
}
