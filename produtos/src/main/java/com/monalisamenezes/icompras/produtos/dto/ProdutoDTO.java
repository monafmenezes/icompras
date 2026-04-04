package com.monalisamenezes.icompras.produtos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long codigo,

        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotNull(message = "O valor não pode ser nulo.")
        @Positive(message = "O preço deve ser maior que zero.")
        BigDecimal valorUnitario,

        String descricao
) {
}
