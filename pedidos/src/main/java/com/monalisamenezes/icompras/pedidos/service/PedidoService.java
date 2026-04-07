package com.monalisamenezes.icompras.pedidos.service;

import com.monalisamenezes.icompras.pedidos.client.ServicoBancarioClient;
import com.monalisamenezes.icompras.pedidos.model.DadosPagamento;
import com.monalisamenezes.icompras.pedidos.model.Pedido;

import com.monalisamenezes.icompras.pedidos.model.enums.TipoPagamento;
import com.monalisamenezes.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.monalisamenezes.icompras.pedidos.repository.ItemPedidoRepository;
import com.monalisamenezes.icompras.pedidos.repository.PedidoRepository;
import com.monalisamenezes.icompras.pedidos.validator.PedidoValidator;
import enums.StatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    private final PedidoRepository repository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido criarPedido(@RequestBody Pedido pedido) {
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        String chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        repository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }

    public void atualizarStatusPagamento(
            Long codigoPedido, String chavePagamento, boolean sucesso, String observacoes) {
        Optional<Pedido> pedidoEncontrado = repository.findByCodigoAndChavePagamento(codigoPedido, chavePagamento);

        if (pedidoEncontrado.isEmpty()) {
            String msg = String.format("Pedido não encontrado para o codigo %d e chave de pagamento %s",
                    codigoPedido, chavePagamento);
            log.error(msg);
            return;
        } else {
            Pedido pedido = pedidoEncontrado.get();

            if (sucesso) {
                pedido.setStatus(StatusPedido.PAGO);
                pedido.setObservacoes(observacoes);
            } else {
                pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
                pedido.setObservacoes(observacoes);
            }

            repository.save(pedido);
        }
    }

    @Transactional
    public void adicionarNovoPagamento(Long codigoPedido, String dadosCartao, TipoPagamento tipoPagamento) {
        var pedidoEncontrado = repository.findById(codigoPedido);

        if (pedidoEncontrado.isEmpty()) {
            throw new ItemNaoEncontradoException("Pedido não encontrado para o codigo: " + codigoPedido);
        }
            Pedido pedido = pedidoEncontrado.get();

        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setDados(dadosCartao);
        dadosPagamento.setTipoPagamento(tipoPagamento);

        pedido.setDadosPagamento(dadosPagamento);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setObservacoes("Novo pagamento realizado, aguardando o processamento.");

        String novaChavePagamento = servicoBancarioClient.solicitarPagamento(pedido);

        pedido.setChavePagamento(novaChavePagamento);
        repository.save(pedido);
    }
}

