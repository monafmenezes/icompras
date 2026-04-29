package com.monalisamenezes.icompras.logistica.publisher;

import com.monalisamenezes.icompras.logistica.model.AtualizacaoEnvioPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvioPedidoPublisher {

    //Quando tem final significa que ele vai fazer parte do construtor
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${icompras.config.kafka.topic.pedidos-enviados}")
    private String topico;

    public void enviar(AtualizacaoEnvioPedido atualizacaoEnvioPedido) {
        log.info("Enviando pedido para envio: {}", atualizacaoEnvioPedido.codigo());

        try {
            String json = objectMapper.writeValueAsString(atualizacaoEnvioPedido);
            kafkaTemplate.send(topico, "data", json);
            log.info("Pedido para envio enviado {}, codigo de rastreio: {}",
                    atualizacaoEnvioPedido.codigo(), atualizacaoEnvioPedido.codigoRastreio());

        } catch (Exception e) {
            log.error("Erro ao publicar pedido para envio.", e);
        }
    }
}
