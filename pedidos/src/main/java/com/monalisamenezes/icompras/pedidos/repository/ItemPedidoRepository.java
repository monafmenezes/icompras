package com.monalisamenezes.icompras.pedidos.repository;

import com.monalisamenezes.icompras.pedidos.model.ItemPedido;
import com.monalisamenezes.icompras.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido(Pedido pedido);
}
