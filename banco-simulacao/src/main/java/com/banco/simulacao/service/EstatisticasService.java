package com.banco.simulacao.service;

import com.banco.simulacao.dto.EstatisticasDTO;
import com.banco.simulacao.estruturas.GerenciadorFilas;
import com.banco.simulacao.model.Cliente;
import com.banco.simulacao.model.Estatisticas;
import com.banco.simulacao.model.TipoCliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service para cálculo de estatísticas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EstatisticasService {

    private final AtendimentoService atendimentoService;
    private final GerenciadorFilas gerenciadorFilas;

    /**
     * Calcula estatísticas completas
     * Complexidade: O(n)
     */
    public Estatisticas calcularEstatisticas() {
        List<Cliente> atendidos = atendimentoService.listarClientesAtendidos();

        Estatisticas stats = Estatisticas.builder()
                .totalClientesCadastrados(atendimentoService.getTotalClientesCadastrados())
                .totalClientesAtendidos(atendimentoService.getTotalClientesAtendidos())
                .totalClientesAguardando(atendimentoService.getTotalClientesAguardando())
                .totalClientesCancelados(0)
                .build();

        Map<String, Integer> tamanhos = gerenciadorFilas.obterTamanhos();
        stats.setTamanhoFilaComum(tamanhos.get("comum"));
        stats.setTamanhoFilaPreferencial(tamanhos.get("preferencial"));
        stats.setTamanhoFilaCorporativa(tamanhos.get("corporativo"));

        if (atendidos.isEmpty()) {
            configurarEstatisticasVazias(stats);
            return stats;
        }

        calcularTemposMedios(stats, atendidos);
        calcularTemposExtremos(stats, atendidos);
        calcularTempoTotalSimulacao(stats, atendidos);

        stats.setClientesPorTipo(calcularClientesPorTipo(atendidos));
        stats.setTempoMedioEsperaPorTipo(calcularTempoMedioEsperaPorTipo(atendidos));
        stats.setTempoMedioAtendimentoPorTipo(calcularTempoMedioAtendimentoPorTipo(atendidos));

        configurarAnaliseComplexidade(stats);

        return stats;
    }

    private void calcularTemposMedios(Estatisticas stats, List<Cliente> atendidos) {
        double somaEspera = atendidos.stream()
                .mapToLong(Cliente::getTempoEsperaMinutos)
                .sum();
        stats.setTempoMedioEspera(somaEspera / atendidos.size());

        double somaAtendimento = atendidos.stream()
                .mapToLong(Cliente::getTempoAtendimentoReal)
                .sum();
        stats.setTempoMedioAtendimento(somaAtendimento / atendidos.size());

        double somaTotal = atendidos.stream()
                .mapToLong(Cliente::getTempoTotalMinutos)
                .sum();
        stats.setTempoMedioTotal(somaTotal / atendidos.size());
    }

    private void calcularTemposExtremos(Estatisticas stats, List<Cliente> atendidos) {
        stats.setTempoMaximoEspera(
                atendidos.stream()
                        .mapToLong(Cliente::getTempoEsperaMinutos)
                        .max()
                        .orElse(0)
        );

        stats.setTempoMinimoEspera(
                atendidos.stream()
                        .mapToLong(Cliente::getTempoEsperaMinutos)
                        .min()
                        .orElse(0)
        );
    }

    private void calcularTempoTotalSimulacao(Estatisticas stats, List<Cliente> atendidos) {
        if (atendidos.isEmpty()) {
            stats.setTempoTotalSimulacao(0.0);
            return;
        }

        LocalDateTime primeira = atendidos.stream()
                .map(Cliente::getHorarioChegada)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime ultima = atendidos.stream()
                .map(Cliente::getHorarioFimAtendimento)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        stats.setTempoTotalSimulacao((double) Duration.between(primeira, ultima).toMinutes());
    }

    private Map<TipoCliente, Integer> calcularClientesPorTipo(List<Cliente> clientes) {
        Map<TipoCliente, Integer> resultado = new HashMap<>();
        for (TipoCliente tipo : TipoCliente.values()) {
            long count = clientes.stream()
                    .filter(c -> c.getTipo() == tipo)
                    .count();
            resultado.put(tipo, (int) count);
        }
        return resultado;
    }

    private Map<TipoCliente, Double> calcularTempoMedioEsperaPorTipo(List<Cliente> clientes) {
        Map<TipoCliente, Double> resultado = new HashMap<>();
        for (TipoCliente tipo : TipoCliente.values()) {
            double media = clientes.stream()
                    .filter(c -> c.getTipo() == tipo)
                    .mapToLong(Cliente::getTempoEsperaMinutos)
                    .average()
                    .orElse(0.0);
            resultado.put(tipo, media);
        }
        return resultado;
    }

    private Map<TipoCliente, Double> calcularTempoMedioAtendimentoPorTipo(List<Cliente> clientes) {
        Map<TipoCliente, Double> resultado = new HashMap<>();
        for (TipoCliente tipo : TipoCliente.values()) {
            double media = clientes.stream()
                    .filter(c -> c.getTipo() == tipo)
                    .mapToLong(Cliente::getTempoAtendimentoReal)
                    .average()
                    .orElse(0.0);
            resultado.put(tipo, media);
        }
        return resultado;
    }

    private void configurarAnaliseComplexidade(Estatisticas stats) {
        stats.setComplexidadeInsercao("O(1) - Inserção no final da fila");
        stats.setComplexidadeRemocao("O(1) - Remoção do início da fila");
        stats.setComplexidadeOrdenacao("O(n log n) - QuickSort/MergeSort/HeapSort");
    }

    private void configurarEstatisticasVazias(Estatisticas stats) {
        stats.setTempoMedioEspera(0.0);
        stats.setTempoMedioAtendimento(0.0);
        stats.setTempoMedioTotal(0.0);
        stats.setTempoMaximoEspera(0L);
        stats.setTempoMinimoEspera(0L);
        stats.setTempoTotalSimulacao(0.0);
        configurarAnaliseComplexidade(stats);
    }

    public EstatisticasDTO converterParaDTO(Estatisticas stats) {
        return EstatisticasDTO.builder()
                .totalClientesCadastrados(stats.getTotalClientesCadastrados())
                .totalClientesAtendidos(stats.getTotalClientesAtendidos())
                .totalClientesAguardando(stats.getTotalClientesAguardando())
                .tempoMedioEspera(stats.getTempoMedioEspera())
                .tempoMedioAtendimento(stats.getTempoMedioAtendimento())
                .tempoMedioTotal(stats.getTempoMedioTotal())
                .tempoMaximoEspera(stats.getTempoMaximoEspera())
                .tempoMinimoEspera(stats.getTempoMinimoEspera())
                .taxaOcupacao(stats.getTaxaOcupacao())
                .throughput(stats.getThroughput())
                .tempoTotalSimulacao(stats.getTempoTotalSimulacao())
                .clientesPorTipo(stats.getClientesPorTipo())
                .tempoMedioEsperaPorTipo(stats.getTempoMedioEsperaPorTipo())
                .tamanhoFilas(gerenciadorFilas.obterTamanhos())
                .complexidadeInsercao(stats.getComplexidadeInsercao())
                .complexidadeRemocao(stats.getComplexidadeRemocao())
                .complexidadeOrdenacao(stats.getComplexidadeOrdenacao())
                .build();
    }
}
