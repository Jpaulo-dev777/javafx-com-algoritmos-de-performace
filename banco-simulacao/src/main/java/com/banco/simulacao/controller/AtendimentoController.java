package com.banco.simulacao.controller;

import com.banco.simulacao.dto.ClienteDTO;
import com.banco.simulacao.model.Cliente;
import com.banco.simulacao.service.AtendimentoService;
import com.banco.simulacao.service.ComparadorAlgoritmosService;
import com.banco.simulacao.service.EstatisticasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de atendimentos
 */
@Slf4j
@RestController
@RequestMapping("/api/atendimento")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;
    private final EstatisticasService estatisticasService;
    private final ComparadorAlgoritmosService comparadorService;

    /**
     * Cadastra um novo cliente na fila
     * POST /api/atendimento/cadastrar
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, Object>> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
        try {
            Cliente cliente = atendimentoService.cadastrarCliente(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Cliente cadastrado com sucesso");
            response.put("cliente", cliente);
            response.put("tamanhoFilas", atendimentoService.getTotalClientesAguardando());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao cadastrar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("sucesso", false, "mensagem", "Erro ao cadastrar cliente"));
        }
    }

    /**
     * Processa o próximo atendimento
     * POST /api/atendimento/processar
     */
    @PostMapping("/processar")
    public ResponseEntity<Map<String, Object>> processarProximo() {
        Cliente cliente = atendimentoService.processarProximoAtendimento();

        if (cliente == null) {
            return ResponseEntity.ok(Map.of(
                    "sucesso", false,
                    "mensagem", "Não há clientes na fila"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Cliente atendido com sucesso",
                "cliente", cliente,
                "tempoEspera", cliente.getTempoEsperaMinutos() + " minutos",
                "tempoTotal", cliente.getTempoTotalMinutos() + " minutos"
        ));
    }

    /**
     * Processa todos os atendimentos pendentes
     * POST /api/atendimento/processar-todos
     */
    @PostMapping("/processar-todos")
    public ResponseEntity<Map<String, Object>> processarTodos() {
        long inicio = System.currentTimeMillis();

        int totalProcessado = atendimentoService.processarTodosAtendimentos();

        long tempoExecucao = System.currentTimeMillis() - inicio;
        var stats = estatisticasService.calcularEstatisticas();
        var statsDTO = estatisticasService.converterParaDTO(stats);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Todos os atendimentos foram processados",
                "totalProcessado", totalProcessado,
                "tempoExecucao", tempoExecucao + " ms",
                "estatisticas", statsDTO
        ));
    }

    /**
     * Reordena as filas usando o algoritmo especificado
     * POST /api/atendimento/reordenar?algoritmo=quicksort
     */
    @PostMapping("/reordenar")
    public ResponseEntity<Map<String, Object>> reordenarFilas(
            @RequestParam(defaultValue = "quicksort") String algoritmo) {

        long tempoExecucao = atendimentoService.reordenarFilas(algoritmo);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Filas reordenadas com sucesso",
                "algoritmo", algoritmo,
                "tempoExecucao", tempoExecucao + " ms"
        ));
    }

    /**
     * Lista todos os clientes aguardando na fila
     * GET /api/atendimento/fila
     */
    @GetMapping("/fila")
    public ResponseEntity<Map<String, Object>> listarFila() {
        List<Cliente> clientes = atendimentoService.listarClientesNaFila();

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "total", clientes.size(),
                "clientes", clientes
        ));
    }

    /**
     * Lista todos os clientes já atendidos
     * GET /api/atendimento/atendidos
     */
    @GetMapping("/atendidos")
    public ResponseEntity<Map<String, Object>> listarAtendidos() {
        List<Cliente> clientes = atendimentoService.listarClientesAtendidos();

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "total", clientes.size(),
                "clientes", clientes
        ));
    }

    /**
     * Retorna o tamanho de cada fila
     * GET /api/atendimento/tamanho-filas
     */
    @GetMapping("/tamanho-filas")
    public ResponseEntity<Map<String, Object>> obterTamanhoFilas() {
        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", true);
        response.put("totalCadastrados", atendimentoService.getTotalClientesCadastrados());
        response.put("totalAtendidos", atendimentoService.getTotalClientesAtendidos());
        response.put("totalAguardando", atendimentoService.getTotalClientesAguardando());

        return ResponseEntity.ok(response);
    }

    /**
     * Compara os algoritmos de ordenação
     * GET /api/atendimento/comparar-algoritmos
     */
    @GetMapping("/comparar-algoritmos")
    public ResponseEntity<Object> compararAlgoritmos(
            @RequestParam(defaultValue = "1") int numeroTestes) {

        List<Cliente> clientes = atendimentoService.listarClientesNaFila();

        if (clientes.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "sucesso", false,
                    "mensagem", "Não há clientes na fila para comparação"
            ));
        }

        var comparacao = numeroTestes > 1
                ? comparadorService.compararComMultiplosTestes(clientes, numeroTestes)
                : comparadorService.compararAlgoritmos(clientes);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "comparacao", comparacao
        ));
    }

    /**
     * Simula cadastro de múltiplos clientes para testes
     * POST /api/atendimento/simular?quantidade=50
     */
    @PostMapping("/simular")
    public ResponseEntity<Map<String, Object>> simularCadastros(
            @RequestParam(defaultValue = "50") int quantidade) {

        if (quantidade <= 0 || quantidade > 10000) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Quantidade deve estar entre 1 e 10000"
            ));
        }

        List<Cliente> clientes = atendimentoService.simularCadastros(quantidade);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Clientes simulados com sucesso",
                "quantidade", quantidade,
                "totalAguardando", atendimentoService.getTotalClientesAguardando()
        ));
    }

    /**
     * Limpa todas as filas e histórico
     * DELETE /api/atendimento/limpar
     */
    @DeleteMapping("/limpar")
    public ResponseEntity<Map<String, Object>> limparTudo() {
        atendimentoService.limparSistema();

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Sistema limpo com sucesso"
        ));
    }
}
