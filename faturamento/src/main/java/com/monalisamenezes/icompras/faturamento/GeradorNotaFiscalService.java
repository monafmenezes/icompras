package com.monalisamenezes.icompras.faturamento;

import com.monalisamenezes.icompras.faturamento.config.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeradorNotaFiscalService {
    public void gerar(Pedido pedido) {
        log.info("Gerada a nota fiscal para o pedido {}: ", pedido.codigo());
    }
}
