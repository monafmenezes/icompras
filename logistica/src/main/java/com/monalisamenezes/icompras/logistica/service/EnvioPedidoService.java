package com.monalisamenezes.icompras.logistica.service;

import com.monalisamenezes.icompras.logistica.model.AtualizacaoEnvioPedido;
import com.monalisamenezes.icompras.logistica.model.StatusPedido;
import com.monalisamenezes.icompras.logistica.publisher.EnvioPedidoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EnvioPedidoService {
    private final EnvioPedidoPublisher publisher;


    public void enviar(Long codigoPedido, String urlNotaFiscal) {

        String codigoRastreio = gerarCodigoRastreio();

        AtualizacaoEnvioPedido atualizacaoEnvioPedido
                = new AtualizacaoEnvioPedido(codigoPedido, StatusPedido.ENVIADO, codigoRastreio);

        publisher.enviar(atualizacaoEnvioPedido);
    }

    private String gerarCodigoRastreio() {
        var random = new Random();

        return String.valueOf((char) ('A' + random.nextInt(26))) +
                (char) ('A' + random.nextInt(26)) +
                String.format("%08d", random.nextInt(100000000)) +
                "BR";
    }
}
