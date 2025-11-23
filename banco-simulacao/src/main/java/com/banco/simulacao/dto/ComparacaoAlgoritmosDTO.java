package com.banco.simulacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparacaoAlgoritmosDTO {

    private Integer quantidadeElementos;

    @Builder.Default
    private Map<String, ResultadoAlgoritmo> resultados = new HashMap<>();

    private String algoritmoMaisRapido;
    private String recomendacao;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoAlgoritmo {
        private String nome;
        private Long tempoExecucaoNs;
        private Double tempoExecucaoMs;
        private String complexidadeTempo;
        private String complexidadeEspaco;
        private Boolean ordenadoCorretamente;
    }
}
