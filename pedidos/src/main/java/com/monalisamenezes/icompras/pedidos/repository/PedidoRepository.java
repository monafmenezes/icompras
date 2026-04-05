package com.monalisamenezes.icompras.pedidos.repository;

import com.monalisamenezes.icompras.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido>findByCodigoAndChavePagamento(Long codigo, String chavePagamento);
}
