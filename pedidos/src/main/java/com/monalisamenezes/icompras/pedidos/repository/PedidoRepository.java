package com.monalisamenezes.icompras.pedidos.repository;

import com.monalisamenezes.icompras.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
