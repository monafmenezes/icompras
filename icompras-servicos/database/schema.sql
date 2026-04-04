CREATE
DATABASE icomprasclientes;
create table clientes
(
    codigo     serial       not null primary key,
    nome       varchar(150) not null,
    cpf        char(11)     not null,
    logradouro varchar(100),
    numero     varchar(10),
    bairro     varchar(100),
    email      varchar(150),
    telefone   varchar(20)
);

CREATE
DATABASE icomprasprodutos;

create table produtos
(
    codigo     serial not null primary key,
    nome       varchar(100) not null,
    valor_unitario decimal(16,2) not null,
    descricao varchar(250)
);

CREATE
DATABASE icompraspedidos;

create table pedidos(
    codigo serial not null primary key,
    codigo_cliente bigint not null,
    codigo_pedido bigint not null,
    data_pedido timestamp not null default now(),
    chave_pagamento text,
    observacoes text,
    status varchar(20) check ( status in ('realizado', 'pago', 'faturado', 'enviado', 'erro_pagamento', 'preparando_envio') ),
    total decimal (16,2) not null,
    codigo_rastreio varchar(255),
    url_nf text
)

create table item_pedido(
    codigo serial not null primary key,
    codigo_pedido bigint not null references pedidos (codigo),
    codigo_produto bigint not null,
    quantidade int not null,
    valor_unitario decimal (16,2)
);


CREATE
DATABASE icomprasauth;