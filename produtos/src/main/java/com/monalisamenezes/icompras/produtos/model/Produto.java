package com.monalisamenezes.icompras.produtos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "valor_unitario", nullable = false, precision = 16, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = true, length = 250)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    public void prePersist() {
        setAtivo(true);
    }
}
