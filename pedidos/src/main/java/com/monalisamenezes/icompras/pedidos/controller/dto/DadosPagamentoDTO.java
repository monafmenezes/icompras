package com.monalisamenezes.icompras.pedidos.controller.dto;

import com.monalisamenezes.icompras.pedidos.model.enums.TipoPagamento;

public record DadosPagamentoDTO(
        String dados,
        TipoPagamento tipoPagamento) {
}
