package com.banco.simulacao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Armazena estatísticas completas do sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estatisticas {

    private Integer totalClientesCadastrados;
    private Integer totalClientesAtendidos;
    private Integer totalClientesAguardando;
    private Integer totalClientesCancelados;

    private Double tempoMedioEspera;
    private Double tempoMedioAtendimento;
    private Double tempoMedioTotal;

    private Long tempoMaximoEspera;
    private Long tempoMinimoEspera;

    private Double tempoTotalSimulacao;

    @Builder.Default
    private Map<TipoCliente, Integer> clientesPorTipo = new HashMap<>();

    @Builder.Default
    private Map<TipoCliente, Double> tempoMedioEsperaPorTipo = new HashMap<>();

    @Builder.Default
    private Map<TipoCliente, Double> tempoMedioAtendimentoPorTipo = new HashMap<>();

    private String complexidadeInsercao;
    private String complexidadeRemocao;
    private String complexidadeOrdenacao;
    private Long tempoExecucaoMs;

    private Integer tamanhoFilaComum;
    private Integer tamanhoFilaPreferencial;
    private Integer tamanhoFilaCorporativa;

    public double getTaxaOcupacao() {
        if (totalClientesCadastrados == null || totalClientesCadastrados == 0) return 0.0;
        return (double) totalClientesAtendidos / totalClientesCadastrados * 100;
    }

    public double getThroughput() {
        if (tempoTotalSimulacao == null || tempoTotalSimulacao == 0) return 0.0;
        return totalClientesAtendidos / tempoTotalSimulacao;
    }
}
