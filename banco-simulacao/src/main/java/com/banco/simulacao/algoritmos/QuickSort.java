package com.banco.simulacao.algoritmos;

import com.banco.simulacao.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do algoritmo Quick Sort
 * Complexidade: O(n log n) médio, O(n²) pior caso
 * Espaço: O(log n) devido à recursão
 */
@Component
public class QuickSort implements AlgoritmoOrdenacao {

    @Override
    public List<Cliente> ordenar(List<Cliente> clientes) {
        if (clientes == null || clientes.size() <= 1) {
            return new ArrayList<>(clientes != null ? clientes : List.of());
        }

        List<Cliente> resultado = new ArrayList<>(clientes);
        quickSortRecursivo(resultado, 0, resultado.size() - 1);
        return resultado;
    }

    /**
     * Método recursivo do Quick Sort
     * Complexidade: O(n log n) médio
     */
    private void quickSortRecursivo(List<Cliente> lista, int inicio, int fim) {
        if (inicio < fim) {
            int indicePivo = particionar(lista, inicio, fim);
            quickSortRecursivo(lista, inicio, indicePivo - 1);
            quickSortRecursivo(lista, indicePivo + 1, fim);
        }
    }

    /**
     * Particiona a lista em torno do pivô
     * Complexidade: O(n)
     */
    private int particionar(List<Cliente> lista, int inicio, int fim) {
        Cliente pivo = lista.get(fim);
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (lista.get(j).compareTo(pivo) <= 0) {
                i++;
                trocar(lista, i, j);
            }
        }

        trocar(lista, i + 1, fim);
        return i + 1;
    }

    /**
     * Troca dois elementos de posição
     * Complexidade: O(1)
     */
    private void trocar(List<Cliente> lista, int i, int j) {
        Cliente temp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, temp);
    }

    @Override
    public String getNome() {
        return "Quick Sort";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O(n log n) médio, O(n²) pior caso";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(log n)";
    }
}
