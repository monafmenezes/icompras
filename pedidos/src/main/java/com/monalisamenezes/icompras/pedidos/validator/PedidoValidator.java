package com.monalisamenezes.icompras.pedidos.validator;

import com.monalisamenezes.icompras.pedidos.client.ClientesClient;
import com.monalisamenezes.icompras.pedidos.client.ProdutosClient;
import com.monalisamenezes.icompras.pedidos.client.representation.ClienteRepresentation;
import com.monalisamenezes.icompras.pedidos.client.representation.ProdutoRepresentation;
import com.monalisamenezes.icompras.pedidos.model.ItemPedido;
import com.monalisamenezes.icompras.pedidos.model.Pedido;
import com.monalisamenezes.icompras.pedidos.model.exception.ValidationException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PedidoValidator {
    private final ProdutosClient produtosClient;
    private final ClientesClient clientesClient;

    public void validar(Pedido pedido) {
        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);
        pedido.getItens().forEach(this::validarItem);
    }

    private void validarCliente(Long codigoCliente) {
        try {
            ResponseEntity<ClienteRepresentation> response = clientesClient.findByCodigo(codigoCliente);
            ClienteRepresentation cliente = response.getBody();
            log.info("Cliente de codigo {} encontrado: {}", cliente.codigo(), cliente.nome());
        } catch (FeignException.NotFound e) {
            String message = String.format("Cliente de codigo %d não encontrado", codigoCliente);
            throw new ValidationException("codigoCliente", message);
        }
    }

    private void validarItem(ItemPedido item) {
        try {
            ResponseEntity<ProdutoRepresentation> response = produtosClient.obterDados(item.getCodigoProduto());
            ProdutoRepresentation produto = response.getBody();
            log.info("Produto de codigo {} encontrado: {}", produto.codigo(), produto.nome());
        } catch (FeignException.NotFound e) {
            String message = String.format("Produto de codigo %d não encontrado", item.getCodigoProduto());
            throw new ValidationException("codigoProduto", message);
        }
    }
}

