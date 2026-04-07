package com.monalisamenezes.icompras.pedidos.controller.dto;

import java.util.List;

public record NovoPedidoDTO(
        Long codigoCliente,
        String observacoes,
        DadosPagamentoDTO dadosPagamento,
        List<ItemPedidoDTO> itens) {
}
