<p align="center">
  <img src="https://github.com/user-attachments/assets/3a439c9a-4acd-41a3-b67b-69426775425a" alt="iCompras Logo" width="378">
</p>

# <p align="center">🛒 iCompras</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  </p>
<p align="center">
  <strong>A microservices-based e-commerce platform</strong><br>
  <em>Built with Java 21 • Spring Boot • Kafka • JasperReports • MinIO</em>
</p>

<p align="center">
  <a href="#-english">🇺🇸 English</a> •
  <a href="#-português">🇧🇷 Português</a> •
  <a href="docs/plano-acao.md">📌 Plano de ação</a> •
  <a href="docs/fase1-fundacao.md">🧱 Fase 1</a>
</p>

---

## 🇺🇸 English

### 📋 About

**iCompras** is a microservices-based e-commerce platform built with **Java 21** and **Spring Boot**. The system manages customers, products, orders, invoicing, and logistics through independent services that communicate via REST APIs using Spring Cloud OpenFeign and publish events through Apache Kafka. Invoice PDFs are generated with JasperReports and stored in MinIO. Shipment processing is handled through an event-driven pipeline with automatic tracking code generation.

### 🛠️ Tech Stack

| Component | Details |
|---|---|
| ☕ Language | Java 21 |
| 🌱 Framework | Spring Boot (3.3.4 – 4.0.5) |
| 🐘 Database | PostgreSQL 17.4 |
| 🗃️ ORM | Spring Data JPA / Hibernate |
| 🔗 Sync Communication | Spring Cloud OpenFeign |
| 📨 Async Communication | Apache Kafka 7.2.15 |
| 🔨 Build Tool | Maven |
| ⚙️ Utilities | Lombok, MapStruct |
| ✅ Validation | Jakarta Validation |
| 📄 Reporting | JasperReports 7.0.6, JasperReports PDF 7.0.0, OpenPDF 2.0.3 |
| 📦 Object Storage | MinIO (S3-compatible) |
| 📖 API Docs | Springdoc OpenAPI 2.5.0 (Swagger UI) |
| 🐳 Containers | Docker / Docker Compose |
| 📊 Monitoring | Kafka UI (provectuslabs v0.7.2) |

### 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                            iCompras Platform                                │
│                                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐  ┌─────────────┐ ┌──────────┐ │
│  │ Clientes │  │ Produtos │  │   Pedidos    │  │ Faturamento │ │Logistica │ │
│  │  :8082   │  │  :8081   │  │    :8080     │  │    :8083    │ │  :8084   │ │
│  └────┬─────┘  └────┬─────┘  └──┬───┬───┬───┘  └──────┬──────┘ └────┬─────┘ │
│       │             │           │   │   │             │             │       │
│       │             │      ┌────┘   │   └────┐        │             │       │
│       │             │      │        │        │        │             │       │
│       ▼             ▼      ▼        ▼        ▼        │             │       │
│  ┌─────────┐  ┌─────────┐ Clientes Produtos  │        │             │       │
│  │icompras │  │icompras │ Service  Service    │        │             │       │
│  │clientes │  │icompras │ (Feign)  (Feign)    │        │             │       │
│  │  (DB)   │  │produtos │                     │        │             │       │
│  └─────────┘  │  (DB)   │ ┌─────────┐  ┌──────────┐   │             │       │
│               └─────────┘ │icompras │  │  Kafka   │   │             │       │
│                           │pedidos  │─▶│  Broker  │──▶│             │       │
│                           │  (DB)   │  │  :29092  │──────────────▶│       │
│                           └─────────┘  └──────────┘   │             │       │
│                                              │        ▼             │       │
│                                         Kafka UI  ┌─────────┐      │       │
│                                          :8090    │  MinIO  │      │       │
│        PostgreSQL 17.4 — Port 5555                │  :9000  │      │       │
│                                                   └─────────┘      │       │
│                                                                     │       │
│  Kafka Event Flow:                                                  │       │
│  Pedidos ──▶ [pedidos-pagos] ──▶ Faturamento ──▶ [pedidos-faturados]│       │
│  ──▶ Logistica ──▶ [pedidos-enviados] ──▶ Pedidos                   │       │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 🔧 Microservices

#### 👥 Clientes (Customers) — Port 8082

Manages customer registration and queries.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/clientes` | Create a new customer |
| GET | `/clientes` | List all customers |
| GET | `/clientes/{codigo}` | Get customer by ID |
| DELETE | `/clientes/{codigo}` | Soft delete a customer (sets `ativo` to `false`) |

**Entity fields:** `codigo`, `nome`, `cpf`, `logradouro`, `numero`, `bairro`, `email`, `telefone`, `ativo`

#### 📦 Produtos (Products) — Port 8081

Manages the product catalog.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/produtos` | Create a new product |
| GET | `/produtos` | List all products |
| GET | `/produtos/{codigo}` | Get product by ID |
| DELETE | `/produtos/{codigo}` | Soft delete a product (sets `ativo` to `false`) |

**Entity fields:** `codigo`, `nome`, `valorUnitario`, `descricao`, `ativo`

#### 🛍️ Pedidos (Orders) — Port 8080

Manages orders, line items, and payment processing. Communicates with the Clientes and Produtos services via Feign clients to validate data before creating orders. Publishes payment events to Kafka upon successful payment. Also consumes events from the `icompras.pedidos-faturados` and `icompras.pedidos-enviados` topics to update order status with invoice URLs and tracking codes.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/pedidos` | Create a new order |
| GET | `/pedidos/{codigo}` | Get order details |
| POST | `/pedidos/pagamentos` | Retry payment for an existing order |
| POST | `/pedidos/callback-pagamentos` | Payment status webhook |

**Kafka consumer:**
- Topics: `icompras.pedidos-faturados`, `icompras.pedidos-enviados`
- Consumer group: `icompras-atualizacao-pedido`
- Updates order status, invoice URL (`urlNf`), and tracking code (`codigoRastreio`)

**Order entity fields:** `codigo`, `codigoCliente`, `dataPedido`, `chavePagamento`, `observacoes`, `status`, `total`, `codigoRastreio`, `urlNf`, `itens`

**Order item fields:** `codigo`, `codigoProduto`, `quantidade`, `valorUnitario`

**Order statuses:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

**Payment types:** `CREDIT`, `DEBIT`, `PIX`

#### 🧾 Faturamento (Invoicing) — Port 8083

Consumes Kafka events from the Pedidos service and generates PDF invoices using JasperReports. Stores invoices in MinIO object storage (S3-compatible). After generating an invoice, publishes a `FATURADO` event to Kafka so the Logistica service can proceed with shipment. Also exposes a REST API for file upload and download.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/bucket` | Upload a file to MinIO |
| GET | `/bucket?filename={filename}` | Get a presigned download URL (1h expiry) |

**Kafka consumer:**
- Topic: `icompras.pedidos-pagos`
- Consumer group: `icompras-faturamento`

**Kafka producer:**
- Topic: `icompras.pedidos-faturados`
- Publishes order ID, status `FATURADO`, and invoice URL after PDF generation

**Invoice generation flow:**
1. 👂 Listens for payment confirmation events on Kafka
2. 📥 Deserializes order data (customer info, items, totals)
3. 📄 Generates a PDF invoice using a JasperReports template
4. ☁️ Uploads the PDF to MinIO bucket `icompras.faturas`
5. 📣 Publishes `FATURADO` event with the invoice URL to Kafka

**Dependencies:** Spring Kafka, JasperReports 7.0.6, JasperReports PDF 7.0.0, OpenPDF 2.0.3, MinIO Client 8.5.17, Jackson Datatype JSR-310/JDK8, Lombok

#### 🚚 Logistica (Logistics) — Port 8084

Handles shipment processing. This service is entirely event-driven with no REST endpoints — it consumes Kafka events from the Faturamento service, generates a tracking code, and publishes a shipment event back to Kafka so the Pedidos service can update the order status.

**Kafka consumer:**
- Topic: `icompras.pedidos-faturados`
- Consumer group: `icompras-logistica`

**Kafka producer:**
- Topic: `icompras.pedidos-enviados`
- Publishes order ID, status `ENVIADO`, and tracking code

**Shipment flow:**
1. 👂 Listens for invoicing events on `icompras.pedidos-faturados`
2. 📥 Receives order ID and invoice URL
3. 📦 Generates a tracking code (format: `XX00000000BR`)
4. 📣 Publishes `ENVIADO` event with tracking code to `icompras.pedidos-enviados`

**Dependencies:** Spring Kafka, Jackson Datatype JSR-310/JDK8, Lombok

---

##### 🔔 Payment Webhook

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

##### 🔄 Retry Payment

The endpoint `POST /pedidos/pagamentos` allows adding a new payment attempt for an existing order.

**Request body:**

| Field | Type | Description |
|---|---|---|
| `codigoPedido` | Long | Order ID |
| `dadosCartao` | String | Card data |
| `tipoPagamento` | String | Payment type (`CREDIT`, `DEBIT`, `PIX`) |

### 📨 Kafka Events

The Pedidos service publishes events to Apache Kafka for asynchronous processing.

| Topic | Producer | Consumer | Description |
|---|---|---|---|
| `icompras.pedidos-pagos` | Pedidos | Faturamento | Published when an order payment is confirmed |
| `icompras.pedidos-faturados` | Faturamento | Logistica, Pedidos | Published when an invoice PDF is generated |
| `icompras.pedidos-enviados` | Logistica | Pedidos | Published when a shipment is prepared with a tracking code |

**Event flow:**
```
Pedidos ──▶ [icompras.pedidos-pagos] ──▶ Faturamento
                                              │
                                              ▼
                                   Generate PDF Invoice
                                   Upload to MinIO
                                              │
                                              ▼
Pedidos ◀── [icompras.pedidos-faturados] ◀── Faturamento
   │
   ▼
Logistica ◀── [icompras.pedidos-faturados]
      │
      ▼
   Generate Tracking Code
      │
      ▼
Pedidos ◀── [icompras.pedidos-enviados] ◀── Logistica
```

Kafka UI is available at `http://localhost:8090` for monitoring topics and messages.

### 🗄️ Database

Each microservice has its own database, following the **database-per-service** pattern:

| Database | Service | Tables |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reserved) | — |

### 📦 Object Storage

MinIO provides S3-compatible object storage for invoice PDFs.

| Resource | Port | Description |
|---|---|---|
| MinIO API | 9000 | S3-compatible API |
| MinIO Console | 9001 | Management UI |
| Bucket | — | `icompras.faturas` |

### ✅ Prerequisites

- ☕ Java 21+
- 🔨 Maven 3.8+
- 🐳 Docker & Docker Compose

### 🚀 Getting Started

**1️⃣ Start the infrastructure**

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

**2️⃣ Run each microservice**

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

# Logistica (port 8084)
cd logistica
./mvnw spring-boot:run
```

**3️⃣ Test the APIs**

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

### 📁 Project Structure

```
icompras/
├── 👥 clientes/           # Customer microservice (Spring Boot 4.0.5)
├── 📦 produtos/           # Product microservice (Spring Boot 3.4.4)
├── 🛍️ pedidos/            # Order microservice (Spring Boot 3.3.4)
├── 🧾 faturamento/        # Invoicing microservice (Spring Boot 4.0.5)
├── 🚚 logistica/          # Logistics microservice (Spring Boot 4.0.5)
└── ⚙️ icompras-servicos/  # Infrastructure
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

## 🇧🇷 Português

### 📋 Sobre

**iCompras** é uma plataforma de e-commerce baseada em microsserviços, construída com **Java 21** e **Spring Boot**. O sistema gerencia clientes, produtos, pedidos, faturamento e logística por meio de serviços independentes que se comunicam via APIs REST utilizando Spring Cloud OpenFeign e publicam eventos através do Apache Kafka. Notas fiscais em PDF são geradas com JasperReports e armazenadas no MinIO. O processamento de envios é realizado através de um pipeline orientado a eventos com geração automática de código de rastreio.

### 🛠️ Stack Tecnológica

| Componente | Detalhes |
|---|---|
| ☕ Linguagem | Java 21 |
| 🌱 Framework | Spring Boot (3.3.4 – 4.0.5) |
| 🐘 Banco de Dados | PostgreSQL 17.4 |
| 🗃️ ORM | Spring Data JPA / Hibernate |
| 🔗 Comunicação Síncrona | Spring Cloud OpenFeign |
| 📨 Comunicação Assíncrona | Apache Kafka 7.2.15 |
| 🔨 Build | Maven |
| ⚙️ Utilitários | Lombok, MapStruct |
| ✅ Validação | Jakarta Validation |
| 📄 Relatórios | JasperReports 7.0.6, JasperReports PDF 7.0.0, OpenPDF 2.0.3 |
| 📦 Armazenamento de Objetos | MinIO (compatível com S3) |
| 📖 Documentação de API | Springdoc OpenAPI 2.5.0 (Swagger UI) |
| 🐳 Containers | Docker / Docker Compose |
| 📊 Monitoramento | Kafka UI (provectuslabs v0.7.2) |

### 🏗️ Arquitetura

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           Plataforma iCompras                            │
│                                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐  ┌─────────────┐ ┌──────────┐ │
│  │ Clientes │  │ Produtos │  │   Pedidos    │  │ Faturamento │ │Logistica │ │
│  │  :8082   │  │  :8081   │  │    :8080     │  │    :8083    │ │  :8084   │ │
│  └────┬─────┘  └────┬─────┘  └──┬───┬───┬───┘  └──────┬──────┘ └────┬─────┘ │
│       │             │           │   │   │             │             │       │
│       │             │      ┌────┘   │   └────┐        │             │       │
│       │             │      │        │        │        │             │       │
│       ▼             ▼      ▼        ▼        ▼        │             │       │
│  ┌─────────┐  ┌─────────┐ Clientes Produtos  │        │             │       │
│  │icompras │  │icompras │ Service  Service    │        │             │       │
│  │clientes │  │icompras │ (Feign)  (Feign)    │        │             │       │
│  │  (BD)   │  │produtos │                     │        │             │       │
│  └─────────┘  │  (BD)   │ ┌─────────┐  ┌──────────┐   │             │       │
│               └─────────┘ │icompras │  │  Kafka   │   │             │       │
│                           │pedidos  │─▶│  Broker  │──▶│             │       │
│                           │  (BD)   │  │  :29092  │──────────────▶│       │
│                           └─────────┘  └──────────┘   │             │       │
│                                              │        ▼             │       │
│                                         Kafka UI  ┌─────────┐      │       │
│                                          :8090    │  MinIO  │      │       │
│        PostgreSQL 17.4 — Porta 5555                │  :9000  │      │       │
│                                                   └─────────┘      │       │
│                                                                     │       │
│  Fluxo de Eventos Kafka:                                            │       │
│  Pedidos ──▶ [pedidos-pagos] ──▶ Faturamento ──▶ [pedidos-faturados]│       │
│  ──▶ Logistica ──▶ [pedidos-enviados] ──▶ Pedidos                   │       │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 🔧 Microsserviços

#### 👥 Clientes — Porta 8082

Gerencia o cadastro e consulta de clientes.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/clientes` | Cadastrar novo cliente |
| GET | `/clientes` | Listar todos os clientes |
| GET | `/clientes/{codigo}` | Buscar cliente por ID |
| DELETE | `/clientes/{codigo}` | Exclusão lógica do cliente (define `ativo` como `false`) |

**Campos da entidade:** `codigo`, `nome`, `cpf`, `logradouro`, `numero`, `bairro`, `email`, `telefone`, `ativo`

#### 📦 Produtos — Porta 8081

Gerencia o catálogo de produtos.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/produtos` | Cadastrar novo produto |
| GET | `/produtos` | Listar todos os produtos |
| GET | `/produtos/{codigo}` | Buscar produto por ID |
| DELETE | `/produtos/{codigo}` | Exclusão lógica do produto (define `ativo` como `false`) |

**Campos da entidade:** `codigo`, `nome`, `valorUnitario`, `descricao`, `ativo`

#### 🛍️ Pedidos — Porta 8080

Gerencia pedidos, itens e processamento de pagamento. Comunica-se com os serviços de Clientes e Produtos via Feign clients para validar dados antes de criar pedidos. Publica eventos de pagamento no Kafka após confirmação. Também consome eventos dos tópicos `icompras.pedidos-faturados` e `icompras.pedidos-enviados` para atualizar o status do pedido com URL da nota fiscal e código de rastreio.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/pedidos` | Criar novo pedido |
| GET | `/pedidos/{codigo}` | Consultar detalhes do pedido |
| POST | `/pedidos/pagamentos` | Retentar pagamento de um pedido |
| POST | `/pedidos/callback-pagamentos` | Webhook de status de pagamento |

**Consumidor Kafka:**
- Tópicos: `icompras.pedidos-faturados`, `icompras.pedidos-enviados`
- Grupo de consumo: `icompras-atualizacao-pedido`
- Atualiza status do pedido, URL da nota fiscal (`urlNf`) e código de rastreio (`codigoRastreio`)

**Campos da entidade pedido:** `codigo`, `codigoCliente`, `dataPedido`, `chavePagamento`, `observacoes`, `status`, `total`, `codigoRastreio`, `urlNf`, `itens`

**Campos do item do pedido:** `codigo`, `codigoProduto`, `quantidade`, `valorUnitario`

**Status do pedido:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

**Tipos de pagamento:** `CREDIT`, `DEBIT`, `PIX`

#### 🧾 Faturamento — Porta 8083

Consome eventos Kafka do serviço de Pedidos e gera notas fiscais em PDF utilizando JasperReports. Armazena as notas no MinIO (armazenamento de objetos compatível com S3). Após gerar a nota fiscal, publica um evento `FATURADO` no Kafka para que o serviço de Logística prossiga com o envio. Também expõe uma API REST para upload e download de arquivos.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/bucket` | Fazer upload de arquivo no MinIO |
| GET | `/bucket?filename={filename}` | Obter URL de download pré-assinada (1h de validade) |

**Consumidor Kafka:**
- Tópico: `icompras.pedidos-pagos`
- Grupo de consumo: `icompras-faturamento`

**Produtor Kafka:**
- Tópico: `icompras.pedidos-faturados`
- Publica ID do pedido, status `FATURADO` e URL da nota fiscal após geração do PDF

**Fluxo de geração de nota fiscal:**
1. 👂 Escuta eventos de confirmação de pagamento no Kafka
2. 📥 Deserializa os dados do pedido (info do cliente, itens, totais)
3. 📄 Gera um PDF de nota fiscal usando template JasperReports
4. ☁️ Faz upload do PDF no bucket MinIO `icompras.faturas`
5. 📣 Publica evento `FATURADO` com a URL da nota fiscal no Kafka

**Dependências:** Spring Kafka, JasperReports 7.0.6, JasperReports PDF 7.0.0, OpenPDF 2.0.3, MinIO Client 8.5.17, Jackson Datatype JSR-310/JDK8, Lombok

#### 🚚 Logística — Porta 8084

Gerencia o processamento de envios. Este serviço é inteiramente orientado a eventos, sem endpoints REST — consome eventos Kafka do serviço de Faturamento, gera um código de rastreio e publica um evento de envio no Kafka para que o serviço de Pedidos atualize o status do pedido.

**Consumidor Kafka:**
- Tópico: `icompras.pedidos-faturados`
- Grupo de consumo: `icompras-logistica`

**Produtor Kafka:**
- Tópico: `icompras.pedidos-enviados`
- Publica ID do pedido, status `ENVIADO` e código de rastreio

**Fluxo de envio:**
1. 👂 Escuta eventos de faturamento em `icompras.pedidos-faturados`
2. 📥 Recebe ID do pedido e URL da nota fiscal
3. 📦 Gera um código de rastreio (formato: `XX00000000BR`)
4. 📣 Publica evento `ENVIADO` com código de rastreio em `icompras.pedidos-enviados`

**Dependências:** Spring Kafka, Jackson Datatype JSR-310/JDK8, Lombok

---

##### 🔔 Webhook de Pagamento

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

##### 🔄 Retentar Pagamento

O endpoint `POST /pedidos/pagamentos` permite adicionar uma nova tentativa de pagamento para um pedido existente.

**Corpo da requisição:**

| Campo | Tipo | Descrição |
|---|---|---|
| `codigoPedido` | Long | ID do pedido |
| `dadosCartao` | String | Dados do cartão |
| `tipoPagamento` | String | Tipo de pagamento (`CREDIT`, `DEBIT`, `PIX`) |

### 📨 Eventos Kafka

O serviço de Pedidos publica eventos no Apache Kafka para processamento assíncrono.

| Tópico | Produtor | Consumidor | Descrição |
|---|---|---|---|
| `icompras.pedidos-pagos` | Pedidos | Faturamento | Publicado quando o pagamento de um pedido é confirmado |
| `icompras.pedidos-faturados` | Faturamento | Logística, Pedidos | Publicado quando a nota fiscal em PDF é gerada |
| `icompras.pedidos-enviados` | Logística | Pedidos | Publicado quando o envio é preparado com código de rastreio |

**Fluxo de eventos:**
```
Pedidos ──▶ [icompras.pedidos-pagos] ──▶ Faturamento
                                              │
                                              ▼
                                   Gerar Nota Fiscal PDF
                                   Upload no MinIO
                                              │
                                              ▼
Pedidos ◀── [icompras.pedidos-faturados] ◀── Faturamento
   │
   ▼
Logística ◀── [icompras.pedidos-faturados]
      │
      ▼
   Gerar Código de Rastreio
      │
      ▼
Pedidos ◀── [icompras.pedidos-enviados] ◀── Logística
```

O Kafka UI está disponível em `http://localhost:8090` para monitoramento de tópicos e mensagens.

### 🗄️ Banco de Dados

Cada microsserviço possui seu próprio banco de dados, seguindo o padrão **database-per-service**:

| Banco de Dados | Serviço | Tabelas |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reservado) | — |

### 📦 Armazenamento de Objetos

MinIO fornece armazenamento de objetos compatível com S3 para os PDFs de notas fiscais.

| Recurso | Porta | Descrição |
|---|---|---|
| MinIO API | 9000 | API compatível com S3 |
| MinIO Console | 9001 | Interface de gerenciamento |
| Bucket | — | `icompras.faturas` |

### ✅ Pré-requisitos

- ☕ Java 21+
- 🔨 Maven 3.8+
- 🐳 Docker e Docker Compose

### 🚀 Como Executar

**1️⃣ Iniciar a infraestrutura**

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

**2️⃣ Executar cada microsserviço**

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

# Logistica (porta 8084)
cd logistica
./mvnw spring-boot:run
```

**3️⃣ Testar as APIs**

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

### 📁 Estrutura do Projeto

```
icompras/
├── 👥 clientes/           # Microsserviço de clientes (Spring Boot 4.0.5)
├── 📦 produtos/           # Microsserviço de produtos (Spring Boot 3.4.4)
├── 🛍️ pedidos/            # Microsserviço de pedidos (Spring Boot 3.3.4)
├── 🧾 faturamento/        # Microsserviço de faturamento (Spring Boot 4.0.5)
├── 🚚 logistica/          # Microsserviço de logística (Spring Boot 4.0.5)
└── ⚙️ icompras-servicos/  # Infraestrutura
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

<p align="center">
  💻 <strong>Developed by Monalisa Menezes</strong> 💜
</p>
