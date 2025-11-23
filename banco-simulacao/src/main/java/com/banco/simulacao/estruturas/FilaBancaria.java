package com.banco.simulacao.estruturas;

import com.banco.simulacao.model.Cliente;
import com.banco.simulacao.model.TipoCliente;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Implementação de Fila usando LinkedList
 * Estrutura de dados: Queue (FIFO - First In First Out)
 */
@Getter
public class FilaBancaria {

    private final TipoCliente tipoFila;
    private final Queue<Cliente> fila;
    private final List<Cliente> historico;

    public FilaBancaria(TipoCliente tipo) {
        this.tipoFila = tipo;
        this.fila = new LinkedList<>();
        this.historico = new ArrayList<>();
    }

    /**
     * Adiciona cliente na fila
     * Complexidade: O(1)
     */
    public boolean adicionar(Cliente cliente) {
        if (cliente.getTipo() != tipoFila) {
            return false;
        }
        return fila.offer(cliente);
    }

    /**
     * Remove e retorna o próximo cliente da fila
     * Complexidade: O(1)
     */
    public Cliente atender() {
        Cliente cliente = fila.poll();
        if (cliente != null) {
            historico.add(cliente);
        }
        return cliente;
    }

    /**
     * Visualiza o próximo cliente sem remover
     * Complexidade: O(1)
     */
    public Cliente proximoCliente() {
        return fila.peek();
    }

    /**
     * Retorna o tamanho da fila
     * Complexidade: O(1)
     */
    public int tamanho() {
        return fila.size();
    }

    /**
     * Verifica se a fila está vazia
     * Complexidade: O(1)
     */
    public boolean estaVazia() {
        return fila.isEmpty();
    }

    /**
     * Retorna todos os clientes na fila como lista
     * Complexidade: O(n)
     */
    public List<Cliente> listarTodos() {
        return new ArrayList<>(fila);
    }

    /**
     * Limpa a fila
     * Complexidade: O(1)
     */
    public void limpar() {
        fila.clear();
    }

    /**
     * Substitui a fila atual por uma nova lista ordenada
     * Complexidade: O(n)
     */
    public void substituirFila(List<Cliente> novosClientes) {
        fila.clear();
        fila.addAll(novosClientes);
    }
}
