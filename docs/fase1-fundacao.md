# Fase 1 - Fundacao e Padronizacao

Este documento registra o que foi implementado na Fase 1 para padronizar os microservicos do iCompras.

## Checklist de Implementacao

- [x] Padronizar versoes do Spring Boot entre todos os microservicos.
- [x] Criar um `docker-compose.yml` raiz para subir a stack local.
- [x] Criar profiles `local`, `docker` e `test`.
- [x] Padronizar variaveis de ambiente.
- [x] Adicionar Spring Boot Actuator em todos os servicos.
- [x] Padronizar logs e mensagens de erro.
- [x] Revisar nomes de classes/configuracoes com typos ou inconsistencias.
- [x] Criar documentacao de portas e dependencias.
- [x] Criar colecao Postman, Insomnia ou arquivos `.http`.

## Escopo Aplicado

### 1) Versoes e dependencias

- Todos os `pom.xml` foram alinhados para `spring-boot-starter-parent` na versao `3.3.4`.
- Foi adicionado `spring-boot-starter-actuator` em:
  - `clientes`
  - `produtos`
  - `pedidos`
  - `faturamento`
  - `logistica`
  - `icompras-servicos`
- Foram removidos artefatos de teste/modulos legados de WebMVC usados no modelo anterior.

### 2) Configuracao padrao dos microservicos

Os servicos com `application.yml` agora usam:

- `spring.profiles.active` com default `local`.
- Variaveis de ambiente padronizadas para banco, Kafka, MinIO e portas.
- Bloco de observabilidade:
  - `management.endpoints.web.exposure.include=health,info,metrics`
  - `management.endpoint.health.show-details=always`
- Bloco de logs padronizado com `traceId` e `spanId`.
- Bloco de erro HTTP padrao:
  - `server.error.include-message=always`
  - `server.error.include-binding-errors=always`

Perfis implementados:

- `local`: defaults para execucao local.
- `docker`: ajustes para nomes dos containers.
- `test`: override basico para testes (`ddl-auto=create-drop` nos servicos com JPA).

### 3) Docker Compose raiz

Foi criado `docker-compose.yml` na raiz com:

- Infra:
  - PostgreSQL
  - Zookeeper
  - Kafka
  - Kafka UI
  - MinIO
  - MinIO init (criacao de bucket)
- Aplicacoes (profile `apps`):
  - clientes
  - produtos
  - pedidos
  - faturamento
  - logistica

Comandos uteis:

```bash
docker compose up -d
docker compose --profile apps up -d --build
docker compose down
```

### 4) Padronizacao de nomenclatura

- Renomeado:
  - `BuckerConfig` -> `BucketConfig`

### 5) Documentacao e requests

- Documento de portas e dependencias: `docs/portas-e-dependencias.md`.
- Arquivo de requests HTTP para testes manuais: `docs/icompras.http`.

## Variaveis de Ambiente Padrao

```text
SPRING_PROFILES_ACTIVE
SERVER_PORT
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
JPA_DDL_AUTO
JPA_SHOW_SQL
KAFKA_BOOTSTRAP_SERVERS
KAFKA_GROUP_PEDIDOS
KAFKA_GROUP_FATURAMENTO
KAFKA_GROUP_LOGISTICA
TOPIC_PEDIDOS_PAGOS
TOPIC_PEDIDOS_FATURADOS
TOPIC_PEDIDOS_ENVIADOS
CLIENTES_SERVICE_URL
PRODUTOS_SERVICE_URL
MINIO_URL
MINIO_ACCESS_KEY
MINIO_SECRET_KEY
MINIO_BUCKET_NAME
LOG_LEVEL_ROOT
LOG_LEVEL_APP
```
