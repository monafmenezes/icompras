package com.monalisamenezes.icompras.faturamento.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monalisamenezes.icompras.faturamento.config.model.Pedido;
import com.monalisamenezes.icompras.faturamento.publisher.enums.StatusPedido;
import com.monalisamenezes.icompras.faturamento.publisher.representation.AtualizacaoStatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FaturamentoPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${icompras.config.kafka.topic.pedidos-faturados}")
    private String topico;

    public void publicar(Pedido pedido, String urlNotaFiscal) {
       try {
           AtualizacaoStatusPedido atualizacaoStatusPedido = new AtualizacaoStatusPedido(
                   pedido.codigo(),
                   StatusPedido.FATURADO,
                   urlNotaFiscal
           );

           String json = objectMapper.writeValueAsString(atualizacaoStatusPedido);
           kafkaTemplate.send(topico, "data", json);
       } catch (Exception e) {
           log.error(e.getMessage(), e);
       }

    }
}
