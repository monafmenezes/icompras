package com.monalisamenezes.icompras.pedidos.model;

import com.monalisamenezes.icompras.pedidos.client.representation.ClienteRepresentation;
import com.monalisamenezes.icompras.pedidos.model.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "codigo_cliente", nullable = false)
    private Long codigoCliente;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "chave_pagamento", columnDefinition = "text")
    private String chavePagamento;

    @Column(name = "observacoes", columnDefinition = "text")
    private String observacoes;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Column(name = "total", nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Column(name = "codigo_rastreio")
    private String codigoRastreio;

    @Column(name = "url_nf", columnDefinition = "text")
    private String urlNf;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @Transient //não é salvo no banco de dados
    private DadosPagamento dadosPagamento;

    @Transient
    private ClienteRepresentation dadosCliente;

    public void setItens(List<ItemPedido> novosItens) {
        if (this.itens == null) {
            this.itens = novosItens;
        } else {
            this.itens.clear();
            if (novosItens != null) {
                this.itens.addAll(novosItens);
            }
        }
    }
}
