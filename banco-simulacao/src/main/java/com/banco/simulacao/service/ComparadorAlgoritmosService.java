package com.banco.simulacao.service;

import com.banco.simulacao.algoritmos.AlgoritmoOrdenacao;
import com.banco.simulacao.algoritmos.HeapSort;
import com.banco.simulacao.algoritmos.MergeSort;
import com.banco.simulacao.algoritmos.QuickSort;
import com.banco.simulacao.dto.ComparacaoAlgoritmosDTO;
import com.banco.simulacao.dto.ComparacaoAlgoritmosDTO.ResultadoAlgoritmo;
import com.banco.simulacao.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service para comparação de desempenho entre algoritmos
 * Análise de complexidade e custo computacional
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComparadorAlgoritmosService {

    private final QuickSort quickSort;
    private final MergeSort mergeSort;
    private final HeapSort heapSort;

    /**
     * Compara todos os algoritmos de ordenação
     * Complexidade: O(n log n) * número de algoritmos
     */
    public ComparacaoAlgoritmosDTO compararAlgoritmos(List<Cliente> clientes) {
        if (clientes == null || clientes.isEmpty()) {
            log.warn("Lista vazia para comparação");
            return criarComparacaoVazia();
        }

        log.info("Comparando algoritmos com {} elementos", clientes.size());

        Map<String, ResultadoAlgoritmo> resultados = new HashMap<>();

        resultados.put("quicksort", testarAlgoritmo(quickSort, clientes));
        resultados.put("mergesort", testarAlgoritmo(mergeSort, clientes));
        resultados.put("heapsort", testarAlgoritmo(heapSort, clientes));

        String maisRapido = encontrarMaisRapido(resultados);
        String recomendacao = gerarRecomendacao(clientes.size(), maisRapido, resultados);

        return ComparacaoAlgoritmosDTO.builder()
                .quantidadeElementos(clientes.size())
                .resultados(resultados)
                .algoritmoMaisRapido(maisRapido)
                .recomendacao(recomendacao)
                .build();
    }

    /**
     * Testa um algoritmo específico
     */
    private ResultadoAlgoritmo testarAlgoritmo(AlgoritmoOrdenacao algoritmo, List<Cliente> clientes) {
        List<Cliente> copia = new ArrayList<>(clientes);

        long inicio = System.nanoTime();
        List<Cliente> resultado = algoritmo.ordenar(copia);
        long tempoNs = System.nanoTime() - inicio;
        double tempoMs = tempoNs / 1_000_000.0;

        boolean ordenadoCorretamente = verificarOrdenacao(resultado);

        log.debug("{}: {} ns ({} ms) - Correto: {}",
                algoritmo.getNome(), tempoNs, tempoMs, ordenadoCorretamente);

        return ResultadoAlgoritmo.builder()
                .nome(algoritmo.getNome())
                .tempoExecucaoNs(tempoNs)
                .tempoExecucaoMs(tempoMs)
                .complexidadeTempo(algoritmo.getComplexidadeTempo())
                .complexidadeEspaco(algoritmo.getComplexidadeEspaco())
                .ordenadoCorretamente(ordenadoCorretamente)
                .build();
    }

    /**
     * Verifica se a lista está ordenada
     * Complexidade: O(n)
     */
    private boolean verificarOrdenacao(List<Cliente> clientes) {
        for (int i = 0; i < clientes.size() - 1; i++) {
            if (clientes.get(i).compareTo(clientes.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encontra o algoritmo mais rápido
     */
    private String encontrarMaisRapido(Map<String, ResultadoAlgoritmo> resultados) {
        return resultados.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().getTempoExecucaoNs()))
                .map(Map.Entry::getKey)
                .orElse("quicksort");
    }

    /**
     * Gera recomendação baseada nos resultados
     */
    private String gerarRecomendacao(int tamanho, String maisRapido,
                                     Map<String, ResultadoAlgoritmo> resultados) {
        StringBuilder sb = new StringBuilder();

        sb.append("========== ANÁLISE DE DESEMPENHO ==========\n\n");
        sb.append(String.format("Quantidade de elementos: %d\n", tamanho));
        sb.append(String.format("Algoritmo mais rápido: %s\n\n", maisRapido.toUpperCase()));

        sb.append("--- Tempos de Execução ---\n");
        resultados.forEach((nome, res) -> {
            sb.append(String.format("• %s: %.3f ms\n", res.getNome(), res.getTempoExecucaoMs()));
        });

        sb.append("\n--- Análise de Complexidade ---\n");
        resultados.forEach((nome, res) -> {
            sb.append(String.format("• %s:\n", res.getNome()));
            sb.append(String.format("  - Tempo: %s\n", res.getComplexidadeTempo()));
            sb.append(String.format("  - Espaço: %s\n", res.getComplexidadeEspaco()));
        });

        sb.append("\n--- Recomendações ---\n");

        if (tamanho < 50) {
            sb.append("• Tamanho pequeno: Qualquer algoritmo é eficiente\n");
            sb.append("• QuickSort geralmente é mais rápido para dados pequenos\n");
        } else if (tamanho < 1000) {
            sb.append("• Tamanho médio: QuickSort ou MergeSort recomendados\n");
            sb.append("• QuickSort: Mais rápido em média\n");
            sb.append("• MergeSort: Mais estável e previsível\n");
        } else {
            sb.append("• Tamanho grande: MergeSort ou HeapSort recomendados\n");
            sb.append("• MergeSort: O(n log n) garantido, mas usa mais memória\n");
            sb.append("• HeapSort: O(n log n) garantido, usa menos memória\n");
            sb.append("• QuickSort: Pode ter pior caso O(n²)\n");
        }

        sb.append("\n--- Quando usar cada algoritmo ---\n");
        sb.append("• QuickSort: Dados aleatórios, performance média importante\n");
        sb.append("• MergeSort: Estabilidade necessária, dados já parcialmente ordenados\n");
        sb.append("• HeapSort: Memória limitada, garantia de O(n log n)\n");

        sb.append("\n==========================================\n");

        return sb.toString();
    }

    /**
     * Cria comparação vazia
     */
    private ComparacaoAlgoritmosDTO criarComparacaoVazia() {
        return ComparacaoAlgoritmosDTO.builder()
                .quantidadeElementos(0)
                .resultados(new HashMap<>())
                .algoritmoMaisRapido("N/A")
                .recomendacao("Não há dados para comparação")
                .build();
    }

    /**
     * Executa múltiplos testes e retorna média
     */
    public ComparacaoAlgoritmosDTO compararComMultiplosTestes(List<Cliente> clientes, int numeroTestes) {
        if (numeroTestes <= 0) {
            numeroTestes = 1;
        }

        log.info("Executando {} testes para cada algoritmo", numeroTestes);

        Map<String, List<Long>> temposPorAlgoritmo = new HashMap<>();
        temposPorAlgoritmo.put("quicksort", new ArrayList<>());
        temposPorAlgoritmo.put("mergesort", new ArrayList<>());
        temposPorAlgoritmo.put("heapsort", new ArrayList<>());

        for (int i = 0; i < numeroTestes; i++) {
            ComparacaoAlgoritmosDTO resultado = compararAlgoritmos(clientes);

            resultado.getResultados().forEach((nome, res) ->
                    temposPorAlgoritmo.get(nome).add(res.getTempoExecucaoNs())
            );
        }

        Map<String, ResultadoAlgoritmo> resultadosMedias = new HashMap<>();

        temposPorAlgoritmo.forEach((nome, tempos) -> {
            double mediaNs = tempos.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);

            AlgoritmoOrdenacao algoritmo = obterAlgoritmo(nome);

            resultadosMedias.put(nome, ResultadoAlgoritmo.builder()
                    .nome(algoritmo.getNome())
                    .tempoExecucaoNs((long) mediaNs)
                    .tempoExecucaoMs(mediaNs / 1_000_000.0)
                    .complexidadeTempo(algoritmo.getComplexidadeTempo())
                    .complexidadeEspaco(algoritmo.getComplexidadeEspaco())
                    .ordenadoCorretamente(true)
                    .build());
        });

        String maisRapido = encontrarMaisRapido(resultadosMedias);

        return ComparacaoAlgoritmosDTO.builder()
                .quantidadeElementos(clientes.size())
                .resultados(resultadosMedias)
                .algoritmoMaisRapido(maisRapido)
                .recomendacao(gerarRecomendacao(clientes.size(), maisRapido, resultadosMedias) +
                        "\n\n(Média de " + numeroTestes + " testes)")
                .build();
    }

    private AlgoritmoOrdenacao obterAlgoritmo(String nome) {
        return switch (nome.toLowerCase()) {
            case "mergesort" -> mergeSort;
            case "heapsort" -> heapSort;
            default -> quickSort;
        };
    }
}
