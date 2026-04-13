package com.monalisamenezes.icompras.pedidos.service;

import com.monalisamenezes.icompras.pedidos.model.enums.StatusPedido;
import com.monalisamenezes.icompras.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {
   private final PedidoRepository repository;

   @Transactional
   public void atualizarStatus(
           Long codigo, StatusPedido status, String urlNotaFiscal, String codigoRastreio) {
      repository.findById(codigo).ifPresent(pedido -> {
         pedido.setStatus(status);
         pedido.setUrlNf(urlNotaFiscal);
         pedido.setCodigoRastreio(codigoRastreio);
      });
   }

}
