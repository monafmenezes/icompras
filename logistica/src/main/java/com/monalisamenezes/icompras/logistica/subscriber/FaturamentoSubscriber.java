package com.monalisamenezes.icompras.logistica.subscriber;

import com.monalisamenezes.icompras.logistica.service.EnvioPedidoService;
import com.monalisamenezes.icompras.logistica.subscriber.representation.AtualizacaoFaturamentoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class FaturamentoSubscriber {

    private final ObjectMapper mapper;
    private final EnvioPedidoService service;


    @KafkaListener(
            topics = "${icompras.config.kafka.topic.pedidos-faturados}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String json) {
        log.info("Recebendo pedido para envio: {}", json);

        try {
            AtualizacaoFaturamentoRepresentation pedido = mapper.readValue(json, AtualizacaoFaturamentoRepresentation.class);
            service.enviar(pedido.codigo(), pedido.urlNotaFiscal());
            log.info("Pedido para envio: {}", pedido.codigo());
        } catch(Exception e) {
            log.error("Erro ao preparar pedido para envio.", e);
        }

    }
}
