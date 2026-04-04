package enums;

public enum StatusPedido {
    REALIZADO("realizado"),
    PAGO("pago"),
    FATURADO("faturado"),
    ENVIADO("enviado"),
    ERRO_PAGAMENTO("erro_pagamento"),
    PREPARANDO_ENVIO("preparando_envio");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }
}
