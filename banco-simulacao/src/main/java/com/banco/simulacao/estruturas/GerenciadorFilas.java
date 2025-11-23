package com.banco.simulacao.estruturas;

import com.banco.simulacao.model.Cliente;
import com.banco.simulacao.model.TipoCliente;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerencia as três filas do banco
 * Estrutura de dados: Múltiplas Queues organizadas por prioridade
 */
@Component
@Getter
public class GerenciadorFilas {

    private final FilaBancaria filaComum;
    private final FilaBancaria filaPreferencial;
    private final FilaBancaria filaCorporativa;

    private int contadorCorporativo = 0;
    private int contadorPreferencial = 0;
    private int contadorComum = 0;

    public GerenciadorFilas() {
        this.filaComum = new FilaBancaria(TipoCliente.COMUM);
        this.filaPreferencial = new FilaBancaria(TipoCliente.PREFERENCIAL);
        this.filaCorporativa = new FilaBancaria(TipoCliente.CORPORATIVO);
    }

    /**
     * Adiciona cliente na fila apropriada
     * Complexidade: O(1)
     */
    public boolean adicionarCliente(Cliente cliente) {
        return switch (cliente.getTipo()) {
            case COMUM -> filaComum.adicionar(cliente);
            case PREFERENCIAL -> filaPreferencial.adicionar(cliente);
            case CORPORATIVO -> filaCorporativa.adicionar(cliente);
        };
    }

    /**
     * Obtém próximo cliente seguindo regra de prioridade
     * Regra: 1 corporativo -> 1 preferencial -> 2 comuns (ciclo)
     * Complexidade: O(1)
     */
    public Cliente obterProximoCliente() {
        if (!filaCorporativa.estaVazia() && contadorCorporativo == 0) {
            contadorCorporativo++;
            resetarContadoresSeNecessario();
            return filaCorporativa.atender();
        }

        if (!filaPreferencial.estaVazia() && contadorPreferencial == 0) {
            contadorPreferencial++;
            resetarContadoresSeNecessario();
            return filaPreferencial.atender();
        }

        if (!filaComum.estaVazia() && contadorComum < 2) {
            contadorComum++;
            resetarContadoresSeNecessario();
            return filaComum.atender();
        }

        if (!filaCorporativa.estaVazia()) {
            return filaCorporativa.atender();
        }
        if (!filaPreferencial.estaVazia()) {
            return filaPreferencial.atender();
        }
        if (!filaComum.estaVazia()) {
            return filaComum.atender();
        }

        return null;
    }

    private void resetarContadoresSeNecessario() {
        if (contadorCorporativo >= 1 && contadorPreferencial >= 1 && contadorComum >= 2) {
            contadorCorporativo = 0;
            contadorPreferencial = 0;
            contadorComum = 0;
        }
    }

    /**
     * Retorna todos os clientes em todas as filas
     * Complexidade: O(n)
     */
    public List<Cliente> listarTodosClientes() {
        List<Cliente> todos = new ArrayList<>();
        todos.addAll(filaCorporativa.listarTodos());
        todos.addAll(filaPreferencial.listarTodos());
        todos.addAll(filaComum.listarTodos());
        return todos;
    }

    /**
     * Retorna tamanho total de todas as filas
     * Complexidade: O(1)
     */
    public int tamanhoTotal() {
        return filaComum.tamanho() +
                filaPreferencial.tamanho() +
                filaCorporativa.tamanho();
    }

    /**
     * Verifica se há clientes em alguma fila
     * Complexidade: O(1)
     */
    public boolean temClientes() {
        return !filaComum.estaVazia() ||
                !filaPreferencial.estaVazia() ||
                !filaCorporativa.estaVazia();
    }

    /**
     * Retorna mapa com tamanho de cada fila
     * Complexidade: O(1)
     */
    public Map<String, Integer> obterTamanhos() {
        Map<String, Integer> tamanhos = new HashMap<>();
        tamanhos.put("comum", filaComum.tamanho());
        tamanhos.put("preferencial", filaPreferencial.tamanho());
        tamanhos.put("corporativo", filaCorporativa.tamanho());
        tamanhos.put("total", tamanhoTotal());
        return tamanhos;
    }

    /**
     * Limpa todas as filas
     * Complexidade: O(1)
     */
    public void limparTodas() {
        filaComum.limpar();
        filaPreferencial.limpar();
        filaCorporativa.limpar();
        contadorCorporativo = 0;
        contadorPreferencial = 0;
        contadorComum = 0;
    }
}
