package com.monalisamenezes.icompras.pedidos.model;

import com.monalisamenezes.icompras.pedidos.model.enums.TipoPagamento;
import lombok.Data;

@Data
public class DadosPagamento {
    private String dados;
    private TipoPagamento tipoPagamento;
}
