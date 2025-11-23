package com.banco.simulacao.service;

import com.banco.simulacao.algoritmos.AlgoritmoOrdenacao;
import com.banco.simulacao.algoritmos.HeapSort;
import com.banco.simulacao.algoritmos.MergeSort;
import com.banco.simulacao.algoritmos.QuickSort;
import com.banco.simulacao.dto.ClienteDTO;
import com.banco.simulacao.estruturas.GerenciadorFilas;
import com.banco.simulacao.model.Cliente;
import com.banco.simulacao.model.StatusAtendimento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service responsável pelo gerenciamento de atendimentos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final GerenciadorFilas gerenciadorFilas;
    private final QuickSort quickSort;
    private final MergeSort mergeSort;
    private final HeapSort heapSort;

    private final AtomicLong geradorId = new AtomicLong(1);
    private final List<Cliente> clientesAtendidos = new ArrayList<>();
    private final List<Cliente> clientesCancelados = new ArrayList<>();

    /**
     * Cadastra um novo cliente na fila apropriada
     * Complexidade: O(1)
     */
    public Cliente cadastrarCliente(ClienteDTO dto) {
        Cliente cliente = Cliente.builder()
                .id(geradorId.getAndIncrement())
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .tempoEstimadoAtendimento(dto.getTempoEstimadoAtendimento())
                .horarioChegada(LocalDateTime.now())
                .status(StatusAtendimento.AGUARDANDO)
                .build();

        boolean adicionado = gerenciadorFilas.adicionarCliente(cliente);

        if (adicionado) {
            log.info("Cliente cadastrado: {} - Tipo: {}", cliente.getNome(), cliente.getTipo());
        }

        return cliente;
    }

    /**
     * Processa o próximo atendimento
     * Complexidade: O(1)
     */
    public Cliente processarProximoAtendimento() {
        Cliente cliente = gerenciadorFilas.obterProximoCliente();

        if (cliente == null) {
            log.warn("Nenhum cliente na fila");
            return null;
        }

        cliente.setHorarioInicioAtendimento(LocalDateTime.now());
        cliente.setStatus(StatusAtendimento.EM_ATENDIMENTO);

        log.info("Atendendo: {} - Espera: {} min",
                cliente.getNome(), cliente.getTempoEsperaMinutos());

        simularAtendimento(cliente);

        cliente.setHorarioFimAtendimento(LocalDateTime.now());
        cliente.setStatus(StatusAtendimento.ATENDIDO);
        clientesAtendidos.add(cliente);

        log.info("Finalizado: {} - Total: {} min",
                cliente.getNome(), cliente.getTempoTotalMinutos());

        return cliente;
    }

    /**
     * Simula o tempo de atendimento
     */
    private void simularAtendimento(Cliente cliente) {
        try {
            Thread.sleep(cliente.getTempoEstimadoAtendimento() * 100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Atendimento interrompido: {}", cliente.getNome());
        }
    }

    /**
     * Processa todos os atendimentos pendentes
     * Complexidade: O(n)
     */
    public int processarTodosAtendimentos() {
        log.info("Processando todos os atendimentos...");

        int contador = 0;
        while (gerenciadorFilas.temClientes()) {
            processarProximoAtendimento();
            contador++;
        }

        log.info("Processamento concluído: {} clientes", contador);
        return contador;
    }

    /**
     * Reordena todas as filas
     * Complexidade: O(n log n)
     */
    public long reordenarFilas(String nomeAlgoritmo) {
        log.info("Reordenando filas com: {}", nomeAlgoritmo);

        List<Cliente> todosClientes = gerenciadorFilas.listarTodosClientes();

        if (todosClientes.isEmpty()) {
            return 0;
        }

        AlgoritmoOrdenacao algoritmo = selecionarAlgoritmo(nomeAlgoritmo);

        long inicio = System.nanoTime();
        List<Cliente> clientesOrdenados = algoritmo.ordenar(todosClientes);
        long tempoExecucao = (System.nanoTime() - inicio) / 1_000_000;

        gerenciadorFilas.limparTodas();

        for (Cliente cliente : clientesOrdenados) {
            gerenciadorFilas.adicionarCliente(cliente);
        }

        log.info("Filas reordenadas: {} - Tempo: {} ms", algoritmo.getNome(), tempoExecucao);

        return tempoExecucao;
    }

    private AlgoritmoOrdenacao selecionarAlgoritmo(String nome) {
        return switch (nome.toLowerCase()) {
            case "mergesort" -> mergeSort;
            case "heapsort" -> heapSort;
            default -> quickSort;
        };
    }

    /**
     * Simula cadastro de múltiplos clientes
     * Complexidade: O(n)
     */
    public List<Cliente> simularCadastros(int quantidade) {
        log.info("Simulando {} clientes", quantidade);

        List<Cliente> clientesSimulados = new ArrayList<>();
        var tipos = com.banco.simulacao.model.TipoCliente.values();

        for (int i = 1; i <= quantidade; i++) {
            var tipo = tipos[i % tipos.length];
            int tempoEstimado = 5 + (int)(Math.random() * 20);

            ClienteDTO dto = ClienteDTO.builder()
                    .nome("Cliente " + i)
                    .tipo(tipo)
                    .tempoEstimadoAtendimento(tempoEstimado)
                    .build();

            clientesSimulados.add(cadastrarCliente(dto));
        }

        return clientesSimulados;
    }

    public List<Cliente> listarClientesNaFila() {
        return gerenciadorFilas.listarTodosClientes();
    }

    public List<Cliente> listarClientesAtendidos() {
        return new ArrayList<>(clientesAtendidos);
    }

    public int getTotalClientesCadastrados() {
        return (int) geradorId.get() - 1;
    }

    public int getTotalClientesAtendidos() {
        return clientesAtendidos.size();
    }

    public int getTotalClientesAguardando() {
        return gerenciadorFilas.tamanhoTotal();
    }

    public void limparSistema() {
        gerenciadorFilas.limparTodas();
        clientesAtendidos.clear();
        clientesCancelados.clear();
        geradorId.set(1);
        log.info("Sistema limpo");
    }
}
