# Portas e Dependencias

## Microservicos

| Servico | Porta | Dependencias principais |
|---|---:|---|
| pedidos | 8080 | PostgreSQL, Kafka, clientes, produtos |
| produtos | 8081 | PostgreSQL |
| clientes | 8082 | PostgreSQL |
| faturamento | 8083 | Kafka, MinIO |
| logistica | 8084 | Kafka |

## Infraestrutura

| Recurso | Porta(s) | Finalidade |
|---|---|---|
| PostgreSQL | 5555 | Persistencia dos servicos |
| Kafka broker | 29092 | Mensageria/eventos |
| Kafka UI | 8090 | Inspecao de topicos |
| MinIO API | 9000 | Armazenamento de objetos |
| MinIO Console | 9001 | Console administrativo |
| Zookeeper | 2181 | Coordenacao do Kafka |

## Ordem recomendada de inicializacao

1. Infraestrutura (`docker compose up -d`).
2. Aplicacoes (`docker compose --profile apps up -d --build`).
3. Validacao de health checks em `/actuator/health`.
