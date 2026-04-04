package com.monalisamenezes.icompras.pedidos.repository;

import com.monalisamenezes.icompras.pedidos.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
