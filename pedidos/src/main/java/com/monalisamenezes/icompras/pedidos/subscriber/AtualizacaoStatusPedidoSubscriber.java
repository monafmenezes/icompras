package com.monalisamenezes.icompras.pedidos.subscriber;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.monalisamenezes.icompras.pedidos.service.AtualizacaoStatusPedidoService;
import com.monalisamenezes.icompras.pedidos.subscriber.representation.AtualizacaoStatusPedidoRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoSubscriber {

    private final AtualizacaoStatusPedidoService service;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = {"${icompras.config.kafka.topics.pedidos-faturados}",
                    "${icompras.config.kafka.topics.pedidos-enviados}"},
            groupId = "${spring.kafka.consumer.group-id}")
    public void  receberAtualizacao(String json) {
        log.info("Recebendo atualização de status do pedido: {}", json );

        try {
            AtualizacaoStatusPedidoRepresentation atualizacao
                    = objectMapper.readValue(json, AtualizacaoStatusPedidoRepresentation.class);

            service.atualizarStatus(
                    atualizacao.codigo(),
                    atualizacao.status(),
                    atualizacao.urlNotaFiscal(),
                    atualizacao.codigoRastreio()
            );

            log.info("Status do pedido {} atualizado com sucesso!", atualizacao.codigo());

        } catch (Exception e) {
            log.error("Erro ao atualizar status pedido: {}", e.getMessage());
        }
    }

}
