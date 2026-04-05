package com.monalisamenezes.icompras.pedidos.controller.dto;

public record RecebimentoCallbackRecebimentoDTO(
        Long codigo,
        String chavePagamento,
        boolean status,
        String observacoes
) {
}
