package com.banco.simulacao.model;

import lombok.Getter;

@Getter
public enum TipoCliente {
    COMUM(3, "Cliente Comum"),
    PREFERENCIAL(2, "Cliente Preferencial (Idoso/Gestante/PCD)"),
    CORPORATIVO(1, "Cliente Corporativo");

    private final int prioridade;
    private final String descricao;

    TipoCliente(int prioridade, String descricao) {
        this.prioridade = prioridade;
        this.descricao = descricao;
    }
}
