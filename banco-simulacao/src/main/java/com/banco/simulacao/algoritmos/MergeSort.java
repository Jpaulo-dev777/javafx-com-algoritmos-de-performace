package com.banco.simulacao.algoritmos;

import com.banco.simulacao.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do algoritmo Merge Sort
 * Complexidade: O(n log n) garantido
 * Espaço: O(n)
 * Vantagem: Estável e previsível
 */
@Component
public class MergeSort implements AlgoritmoOrdenacao {

    @Override
    public List<Cliente> ordenar(List<Cliente> clientes) {
        if (clientes == null || clientes.size() <= 1) {
            return new ArrayList<>(clientes != null ? clientes : List.of());
        }

        return mergeSortRecursivo(new ArrayList<>(clientes));
    }

    /**
     * Método recursivo do Merge Sort
     * Complexidade: O(n log n)
     */
    private List<Cliente> mergeSortRecursivo(List<Cliente> lista) {
        if (lista.size() <= 1) {
            return lista;
        }

        int meio = lista.size() / 2;
        List<Cliente> esquerda = mergeSortRecursivo(new ArrayList<>(lista.subList(0, meio)));
        List<Cliente> direita = mergeSortRecursivo(new ArrayList<>(lista.subList(meio, lista.size())));

        return merge(esquerda, direita);
    }

    /**
     * Mescla duas listas ordenadas
     * Complexidade: O(n)
     */
    private List<Cliente> merge(List<Cliente> esquerda, List<Cliente> direita) {
        List<Cliente> resultado = new ArrayList<>();
        int i = 0, j = 0;

        while (i < esquerda.size() && j < direita.size()) {
            if (esquerda.get(i).compareTo(direita.get(j)) <= 0) {
                resultado.add(esquerda.get(i++));
            } else {
                resultado.add(direita.get(j++));
            }
        }

        while (i < esquerda.size()) {
            resultado.add(esquerda.get(i++));
        }

        while (j < direita.size()) {
            resultado.add(direita.get(j++));
        }

        return resultado;
    }

    @Override
    public String getNome() {
        return "Merge Sort";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O(n log n) garantido";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(n)";
    }
}
