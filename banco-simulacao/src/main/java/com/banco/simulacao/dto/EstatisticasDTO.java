package com.banco.simulacao.dto;

import com.banco.simulacao.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasDTO {

    private Integer totalClientesCadastrados;
    private Integer totalClientesAtendidos;
    private Integer totalClientesAguardando;

    private Double tempoMedioEspera;
    private Double tempoMedioAtendimento;
    private Double tempoMedioTotal;

    private Long tempoMaximoEspera;
    private Long tempoMinimoEspera;

    private Double taxaOcupacao;
    private Double throughput;
    private Double tempoTotalSimulacao;

    @Builder.Default
    private Map<TipoCliente, Integer> clientesPorTipo = new HashMap<>();

    @Builder.Default
    private Map<TipoCliente, Double> tempoMedioEsperaPorTipo = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> tamanhoFilas = new HashMap<>();

    private String complexidadeInsercao;
    private String complexidadeRemocao;
    private String complexidadeOrdenacao;
}
