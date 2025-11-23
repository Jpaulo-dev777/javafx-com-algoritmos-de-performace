package com.banco.simulacao.algoritmos;

import com.banco.simulacao.model.Cliente;
import java.util.List;

/**
 * Interface para algoritmos de ordenação
 * Permite comparação entre diferentes implementações
 */
public interface AlgoritmoOrdenacao {

    /**
     * Ordena a lista de clientes
     * @param clientes Lista a ser ordenada
     * @return Lista ordenada
     */
    List<Cliente> ordenar(List<Cliente> clientes);

    /**
     * Retorna o nome do algoritmo
     */
    String getNome();

    /**
     * Retorna a complexidade de tempo
     */
    String getComplexidadeTempo();

    /**
     * Retorna a complexidade de espaço
     */
    String getComplexidadeEspaco();
}
