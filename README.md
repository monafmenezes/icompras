# iCompras

**[English](#english)** | **[Português](#português)**

---

## English

### About

**iCompras** is a microservices-based e-commerce platform built with Java 21 and Spring Boot. The system manages customers, products, and orders through independent services that communicate via REST APIs using Spring Cloud OpenFeign.

### Tech Stack

| Component | Details |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot (3.3.4 – 4.0.5) |
| Database | PostgreSQL 17.4 |
| ORM | Spring Data JPA / Hibernate |
| Communication | Spring Cloud OpenFeign |
| Build Tool | Maven |
| Utilities | Lombok, MapStruct |
| Validation | Jakarta Validation |
| Containers | Docker / Docker Compose |

### Architecture

```
┌─────────────────────────────────────────────────────┐
│                    iCompras Platform                 │
│                                                     │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐    │
│  │ Clientes │   │ Produtos │   │   Pedidos    │    │
│  │  :8082   │   │  :8081   │   │    :8080     │    │
│  └────┬─────┘   └────┬─────┘   └──┬───┬───┬───┘    │
│       │              │             │   │   │        │
│       │              │        ┌────┘   │   └────┐   │
│       │              │        │        │        │   │
│       ▼              ▼        ▼        ▼        ▼   │
│  ┌─────────┐   ┌─────────┐  Clientes Produtos  │   │
│  │icompras │   │icompras │  Service  Service    │   │
│  │clientes │   │produtos │  (Feign)  (Feign)    │   │
│  │  (DB)   │   │  (DB)   │                      │   │
│  └─────────┘   └─────────┘  ┌─────────┐        │   │
│                              │icompras │        │   │
│                              │pedidos  │        │   │
│                              │  (DB)   │        │   │
│                              └─────────┘        │   │
│                                                     │
│          PostgreSQL 17.4 — Port 5555                │
└─────────────────────────────────────────────────────┘
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

Manages orders, line items, and payment processing. Communicates with the Clientes and Produtos services via Feign clients to validate data before creating orders.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/pedidos` | Create a new order |

**Order statuses:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

### Database

Each microservice has its own database, following the **database-per-service** pattern:

| Database | Service | Tables |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reserved) | — |

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Getting Started

**1. Start the database**

```bash
cd icompras-servicos/database
docker compose up -d
```

This starts a PostgreSQL 17.4 container on port **5555** and automatically creates all required databases and tables.

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
```

### Project Structure

```
icompras/
├── clientes/              # Customer microservice
├── produtos/              # Product microservice
├── pedidos/               # Order microservice
└── icompras-servicos/     # Infrastructure (Docker, DB scripts)
    └── database/
        ├── docker-compose.yml
        ├── schema.sql
        └── init-db/
            └── create_databases.sql
```

---

## Português

### Sobre

**iCompras** é uma plataforma de e-commerce baseada em microsserviços, construída com Java 21 e Spring Boot. O sistema gerencia clientes, produtos e pedidos por meio de serviços independentes que se comunicam via APIs REST utilizando Spring Cloud OpenFeign.

### Stack Tecnológica

| Componente | Detalhes |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot (3.3.4 – 4.0.5) |
| Banco de Dados | PostgreSQL 17.4 |
| ORM | Spring Data JPA / Hibernate |
| Comunicação | Spring Cloud OpenFeign |
| Build | Maven |
| Utilitários | Lombok, MapStruct |
| Validação | Jakarta Validation |
| Containers | Docker / Docker Compose |

### Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                  Plataforma iCompras                 │
│                                                     │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐    │
│  │ Clientes │   │ Produtos │   │   Pedidos    │    │
│  │  :8082   │   │  :8081   │   │    :8080     │    │
│  └────┬─────┘   └────┬─────┘   └──┬───┬───┬───┘    │
│       │              │             │   │   │        │
│       │              │        ┌────┘   │   └────┐   │
│       │              │        │        │        │   │
│       ▼              ▼        ▼        ▼        ▼   │
│  ┌─────────┐   ┌─────────┐  Clientes Produtos  │   │
│  │icompras │   │icompras │  Service  Service    │   │
│  │clientes │   │produtos │  (Feign)  (Feign)    │   │
│  │  (BD)   │   │  (BD)   │                      │   │
│  └─────────┘   └─────────┘  ┌─────────┐        │   │
│                              │icompras │        │   │
│                              │pedidos  │        │   │
│                              │  (BD)   │        │   │
│                              └─────────┘        │   │
│                                                     │
│          PostgreSQL 17.4 — Porta 5555               │
└─────────────────────────────────────────────────────┘
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

Gerencia pedidos, itens e processamento de pagamento. Comunica-se com os serviços de Clientes e Produtos via Feign clients para validar dados antes de criar pedidos.

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/pedidos` | Criar novo pedido |

**Status do pedido:** `REALIZADO`, `PAGO`, `FATURADO`, `ENVIADO`, `ERRO_PAGAMENTO`, `PREPARANDO_ENVIO`

### Banco de Dados

Cada microsserviço possui seu próprio banco de dados, seguindo o padrão **database-per-service**:

| Banco de Dados | Serviço | Tabelas |
|---|---|---|
| `icomprasclientes` | Clientes | `clientes` |
| `icomprasprodutos` | Produtos | `produtos` |
| `icompraspedidos` | Pedidos | `pedidos`, `item_pedido` |
| `icomprasauth` | Auth (reservado) | — |

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker e Docker Compose

### Como Executar

**1. Iniciar o banco de dados**

```bash
cd icompras-servicos/database
docker compose up -d
```

Isso inicia um container PostgreSQL 17.4 na porta **5555** e cria automaticamente todos os bancos de dados e tabelas necessários.

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
```

### Estrutura do Projeto

```
icompras/
├── clientes/              # Microsserviço de clientes
├── produtos/              # Microsserviço de produtos
├── pedidos/               # Microsserviço de pedidos
└── icompras-servicos/     # Infraestrutura (Docker, scripts de BD)
    └── database/
        ├── docker-compose.yml
        ├── schema.sql
        └── init-db/
            └── create_databases.sql
```

---

**Developed by Monalisa Menezes**
