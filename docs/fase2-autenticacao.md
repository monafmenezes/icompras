# Fase 2 - Microservico de Autenticacao

Este documento detalha o plano de acao da Fase 2 do iCompras: criar o microservico `auth`, responsavel por cadastro, login, emissao de JWT e exposicao das informacoes do usuario autenticado.

## Objetivo

Entregar um servico independente de autenticacao pronto para ser integrado ao futuro API Gateway e, depois, aos demais microservicos como Resource Servers.

## Resultado Esperado

Ao final da fase, o projeto deve permitir:

- Cadastrar usuarios com senha armazenada de forma segura.
- Autenticar usuario por email e senha.
- Emitir JWT com identificador, email e roles.
- Validar o token emitido pelo proprio servico.
- Consultar os dados basicos do usuario autenticado.
- Rodar o servico localmente via Maven e via Docker Compose.
- Acessar Swagger e Actuator do servico `auth`.

## Checklist de Implementacao

### 1) Criacao do servico

- [ ] Criar modulo ou diretorio `auth`.
- [ ] Usar Java 21 e Spring Boot alinhado aos demais servicos.
- [ ] Adicionar dependencias:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - OAuth2 Resource Server ou biblioteca JWT adotada pelo projeto
  - PostgreSQL Driver
  - Validation
  - Lombok
  - MapStruct, se mantido como padrao
  - Springdoc OpenAPI
  - Spring Boot Actuator
  - Testes com JUnit 5, Mockito, AssertJ e Testcontainers
- [ ] Criar `AuthApplication`.
- [ ] Definir `spring.application.name=auth`.
- [ ] Definir porta padrao `8085`.

### 2) Banco de dados e configuracao

- [ ] Criar banco `icomprasauth` no PostgreSQL local.
- [ ] Adicionar configuracao do banco no `docker-compose.yml` raiz.
- [ ] Criar profiles `local`, `docker` e `test`.
- [ ] Padronizar variaveis de ambiente:

```text
SERVER_PORT
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
JPA_DDL_AUTO
JPA_SHOW_SQL
JWT_ISSUER
JWT_SECRET
JWT_EXPIRATION_SECONDS
JWT_REFRESH_EXPIRATION_SECONDS
LOG_LEVEL_ROOT
LOG_LEVEL_APP
```

### 3) Modelo de dominio

- [ ] Criar entidade `User`.
- [ ] Criar enum `Role`.
- [ ] Criar repository `UserRepository`.
- [ ] Garantir email unico.
- [ ] Armazenar senha somente como hash.
- [ ] Registrar datas de criacao e atualizacao.
- [ ] Permitir ativar/desativar usuario.

Modelo inicial:

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

Roles iniciais:

```text
ADMIN
CUSTOMER
SERVICE
```

## Endpoints

### Publicos

```text
POST /auth/register
POST /auth/login
POST /auth/refresh
```

### Protegidos

```text
GET /auth/me
POST /auth/validate
```

O endpoint `/auth/validate` e opcional para o MVP se o Gateway validar JWT localmente por chave/secret compartilhado.

## Contratos Sugeridos

### Cadastro

```json
{
  "name": "Maria Silva",
  "email": "maria@email.com",
  "password": "Senha@123"
}
```

Resposta:

```json
{
  "id": "uuid",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "role": "CUSTOMER",
  "active": true,
  "createdAt": "2026-04-29T16:00:00"
}
```

### Login

```json
{
  "email": "maria@email.com",
  "password": "Senha@123"
}
```

Resposta:

```json
{
  "accessToken": "jwt",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "refreshToken": "jwt-ou-token-opaco"
}
```

### Usuario autenticado

```json
{
  "id": "uuid",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "roles": ["CUSTOMER"]
}
```

## Regras de Seguranca

- [ ] Usar `PasswordEncoder` com BCrypt.
- [ ] Bloquear login de usuario inativo.
- [ ] Nao retornar `passwordHash` em nenhuma resposta.
- [ ] Normalizar email antes de salvar e autenticar.
- [ ] Validar senha minima no cadastro.
- [ ] Assinar JWT com secret vindo de variavel de ambiente.
- [ ] Incluir claims minimas:
  - `sub`
  - `email`
  - `roles`
  - `iss`
  - `iat`
  - `exp`
- [ ] Definir expiracao curta para access token.
- [ ] Definir estrategia de refresh token antes de integrar ao frontend.

## Erros Esperados

| Caso | Status |
|---|---:|
| Dados invalidos no cadastro/login | 400 |
| Email ja cadastrado | 409 |
| Credenciais invalidas | 401 |
| Usuario inativo | 403 |
| Token ausente, invalido ou expirado | 401 |
| Usuario autenticado nao encontrado | 404 |

Todos os erros devem seguir o padrao definido no plano geral.

## Testes

### Unitarios

- [ ] Cadastro com dados validos.
- [ ] Cadastro recusado por email duplicado.
- [ ] Cadastro recusado por senha invalida.
- [ ] Login com credenciais validas.
- [ ] Login recusado por senha incorreta.
- [ ] Login recusado por usuario inativo.
- [ ] Geracao de JWT com claims esperadas.
- [ ] Validacao de token expirado/invalido.

### Controller

- [ ] `POST /auth/register` retorna `201`.
- [ ] `POST /auth/register` retorna `400` para payload invalido.
- [ ] `POST /auth/register` retorna `409` para email duplicado.
- [ ] `POST /auth/login` retorna token em caso de sucesso.
- [ ] `POST /auth/login` retorna `401` para credenciais invalidas.
- [ ] `GET /auth/me` retorna `401` sem token.
- [ ] `GET /auth/me` retorna usuario autenticado com token valido.

### Integracao

- [ ] Subir PostgreSQL com Testcontainers.
- [ ] Persistir usuario real no banco.
- [ ] Autenticar usuario cadastrado.
- [ ] Validar fluxo completo: cadastro -> login -> `/auth/me`.

## Documentacao

- [ ] Swagger disponivel em `/swagger-ui.html`.
- [ ] Actuator disponivel em `/actuator/health`.
- [ ] Atualizar `docs/portas-e-dependencias.md` com `auth:8085`.
- [ ] Atualizar `docs/icompras.http` com requests de cadastro, login e `/auth/me`.
- [ ] Atualizar README com a nova fase e instrucao de execucao.

## Ordem Recomendada

1. Criar estrutura do servico `auth`.
2. Configurar Maven, `application.yml`, profiles e Actuator.
3. Criar entidade, enum, repository e DTOs.
4. Implementar cadastro com BCrypt.
5. Implementar login e emissao de JWT.
6. Implementar filtro/configuracao de seguranca para `/auth/me`.
7. Adicionar Swagger e padrao de erros.
8. Criar testes unitarios e de controller.
9. Criar teste de integracao com PostgreSQL.
10. Incluir `auth` no Docker Compose raiz e nos documentos de portas/requests.

## Definition of Done

- [ ] Servico `auth` compila.
- [ ] Testes passam.
- [ ] Swagger abre corretamente.
- [ ] Actuator responde `UP`.
- [ ] Cadastro e login funcionam via `.http`.
- [ ] Senha e dados sensiveis nao aparecem nas respostas.
- [ ] JWT emitido contem claims necessarias para o Gateway.
- [ ] Servico roda via Docker Compose.
- [ ] Documentacao atualizada.

