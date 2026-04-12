package com.monalisamenezes.icompras.faturamento.service;

import com.monalisamenezes.icompras.faturamento.bucket.BucketFile;
import com.monalisamenezes.icompras.faturamento.bucket.BucketService;
import com.monalisamenezes.icompras.faturamento.config.model.Pedido;
import com.monalisamenezes.icompras.faturamento.publisher.FaturamentoPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeradorNotaFiscalService {
    private final NotaFiscalService notaFiscalService;
    private final BucketService bucketService;
    private final FaturamentoPublisher publisher;


    public void gerar(Pedido pedido) {
        try {
            byte[] bytes = notaFiscalService.gerarNota(pedido);

            String nomeArquivo = String.format("notafiscal_pedido_%d.pdf", pedido.codigo());

            BucketFile bucketFile = new BucketFile(
                    nomeArquivo, new ByteArrayInputStream(bytes), MediaType.APPLICATION_PDF, (long) bytes.length);

            bucketService.upload(bucketFile);

            String url = bucketService.getUrl(nomeArquivo);

            publisher.publicar(pedido, url);

            log.info("Gerada a nota fiscal para o pedido {}: ", pedido.codigo());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
