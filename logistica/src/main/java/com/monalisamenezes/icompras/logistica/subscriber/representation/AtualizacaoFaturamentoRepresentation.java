package com.monalisamenezes.icompras.logistica.subscriber.representation;

import com.monalisamenezes.icompras.logistica.model.StatusPedido;

public record AtualizacaoFaturamentoRepresentation(Long codigo, StatusPedido status, String urlNotaFiscal) {
}
