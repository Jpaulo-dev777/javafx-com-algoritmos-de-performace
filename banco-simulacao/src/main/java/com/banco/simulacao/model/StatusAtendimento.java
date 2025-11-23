package com.banco.simulacao.model;

import lombok.Getter;

@Getter
public enum StatusAtendimento {
    AGUARDANDO("Aguardando na Fila"),
    EM_ATENDIMENTO("Em Atendimento"),
    ATENDIDO("Atendido"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAtendimento(String descricao) {
        this.descricao = descricao;
    }
}
