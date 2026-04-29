package com.monalisamenezes.icompras.faturamento.service;

import com.monalisamenezes.icompras.faturamento.config.model.Pedido;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotaFiscalService {
    @Value("classpath:reports/nota-fiscal.jrxml")
    private Resource notaFiscal;

    @Value("classpath:images/logo.png")
    private Resource imagem;

    public byte[] gerarNota(Pedido pedido) {
        try (InputStream inputStream = notaFiscal.getInputStream()) {
            Map<String, Object> params = getStringObjectMap(pedido);

            var itensMap = pedido.itens().stream().map(item -> {
                Map<String, Object> m = new HashMap<>();
                m.put("codigo", item.codigo());
                m.put("descricao", item.descricao());
                m.put("quantidade", item.quantidade());
                m.put("valorUnitario", item.valorUnitario());
                m.put("total", item.total());
                return m;
            }).toList();

            var dataSource = new JRBeanCollectionDataSource(itensMap);

            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (RuntimeException | IOException | JRException e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> getStringObjectMap(Pedido pedido) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("NOME", pedido.cliente().nome());
        params.put("CPF", pedido.cliente().cpf());
        params.put("LOGRADOURO", pedido.cliente().logradouro());
        params.put("NUMERO", pedido.cliente().numero());
        params.put("BAIRRO", pedido.cliente().bairro());
        params.put("EMAIL", pedido.cliente().email());
        params.put("TELEFONE", pedido.cliente().telefone());
        params.put("DATA_PEDIDO", pedido.data());
        params.put("TOTAL_PEDIDO", pedido.total());
        params.put("LOGO", imagem.getInputStream());
        return params;
    }
}
