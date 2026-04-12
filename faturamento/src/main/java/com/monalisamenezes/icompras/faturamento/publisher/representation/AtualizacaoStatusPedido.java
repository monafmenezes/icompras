package com.monalisamenezes.icompras.faturamento.publisher.representation;

import com.monalisamenezes.icompras.faturamento.publisher.enums.StatusPedido;

public record AtualizacaoStatusPedido(Long codigo, StatusPedido status, String urlNotaFiscal) {
}
