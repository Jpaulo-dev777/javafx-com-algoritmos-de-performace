package com.banco.simulacao.algoritmos;

import com.banco.simulacao.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do algoritmo Heap Sort
 * Complexidade: O(n log n) garantido
 * Espaço: O(1) - ordena in-place
 * Vantagem: Usa menos memória que Merge Sort
 */
@Component
public class HeapSort implements AlgoritmoOrdenacao {

    @Override
    public List<Cliente> ordenar(List<Cliente> clientes) {
        if (clientes == null || clientes.size() <= 1) {
            return new ArrayList<>(clientes != null ? clientes : List.of());
        }

        List<Cliente> resultado = new ArrayList<>(clientes);
        heapSortImpl(resultado);
        return resultado;
    }

    /**
     * Implementação do Heap Sort
     * Complexidade: O(n log n)
     */
    private void heapSortImpl(List<Cliente> lista) {
        int n = lista.size();

        // Constrói o heap (rearranja o array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(lista, n, i);
        }

        // Extrai elementos do heap um por um
        for (int i = n - 1; i > 0; i--) {
            trocar(lista, 0, i);
            heapify(lista, i, 0);
        }
    }

    /**
     * Transforma uma subárvore em um heap
     * Complexidade: O(log n)
     */
    private void heapify(List<Cliente> lista, int n, int i) {
        int maior = i;
        int esquerda = 2 * i + 1;
        int direita = 2 * i + 2;

        if (esquerda < n && lista.get(esquerda).compareTo(lista.get(maior)) > 0) {
            maior = esquerda;
        }

        if (direita < n && lista.get(direita).compareTo(lista.get(maior)) > 0) {
            maior = direita;
        }

        if (maior != i) {
            trocar(lista, i, maior);
            heapify(lista, n, maior);
        }
    }

    /**
     * Troca dois elementos
     * Complexidade: O(1)
     */
    private void trocar(List<Cliente> lista, int i, int j) {
        Cliente temp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, temp);
    }

    @Override
    public String getNome() {
        return "Heap Sort";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O(n log n) garantido";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(1)";
    }
}
