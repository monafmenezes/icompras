package com.monalisamenezes.icompras.pedidos.controller.dto.mappers;

import com.monalisamenezes.icompras.pedidos.controller.dto.ItemPedidoDTO;
import com.monalisamenezes.icompras.pedidos.model.ItemPedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper{
    ItemPedido map(ItemPedidoDTO dto);
}

