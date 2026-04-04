package com.monalisamenezes.icompras.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteDTO(
        Long codigo,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O CPF é obrigatório.")
        @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos.")
        String cpf,

        String logradouro,
        String numero,
        String bairro,

        @Email(message = "E-mail inválido.")
        String email,

        String telefone
) {
}