package com.monalisamenezes.icompras.faturamento.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monalisamenezes.icompras.faturamento.GeradorNotaFiscalService;
import com.monalisamenezes.icompras.faturamento.config.model.Pedido;
import com.monalisamenezes.icompras.faturamento.mapper.PedidoMapper;
import com.monalisamenezes.icompras.faturamento.subscriber.representation.DetalhePedidoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoPagoSubscriber {

    private final ObjectMapper mapper;
    private final PedidoMapper pedidoMapper;
    private final GeradorNotaFiscalService service;

    @KafkaListener(topics = "${icompras.config.kafka.topic.pedidos-pagos}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String json) {
        try {
            log.info("Recebendo pedido para faturamento: {}", json);
            DetalhePedidoRepresentation representation = mapper.readValue(json, DetalhePedidoRepresentation.class);
            Pedido pedido = pedidoMapper.map(representation);
            service.gerar(pedido);
        } catch (Exception e) {
            log.error("Erro na consumação do topico de pedidos pagos: {}", e.getMessage());
        }
    }
}
