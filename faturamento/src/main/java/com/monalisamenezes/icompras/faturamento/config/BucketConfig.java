package com.monalisamenezes.icompras.faturamento.config;

import com.monalisamenezes.icompras.faturamento.config.props.MinioProps;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfig {
    @Autowired
    MinioProps props;

    @Bean
    public MinioClient bucketClient() {
        try {
            return MinioClient.builder()
                    .endpoint(props.getUrl())
                    .credentials
                    (props.getAccessKey(), props.getSecretKey())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
