# iCompras

**[English](#english)** | **[Português](#português)**

---

## English

### About

**iCompras** is a microservices-based e-commerce platform built with Java 21 and Spring Boot. The system manages customers, products, orders, and invoicing through independent services that communicate via REST APIs using Spring Cloud OpenFeign and publish events through Apache Kafka. Invoice PDFs are generated with JasperReports and stored in MinIO.

### Tech Stack

| Component | Details |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot (3.3.4 – 4.0.5) |
| Database | PostgreSQL 17.4 |
| ORM | Spring Data JPA / Hibernate |
| Sync Communication | Spring Cloud OpenFeign |
| Async Communication | Apache Kafka 7.2.15 |
| Build Tool | Maven |
| Utilities | Lombok, MapStruct |
| Validation | Jakarta Validation |
| Reporting | JasperReports 7.0.6 |
| Object Storage | MinIO (S3-compatible) |
| Containers | Docker / Docker Compose |
| Monitoring | Kafka UI |

### Architecture

```
┌───────────────────────────────────────────────────────────────────────┐
│                         iCompras Platform                             │
│                                                                       │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐   ┌──────────────┐   │
│  │ Clientes │   │ Produtos │   │   Pedidos    │   │ Faturamento  │   │
│  │  :8082   │   │  :8081   │   │    :8080     │   │    :8083     │   │
│  └────┬─────┘   └────┬─────┘   └──┬───┬───┬───┘   └──────┬───────┘   │
│       │              │             │   │   │              │           │
│       │              │        ┌────┘   │   └────┐         │           │
│       │              │        │        │        │         │           │
│       ▼              ▼        ▼        ▼        ▼         │           │
│  ┌─────────┐   ┌─────────┐  Clientes Produtos  │         │           │
│  │icompras │   │icompras │  Service  Service    │         │           │
│  │clientes │   │icompras │  (Feign)  (Feign)    │         │           │
│  │  (DB)   │   │produtos │                      │         │           │
│  └─────────┘   │  (DB)   │  ┌─────────┐   ┌──────────┐   │           │
│                └─────────┘  │icompras │   │  Kafka   │   │           │
│                             │pedidos  │──▶│  Broker  │──▶│           │
│                             │  (DB)   │   │  :29092  │   │           │
│                             └─────────┘   └──────────┘   │           │
│                                                 │        ▼           │
│                                            Kafka UI  ┌─────────┐    │
│                                             :8090    │  MinIO  │    │
│          PostgreSQL 17.4 — Port 5555                 │  :9000  │    │
│                                                      └─────────┘    │
└───────────────────────────────────────────────────────────────────────┘
```

### Microservices

#### Clientes (Customers) — Port 8082

Manages customer registration and queries.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/clientes` | Create a new customer |
| GET | `/clientes` | List all customers |
| GET | `/clientes/{codigo}` | Get customer by ID |

**Entity fields:** `codigo`, `nome`, `cpf`, `logradouro`, `numero`, `bairro`, `email`, `telefone`

#### Produtos (Products) — Port 8081

Manages the product catalog.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/produtos` | Create a new product |
| GET | `/produtos` | List all products |
| GET | `/produtos/{codigo}` | Get product by ID |

**Entity fields:** `codigo`, `nome`, `valorUnitario`, `descricao`

#### Pedidos (Orders) — Port 8080

Manages orders, line items, and payment processing. Communicates with the Clientes and Produtos services via Feign clients to validate data before creating orders. Publishes payment events to Kafka upon successful payment.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/pedidos` | Create a new order |
| GET | `/pedidos/{codigo}` | Get order details |
| POST | `/pedidos/pagamentos` | Retry payment for an existing order |
| POST | `/pedidos/callback-pagamentos` | Payment status webhook |

**Order entity fields:** `codigo`, `codigoCliente`, `dataPedido`, `chavePagamento`, `observacoes`, `status`, `total`, `codigoRastreio`, `urlNf`, `itens`

**Order item fields:** `codigo`, `codigoProduto`, `quantidade`, `valorUnitario`

**Order statuses:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

**Payment types:** `CREDIT`, `DEBIT`, `PIX`

#### Faturamento (Invoicing) — Port 8083

Consumes Kafka events from the Pedidos service and generates PDF invoices using JasperReports. Stores invoices in MinIO object storage (S3-compatible).

**Kafka consumer:**
- Topic: `icompras.pedidos-pagos`
- Consumer group: `icompras-faturamento`

**Invoice generation flow:**
1. Listens for payment confirmation events on Kafka
2. Deserializes order data (customer info, items, totals)
3. Generates a PDF invoice using a JasperReports template
4. Uploads the PDF to MinIO bucket `icompras.faturas`

**Dependencies:** Spring Kafka, JasperReports 7.0.6, MinIO Client 8.5.17, Lombok

##### Payment Webhook

The endpoint `POST /pedidos/callback-pagamentos` receives payment status callbacks from external banking/payment services. It updates the order status to `PAGO` (on success) or `ERRO_PAGAMENTO` (on failure). When payment succeeds, the order details are published to the Kafka topic `icompras.pedidos-pagos`.

**Headers:**

| Header | Required | Description |
|---|---|---|
| `apiKey` | Yes | API key for authentication |

**Request body:**

| Field | Type | Description |
|---|---|---|
| `codigo` | Long | Order ID |
| `chavePagamento` | String | Payment key/reference |
| `status` | boolean | Payment success status |
| `observacoes` | String | Notes about the payment |

##### Retry Payment

The endpoint `POST /pedidos/pagamentos` allows adding a new payment attempt for an existing order.

**Request body:**

| Field | Type | Description |
|---|---|---|
| `codigoPedido` | Long | Order ID |
| `dadosCartao` | String | Card data |
| `tipoPagamento` | String | Payment type (`CREDIT`, `DEBIT`, `PIX`) |

### Kafka Events

The Pedidos service publishes events to Apache Kafka for asynchronous processing.

| Topic | Description |
|---|---|
| `icompras.pedidos-pagos` | Published when an order payment is confirmed |
| `icompras.pedidos-faturados` | Reserved for invoice notifications |
| `icompras.pedidos-enviados` | Reserved for shipment notifications |

Kafka UI is available at `http://localhost:8090` for monitoring topics and messages.

### Database

Each microservice has its own database, following the **database-per-service** pattern:

| Database | Service | Tables |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reserved) | — |

### Object Storage

MinIO provides S3-compatible object storage for invoice PDFs.

| Resource | Port | Description |
|---|---|---|
| MinIO API | 9000 | S3-compatible API |
| MinIO Console | 9001 | Management UI |
| Bucket | — | `icompras.faturas` |

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Getting Started

**1. Start the infrastructure**

```bash
# Start the database (PostgreSQL on port 5555)
cd icompras-servicos/database
docker compose up -d

# Start the message broker (Kafka on port 29092, Kafka UI on port 8090)
cd ../broker
docker compose up -d

# Start object storage (MinIO on port 9000, Console on port 9001)
cd ../bucket
docker compose up -d
```

**2. Run each microservice**

Open a terminal for each service and run:

```bash
# Produtos (port 8081)
cd produtos
./mvnw spring-boot:run

# Clientes (port 8082)
cd clientes
./mvnw spring-boot:run

# Pedidos (port 8080)
cd pedidos
./mvnw spring-boot:run

# Faturamento (port 8083)
cd faturamento
./mvnw spring-boot:run
```

**3. Test the APIs**

```bash
# Create a customer
curl -X POST http://localhost:8082/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Silva","cpf":"12345678900","email":"maria@email.com"}'

# Create a product
curl -X POST http://localhost:8081/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Notebook","valorUnitario":3500.00,"descricao":"Notebook 16GB RAM"}'

# Create an order
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"codigoCliente":1,"itens":[{"codigoProduto":1,"quantidade":1}]}'

# Get order details
curl http://localhost:8080/pedidos/1
```

### Project Structure

```
icompras/
├── clientes/              # Customer microservice (Spring Boot 4.0.5)
├── produtos/              # Product microservice (Spring Boot 3.4.4)
├── pedidos/               # Order microservice (Spring Boot 3.3.4)
├── faturamento/           # Invoicing microservice (Spring Boot 4.0.5)
└── icompras-servicos/     # Infrastructure
    ├── database/
    │   ├── docker-compose.yml
    │   ├── schema.sql
    │   └── init-db/
    │       └── create_databases.sql
    ├── broker/
    │   └── docker-compose.yml
    └── bucket/
        └── docker-compose.yml
```

---

## Português

### Sobre

**iCompras** é uma plataforma de e-commerce baseada em microsserviços, construída com Java 21 e Spring Boot. O sistema gerencia clientes, produtos, pedidos e faturamento por meio de serviços independentes que se comunicam via APIs REST utilizando Spring Cloud OpenFeign e publicam eventos através do Apache Kafka. Notas fiscais em PDF são geradas com JasperReports e armazenadas no MinIO.

### Stack Tecnológica

| Componente | Detalhes |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot (3.3.4 – 4.0.5) |
| Banco de Dados | PostgreSQL 17.4 |
| ORM | Spring Data JPA / Hibernate |
| Comunicação Síncrona | Spring Cloud OpenFeign |
| Comunicação Assíncrona | Apache Kafka 7.2.15 |
| Build | Maven |
| Utilitários | Lombok, MapStruct |
| Validação | Jakarta Validation |
| Relatórios | JasperReports 7.0.6 |
| Armazenamento de Objetos | MinIO (compatível com S3) |
| Containers | Docker / Docker Compose |
| Monitoramento | Kafka UI |

### Arquitetura

```
┌───────────────────────────────────────────────────────────────────────┐
│                       Plataforma iCompras                            │
│                                                                       │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐   ┌──────────────┐   │
│  │ Clientes │   │ Produtos │   │   Pedidos    │   │ Faturamento  │   │
│  │  :8082   │   │  :8081   │   │    :8080     │   │    :8083     │   │
│  └────┬─────┘   └────┬─────┘   └──┬───┬───┬───┘   └──────┬───────┘   │
│       │              │             │   │   │              │           │
│       │              │        ┌────┘   │   └────┐         │           │
│       │              │        │        │        │         │           │
│       ▼              ▼        ▼        ▼        ▼         │           │
│  ┌─────────┐   ┌─────────┐  Clientes Produtos  │         │           │
│  │icompras │   │icompras │  Service  Service    │         │           │
│  │clientes │   │icompras │  (Feign)  (Feign)    │         │           │
│  │  (BD)   │   │produtos │                      │         │           │
│  └─────────┘   │  (BD)   │  ┌─────────┐   ┌──────────┐   │           │
│                └─────────┘  │icompras │   │  Kafka   │   │           │
│                             │pedidos  │──▶│  Broker  │──▶│           │
│                             │  (BD)   │   │  :29092  │   │           │
│                             └─────────┘   └──────────┘   │           │
│                                                 │        ▼           │
│                                            Kafka UI  ┌─────────┐    │
│                                             :8090    │  MinIO  │    │
│          PostgreSQL 17.4 — Porta 5555                │  :9000  │    │
│                                                      └─────────┘    │
└───────────────────────────────────────────────────────────────────────┘
```

### Microsserviços

#### Clientes — Porta 8082

Gerencia o cadastro e consulta de clientes.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/clientes` | Cadastrar novo cliente |
| GET | `/clientes` | Listar todos os clientes |
| GET | `/clientes/{codigo}` | Buscar cliente por ID |

**Campos da entidade:** `codigo`, `nome`, `cpf`, `logradouro`, `numero`, `bairro`, `email`, `telefone`

#### Produtos — Porta 8081

Gerencia o catálogo de produtos.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/produtos` | Cadastrar novo produto |
| GET | `/produtos` | Listar todos os produtos |
| GET | `/produtos/{codigo}` | Buscar produto por ID |

**Campos da entidade:** `codigo`, `nome`, `valorUnitario`, `descricao`

#### Pedidos — Porta 8080

Gerencia pedidos, itens e processamento de pagamento. Comunica-se com os serviços de Clientes e Produtos via Feign clients para validar dados antes de criar pedidos. Publica eventos de pagamento no Kafka após confirmação.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/pedidos` | Criar novo pedido |
| GET | `/pedidos/{codigo}` | Consultar detalhes do pedido |
| POST | `/pedidos/pagamentos` | Retentar pagamento de um pedido |
| POST | `/pedidos/callback-pagamentos` | Webhook de status de pagamento |

**Campos da entidade pedido:** `codigo`, `codigoCliente`, `dataPedido`, `chavePagamento`, `observacoes`, `status`, `total`, `codigoRastreio`, `urlNf`, `itens`

**Campos do item do pedido:** `codigo`, `codigoProduto`, `quantidade`, `valorUnitario`

**Status do pedido:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

**Tipos de pagamento:** `CREDIT`, `DEBIT`, `PIX`

#### Faturamento — Porta 8083

Consome eventos Kafka do serviço de Pedidos e gera notas fiscais em PDF utilizando JasperReports. Armazena as notas no MinIO (armazenamento de objetos compatível com S3).

**Consumidor Kafka:**
- Tópico: `icompras.pedidos-pagos`
- Grupo de consumo: `icompras-faturamento`

**Fluxo de geração de nota fiscal:**
1. Escuta eventos de confirmação de pagamento no Kafka
2. Deserializa os dados do pedido (info do cliente, itens, totais)
3. Gera um PDF de nota fiscal usando template JasperReports
4. Faz upload do PDF no bucket MinIO `icompras.faturas`

**Dependências:** Spring Kafka, JasperReports 7.0.6, MinIO Client 8.5.17, Lombok

##### Webhook de Pagamento

O endpoint `POST /pedidos/callback-pagamentos` recebe callbacks de status de pagamento de serviços bancários/pagamento externos. Atualiza o status do pedido para `PAGO` (em caso de sucesso) ou `ERRO_PAGAMENTO` (em caso de falha). Quando o pagamento é confirmado, os detalhes do pedido são publicados no tópico Kafka `icompras.pedidos-pagos`.

**Headers:**

| Header | Obrigatório | Descrição |
|---|---|---|
| `apiKey` | Sim | Chave de API para autenticação |

**Corpo da requisição:**

| Campo | Tipo | Descrição |
|---|---|---|
| `codigo` | Long | ID do pedido |
| `chavePagamento` | String | Chave/referência do pagamento |
| `status` | boolean | Status de sucesso do pagamento |
| `observacoes` | String | Observações sobre o pagamento |

##### Retentar Pagamento

O endpoint `POST /pedidos/pagamentos` permite adicionar uma nova tentativa de pagamento para um pedido existente.

**Corpo da requisição:**

| Campo | Tipo | Descrição |
|---|---|---|
| `codigoPedido` | Long | ID do pedido |
| `dadosCartao` | String | Dados do cartão |
| `tipoPagamento` | String | Tipo de pagamento (`CREDIT`, `DEBIT`, `PIX`) |

### Eventos Kafka

O serviço de Pedidos publica eventos no Apache Kafka para processamento assíncrono.

| Tópico | Descrição |
|---|---|
| `icompras.pedidos-pagos` | Publicado quando o pagamento de um pedido é confirmado |
| `icompras.pedidos-faturados` | Reservado para notificações de faturamento |
| `icompras.pedidos-enviados` | Reservado para notificações de envio |

O Kafka UI está disponível em `http://localhost:8090` para monitoramento de tópicos e mensagens.

### Banco de Dados

Cada microsserviço possui seu próprio banco de dados, seguindo o padrão **database-per-service**:

| Banco de Dados | Serviço | Tabelas |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reservado) | — |

### Armazenamento de Objetos

MinIO fornece armazenamento de objetos compatível com S3 para os PDFs de notas fiscais.

| Recurso | Porta | Descrição |
|---|---|---|
| MinIO API | 9000 | API compatível com S3 |
| MinIO Console | 9001 | Interface de gerenciamento |
| Bucket | — | `icompras.faturas` |

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker e Docker Compose

### Como Executar

**1. Iniciar a infraestrutura**

```bash
# Iniciar o banco de dados (PostgreSQL na porta 5555)
cd icompras-servicos/database
docker compose up -d

# Iniciar o message broker (Kafka na porta 29092, Kafka UI na porta 8090)
cd ../broker
docker compose up -d

# Iniciar armazenamento de objetos (MinIO na porta 9000, Console na porta 9001)
cd ../bucket
docker compose up -d
```

**2. Executar cada microsserviço**

Abra um terminal para cada serviço e execute:

```bash
# Produtos (porta 8081)
cd produtos
./mvnw spring-boot:run

# Clientes (porta 8082)
cd clientes
./mvnw spring-boot:run

# Pedidos (porta 8080)
cd pedidos
./mvnw spring-boot:run

# Faturamento (porta 8083)
cd faturamento
./mvnw spring-boot:run
```

**3. Testar as APIs**

```bash
# Cadastrar um cliente
curl -X POST http://localhost:8082/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Silva","cpf":"12345678900","email":"maria@email.com"}'

# Cadastrar um produto
curl -X POST http://localhost:8081/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Notebook","valorUnitario":3500.00,"descricao":"Notebook 16GB RAM"}'

# Criar um pedido
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"codigoCliente":1,"itens":[{"codigoProduto":1,"quantidade":1}]}'

# Consultar detalhes do pedido
curl http://localhost:8080/pedidos/1
```

### Estrutura do Projeto

```
icompras/
├── clientes/              # Microsserviço de clientes (Spring Boot 4.0.5)
├── produtos/              # Microsserviço de produtos (Spring Boot 3.4.4)
├── pedidos/               # Microsserviço de pedidos (Spring Boot 3.3.4)
├── faturamento/           # Microsserviço de faturamento (Spring Boot 4.0.5)
└── icompras-servicos/     # Infraestrutura
    ├── database/
    │   ├── docker-compose.yml
    │   ├── schema.sql
    │   └── init-db/
    │       └── create_databases.sql
    ├── broker/
    │   └── docker-compose.yml
    └── bucket/
        └── docker-compose.yml
```

---

**Developed by Monalisa Menezes**
