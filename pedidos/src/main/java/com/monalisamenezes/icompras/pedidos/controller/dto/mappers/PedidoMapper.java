package com.monalisamenezes.icompras.pedidos.controller.dto.mappers;

import com.monalisamenezes.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.monalisamenezes.icompras.pedidos.model.Pedido;
import enums.StatusPedido;
import org.jspecify.annotations.NonNull;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = { ItemPedidoMapper.class })
public interface PedidoMapper {

    @Mapping(source = "dadosPagamentoDTO", target = "dadosPagamento")
    Pedido map(NovoPedidoDTO dto);
    
    @AfterMapping
    default void afterMapping(@MappingTarget Pedido pedido) {
        pedido.setStatus(StatusPedido.REALIZADO);
        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }
        
        BigDecimal total = calcularTotal(pedido);
        
        pedido.setTotal(total);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
    }

    private static @NonNull BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItens().stream().map(
                item -> item.getValorUnitario().multiply(new BigDecimal(item.getQuantidade()))
        ).reduce(BigDecimal.ZERO, BigDecimal::add).abs();
    }
}