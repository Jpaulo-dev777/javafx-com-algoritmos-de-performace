package com.banco.simulacao.controller;

import com.banco.simulacao.dto.EstatisticasDTO;
import com.banco.simulacao.model.Estatisticas;
import com.banco.simulacao.service.EstatisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para estatísticas do sistema
 */
@Slf4j
@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    /**
     * Retorna estatísticas completas do sistema
     * GET /api/estatisticas
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        Estatisticas stats = estatisticasService.calcularEstatisticas();
        EstatisticasDTO dto = estatisticasService.converterParaDTO(stats);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "estatisticas", dto
        ));
    }

    /**
     * Retorna estatísticas em formato de texto
     * GET /api/estatisticas/texto
     */
    @GetMapping("/texto")
    public ResponseEntity<String> obterEstatisticasTexto() {
        Estatisticas stats = estatisticasService.calcularEstatisticas();

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== ESTATÍSTICAS DO SISTEMA ==========\n\n");
        sb.append(String.format("Total de Clientes Cadastrados: %d\n", stats.getTotalClientesCadastrados()));
        sb.append(String.format("Total de Clientes Atendidos: %d\n", stats.getTotalClientesAtendidos()));
        sb.append(String.format("Total de Clientes Aguardando: %d\n", stats.getTotalClientesAguardando()));
        sb.append(String.format("\nTempo Médio de Espera: %.2f minutos\n", stats.getTempoMedioEspera()));
        sb.append(String.format("Tempo Médio de Atendimento: %.2f minutos\n", stats.getTempoMedioAtendimento()));
        sb.append(String.format("Tempo Médio Total: %.2f minutos\n", stats.getTempoMedioTotal()));
        sb.append(String.format("\nTempo Máximo de Espera: %d minutos\n", stats.getTempoMaximoEspera()));
        sb.append(String.format("Tempo Mínimo de Espera: %d minutos\n", stats.getTempoMinimoEspera()));
        sb.append(String.format("\nTaxa de Ocupação: %.2f%%\n", stats.getTaxaOcupacao()));
        sb.append(String.format("Throughput: %.2f clientes/minuto\n", stats.getThroughput()));
        sb.append(String.format("Tempo Total de Simulação: %.2f minutos\n", stats.getTempoTotalSimulacao()));
        sb.append("\n--- Análise de Complexidade ---\n");
        sb.append(String.format("Inserção: %s\n", stats.getComplexidadeInsercao()));
        sb.append(String.format("Remoção: %s\n", stats.getComplexidadeRemocao()));
        sb.append(String.format("Ordenação: %s\n", stats.getComplexidadeOrdenacao()));
        sb.append("\n==========================================\n");

        return ResponseEntity.ok(sb.toString());
    }
}
