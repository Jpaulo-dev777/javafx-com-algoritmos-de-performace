package com.banco.simulacao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Representa um cliente no sistema bancário
 * Estrutura de dados: Objeto com atributos específicos
 * Complexidade de acesso: O(1)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements Comparable<Cliente> {

    private Long id;
    private String nome;
    private TipoCliente tipo;
    private Integer tempoEstimadoAtendimento;
    private LocalDateTime horarioChegada;
    private LocalDateTime horarioInicioAtendimento;
    private LocalDateTime horarioFimAtendimento;

    @Builder.Default
    private StatusAtendimento status = StatusAtendimento.AGUARDANDO;

    /**
     * Calcula tempo de espera em minutos
     * Complexidade: O(1)
     */
    public long getTempoEsperaMinutos() {
        if (horarioInicioAtendimento == null) {
            return Duration.between(horarioChegada, LocalDateTime.now()).toMinutes();
        }
        return Duration.between(horarioChegada, horarioInicioAtendimento).toMinutes();
    }

    /**
     * Calcula tempo total (espera + atendimento) em minutos
     * Complexidade: O(1)
     */
    public long getTempoTotalMinutos() {
        if (horarioFimAtendimento == null) {
            return getTempoEsperaMinutos();
        }
        return Duration.between(horarioChegada, horarioFimAtendimento).toMinutes();
    }

    /**
     * Calcula tempo real de atendimento
     * Complexidade: O(1)
     */
    public long getTempoAtendimentoReal() {
        if (horarioInicioAtendimento == null || horarioFimAtendimento == null) {
            return 0;
        }
        return Duration.between(horarioInicioAtendimento, horarioFimAtendimento).toMinutes();
    }

    /**
     * Comparação para ordenação por prioridade e tempo de chegada
     * Complexidade: O(1)
     */
    @Override
    public int compareTo(Cliente outro) {
        int comparacaoPrioridade = Integer.compare(
                this.tipo.getPrioridade(),
                outro.tipo.getPrioridade()
        );

        if (comparacaoPrioridade != 0) {
            return comparacaoPrioridade;
        }

        return this.horarioChegada.compareTo(outro.horarioChegada);
    }
}
