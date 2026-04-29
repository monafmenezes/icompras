# Plano de Acao do Projeto iCompras

Este documento organiza a continuidade do projeto iCompras como uma plataforma de e-commerce baseada em microservicos. O objetivo e transformar o projeto em uma entrega completa de portfolio, com autenticacao, API Gateway, frontend, testes, metricas, documentacao e padroes consistentes entre os servicos.

## Objetivos

- Criar um microservico de autenticacao com JWT.
- Criar uma API Gateway como ponto unico de entrada.
- Preparar um frontend para consumir a plataforma.
- Padronizar arquitetura, configuracao, endpoints, erros, logs e documentacao dos microservicos.
- Melhorar confiabilidade com testes unitarios, integracao e contratos.
- Adicionar metricas, health checks e observabilidade.
- Facilitar execucao local com Docker Compose e documentacao clara.

## Estado Atual

O projeto ja possui os seguintes servicos:

| Servico | Porta | Responsabilidade |
|---|---:|---|
| pedidos | 8080 | Criacao de pedidos, pagamento e atualizacao de status |
| produtos | 8081 | Cadastro e consulta de produtos |
| clientes | 8082 | Cadastro e consulta de clientes |
| faturamento | 8083 | Geracao de nota fiscal, upload no MinIO e publicacao de evento |
| logistica | 8084 | Processamento de envio e geracao de codigo de rastreio |

Infraestrutura atual:

| Recurso | Uso |
|---|---|
| PostgreSQL | Banco de dados dos servicos |
| Apache Kafka | Comunicacao assincrona entre pedidos, faturamento e logistica |
| Kafka UI | Visualizacao dos topicos |
| MinIO | Armazenamento de notas fiscais |
| Springdoc OpenAPI | Documentacao Swagger dos servicos |

## Roadmap Geral

### Fase 1: Fundacao e Padronizacao - Concluida

Status: concluida.

Documento detalhado: [`docs/fase1-fundacao.md`](fase1-fundacao.md).

- [x] Padronizar versoes do Spring Boot entre todos os microservicos.
- [x] Criar um `docker-compose.yml` raiz para subir toda a stack local.
- [x] Criar profiles `local`, `docker` e `test`.
- [x] Padronizar variaveis de ambiente.
- [x] Adicionar Spring Boot Actuator em todos os servicos.
- [x] Padronizar logs e mensagens de erro.
- [x] Revisar nomes de classes/configuracoes com typos ou inconsistencias.
- [x] Criar documentacao de portas e dependencias.
- [x] Criar colecao Postman, Insomnia ou arquivos `.http`.

### Fase 2: Microservico de Autenticacao - Em planejamento

Status: proxima fase.

Documento detalhado: [`docs/fase2-autenticacao.md`](fase2-autenticacao.md).

- [ ] Criar modulo/servico `auth`.
- [ ] Criar banco `icomprasauth`.
- [ ] Configurar profiles `local`, `docker` e `test`.
- [ ] Implementar entidade `User`.
- [ ] Implementar cadastro de usuario.
- [ ] Implementar login com email e senha.
- [ ] Gerar token JWT.
- [ ] Validar token JWT.
- [ ] Implementar roles `ADMIN`, `CUSTOMER` e `SERVICE`.
- [ ] Criar endpoint para usuario autenticado.
- [ ] Adicionar refresh token ou definir explicitamente que o MVP usara somente access token.
- [ ] Adicionar testes unitarios, de controller e de integracao.
- [ ] Documentar endpoints no Swagger.
- [ ] Incluir o servico no Docker Compose raiz.

Endpoints sugeridos:

```text
POST /auth/register
POST /auth/login
POST /auth/refresh
GET  /auth/me
```

Modelo sugerido:

```text
User
- id
- name
- email
- passwordHash
- role
- active
- createdAt
- updatedAt
```

### Fase 3: API Gateway

- [ ] Criar servico `gateway`.
- [ ] Configurar Spring Cloud Gateway.
- [ ] Criar rotas para todos os microservicos.
- [ ] Configurar CORS para o frontend.
- [ ] Validar JWT nas requisicoes.
- [ ] Propagar headers importantes, como correlation id e usuario autenticado.
- [ ] Configurar rate limiting, se aplicavel.
- [ ] Centralizar acesso externo somente pelo gateway.
- [ ] Adicionar Swagger agregado ou links para os Swaggers dos servicos.

Rotas sugeridas:

```text
/api/auth/**        -> auth
/api/clientes/**    -> clientes
/api/produtos/**    -> produtos
/api/pedidos/**     -> pedidos
/api/faturamento/** -> faturamento
```

### Fase 4: Seguranca nos Microservicos

- [ ] Adicionar Resource Server JWT nos servicos REST.
- [ ] Proteger endpoints administrativos.
- [ ] Permitir endpoints publicos somente quando necessario.
- [ ] Associar pedidos ao usuario autenticado.
- [ ] Garantir que clientes consultem apenas seus proprios pedidos.
- [ ] Proteger webhook de pagamento com API key, assinatura HMAC ou token especifico.
- [ ] Documentar matriz de permissoes.

Matriz inicial de permissoes:

| Recurso | ADMIN | CUSTOMER | Publico |
|---|---:|---:|---:|
| Criar produto | Sim | Nao | Nao |
| Listar produtos | Sim | Sim | Sim |
| Criar cliente | Sim | Sim | Nao |
| Listar clientes | Sim | Nao | Nao |
| Criar pedido | Sim | Sim | Nao |
| Consultar proprio pedido | Sim | Sim | Nao |
| Consultar todos os pedidos | Sim | Nao | Nao |
| Baixar nota fiscal | Sim | Dono do pedido | Nao |

### Fase 5: Frontend

- [ ] Escolher stack: React + TypeScript + Vite ou Next.js.
- [ ] Criar tela de login.
- [ ] Criar tela de cadastro.
- [ ] Criar catalogo de produtos.
- [ ] Criar fluxo de criacao de pedido.
- [ ] Criar tela de detalhe do pedido.
- [ ] Exibir status do pedido.
- [ ] Exibir link da nota fiscal.
- [ ] Exibir codigo de rastreio.
- [ ] Criar area administrativa para produtos e clientes.
- [ ] Consumir somente a API Gateway.

Fluxo principal esperado:

```text
Usuario faz login
Usuario consulta produtos
Usuario cria pedido
Pagamento e confirmado
Pedido publica evento no Kafka
Faturamento gera nota fiscal
Logistica gera codigo de rastreio
Frontend exibe pedido atualizado
```

### Fase 6: Observabilidade e Metricas

- [ ] Adicionar Actuator em todos os servicos.
- [ ] Expor endpoints `/actuator/health`, `/actuator/info` e `/actuator/metrics`.
- [ ] Adicionar Micrometer.
- [ ] Adicionar Prometheus.
- [ ] Criar dashboard Grafana.
- [ ] Criar correlation id por requisicao.
- [ ] Propagar correlation id entre gateway, REST e eventos Kafka.
- [ ] Registrar logs estruturados.
- [ ] Monitorar consumo e publicacao de eventos Kafka.
- [ ] Monitorar tempo de geracao de nota fiscal.

Metricas recomendadas:

| Metrica | Descricao |
|---|---|
| `http.server.requests` | Tempo e volume de requisicoes HTTP |
| `jvm.memory.used` | Uso de memoria da JVM |
| `jvm.threads.live` | Threads ativas |
| `kafka.consumer.records.consumed.total` | Total de mensagens consumidas |
| `kafka.producer.record.send.total` | Total de mensagens publicadas |
| `orders.created.total` | Total de pedidos criados |
| `orders.payment.success.total` | Total de pagamentos aprovados |
| `orders.payment.error.total` | Total de pagamentos recusados |
| `invoices.generated.total` | Total de notas fiscais geradas |
| `invoices.generation.duration` | Tempo de geracao de nota fiscal |
| `shipments.created.total` | Total de envios gerados |

## Padronizacao de Requisitos dos Microservicos

Todo microservico deve possuir:

- [ ] Nome claro no `spring.application.name`.
- [ ] Porta definida no `application.yml`.
- [ ] Configuracoes por variavel de ambiente.
- [ ] Profile `local`.
- [ ] Profile `docker`.
- [ ] Profile `test`.
- [ ] Swagger/OpenAPI habilitado.
- [ ] Actuator habilitado.
- [ ] Logs com correlation id.
- [ ] Tratamento global de erros.
- [ ] DTOs de entrada e saida.
- [ ] Validacao com Jakarta Validation.
- [ ] Testes unitarios.
- [ ] Testes de integracao quando houver banco, Kafka ou MinIO.
- [ ] README ou secao de documentacao explicando responsabilidade e endpoints.

## Padrao de Estrutura dos Microservicos

Estrutura recomendada:

```text
src/main/java/com/monalisamenezes/icompras/{servico}
├── api ou controller
├── client
├── config
├── dto
├── exception
├── mapper
├── model
├── repository
├── service
├── publisher
├── subscriber
└── {Servico}Application.java
```

Regras:

- Controllers devem receber DTOs e retornar DTOs ou responses padronizadas.
- Services devem conter regras de negocio.
- Repositories devem conter acesso a dados.
- Mappers devem isolar conversao entre entidade, DTO e representation.
- Publishers e subscribers devem ficar separados de regras de negocio complexas.
- Exceptions de negocio devem ser especificas e tratadas por `@ControllerAdvice`.
- Configuracoes externas devem ficar no `application.yml` e aceitar override por variavel de ambiente.

## Padrao de API REST

### Convencoes de endpoint

```text
GET    /recursos
GET    /recursos/{id}
POST   /recursos
PUT    /recursos/{id}
PATCH  /recursos/{id}
DELETE /recursos/{id}
```

### Status HTTP

| Caso | Status |
|---|---:|
| Criacao com sucesso | 201 |
| Consulta com sucesso | 200 |
| Atualizacao sem corpo | 204 |
| Remocao logica/fisica | 204 |
| Erro de validacao | 400 |
| Nao autenticado | 401 |
| Sem permissao | 403 |
| Nao encontrado | 404 |
| Conflito de regra de negocio | 409 |
| Erro interno | 500 |

### Resposta de erro padronizada

```json
{
  "timestamp": "2026-04-29T16:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Campo obrigatorio",
  "path": "/api/produtos",
  "fieldErrors": [
    {
      "field": "nome",
      "message": "Nome e obrigatorio"
    }
  ]
}
```

## Padrao de Configuracao

Variaveis recomendadas:

```text
SERVER_PORT
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
KAFKA_BOOTSTRAP_SERVERS
MINIO_ENDPOINT
MINIO_ACCESS_KEY
MINIO_SECRET_KEY
JWT_ISSUER
JWT_SECRET
JWT_EXPIRATION_SECONDS
CLIENTES_SERVICE_URL
PRODUTOS_SERVICE_URL
PEDIDOS_SERVICE_URL
FATURAMENTO_SERVICE_URL
```

Exemplo de padrao para `application.yml`:

```yaml
spring:
  application:
    name: produtos
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5555}/${DB_NAME:icomprasprodutos}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}

server:
  port: ${SERVER_PORT:8081}
```

## Padrao de Eventos Kafka

Todo evento deve possuir:

- [ ] Nome de topico padronizado.
- [ ] Payload versionado.
- [ ] Identificador do evento.
- [ ] Data de ocorrencia.
- [ ] Correlation id.
- [ ] Identificador da entidade principal.

Envelope sugerido:

```json
{
  "eventId": "uuid",
  "eventType": "PEDIDO_PAGO",
  "eventVersion": "1.0",
  "occurredAt": "2026-04-29T16:00:00",
  "correlationId": "uuid",
  "payload": {}
}
```

Topicos atuais e recomendados:

| Topico | Produtor | Consumidor |
|---|---|---|
| `icompras.pedidos-pagos` | pedidos | faturamento |
| `icompras.pedidos-faturados` | faturamento | pedidos, logistica |
| `icompras.pedidos-enviados` | logistica | pedidos |

## Testes

### Testes Unitarios

Devem cobrir regras de negocio sem subir Spring completo.

Prioridades:

- [ ] `ProdutoService`
- [ ] `ClienteService`
- [ ] `PedidoService`
- [ ] `PedidoValidator`
- [ ] `AtualizacaoStatusPedidoService`
- [ ] `GeradorNotaFiscalService`
- [ ] `NotaFiscalService`
- [ ] `EnvioPedidoService`
- [ ] Services do futuro `auth`

Ferramentas:

- JUnit 5.
- Mockito.
- AssertJ.

Padrao:

```text
Dado um contexto valido
Quando uma acao for executada
Entao o resultado esperado deve ocorrer
```

### Testes de Controller

Devem validar contrato HTTP, status code, validacoes e payloads.

- [ ] Usar `@WebMvcTest` para controllers simples.
- [ ] Mockar services.
- [ ] Validar status HTTP.
- [ ] Validar resposta JSON.
- [ ] Validar erros de entrada.

### Testes de Integracao

Devem validar integracao real com infraestrutura.

- [ ] PostgreSQL com Testcontainers.
- [ ] Kafka com Testcontainers.
- [ ] MinIO com container ou mock compativel.
- [ ] Fluxo de criacao de pedido com banco.
- [ ] Fluxo de publicacao e consumo de eventos.
- [ ] Fluxo de geracao e armazenamento de nota fiscal.

### Testes de Contrato

Recomendado para evolucao futura.

- [ ] Definir contratos entre gateway e servicos.
- [ ] Definir contratos dos Feign clients.
- [ ] Definir contratos dos eventos Kafka.
- [ ] Validar compatibilidade antes de alterar payloads.

### Meta de Cobertura

Meta inicial:

```text
Unitarios: 70%
Services de regra de negocio: 85%
Controllers principais: 70%
Fluxos criticos: pelo menos 1 teste de integracao por fluxo
```

Fluxos criticos:

- [ ] Cadastro de produto.
- [ ] Cadastro de cliente.
- [ ] Criacao de pedido.
- [ ] Confirmacao de pagamento.
- [ ] Publicacao de pedido pago.
- [ ] Geracao de nota fiscal.
- [ ] Publicacao de pedido faturado.
- [ ] Geracao de codigo de rastreio.
- [ ] Atualizacao final do pedido como enviado.

## Requisitos Para Considerar Um Servico Pronto

Checklist de Definition of Done:

- [ ] Build executando sem erro.
- [ ] Testes unitarios relevantes implementados.
- [ ] Testes de integracao implementados quando houver dependencia externa.
- [ ] Swagger acessivel.
- [ ] Actuator acessivel.
- [ ] Logs com informacoes suficientes para diagnostico.
- [ ] Erros padronizados.
- [ ] Configuracao via variaveis de ambiente.
- [ ] Dockerfile criado, quando aplicavel.
- [ ] Servico incluido no Docker Compose raiz.
- [ ] README ou documentacao atualizada.

## Melhorias Para Portfolio

- [ ] Diagrama de arquitetura atualizado com auth, gateway e frontend.
- [ ] Prints do Swagger, Kafka UI, MinIO e frontend.
- [ ] GIF demonstrando o fluxo completo.
- [ ] README com instrucao de execucao local.
- [ ] CI no GitHub Actions.
- [ ] Badges de build, Java, Spring Boot e Docker.
- [ ] Docker Compose unico para demo.
- [ ] Dados iniciais para demonstracao.
- [ ] Script ou endpoint para simular callback de pagamento.
- [ ] Dashboard Grafana com metricas principais.

## Ordem Recomendada de Implementacao

1. Padronizar versoes, configuracoes e estrutura dos servicos existentes.
2. Criar Docker Compose raiz.
3. Adicionar Actuator e padrao de erros.
4. Melhorar testes unitarios dos servicos existentes.
5. Criar microservico `auth`.
6. Criar microservico `gateway`.
7. Integrar gateway com JWT.
8. Proteger endpoints dos servicos.
9. Criar frontend.
10. Adicionar metricas com Prometheus e Grafana.
11. Melhorar README e materiais de portfolio.

## MVP Recomendado

Para uma primeira versao completa de portfolio:

- [ ] Auth com login JWT.
- [ ] Gateway roteando requisicoes.
- [ ] Produtos e clientes protegidos por permissao.
- [ ] Criacao de pedido autenticada.
- [ ] Fluxo Kafka funcionando ate envio.
- [ ] Nota fiscal disponivel via MinIO.
- [ ] Frontend com login, catalogo, pedido e detalhe do pedido.
- [ ] Testes unitarios dos services principais.
- [ ] Teste de integracao do fluxo de pedido.
- [ ] README atualizado com passo a passo.
