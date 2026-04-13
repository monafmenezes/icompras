package com.monalisamenezes.icompras.pedidos.publisher.representation;

import com.monalisamenezes.icompras.pedidos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public record DetalhePedidoRepresentation(
        Long codigo,
        Long codigoCliente,
        String nome,
        String cpf,
        String logradouro,
        String numero,
        String bairro,
        String email,
        String telefone,
        String dataPedido,
        BigDecimal total,
        StatusPedido status,
        String urlNf,
        String codigoRastreio,
        List<DetalheItemPedidoRepresentation> itens
) {
}
