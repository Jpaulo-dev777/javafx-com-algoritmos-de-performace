package com.banco.simulacao.dto;

import com.banco.simulacao.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Tipo de cliente é obrigatório")
    private TipoCliente tipo;

    @NotNull(message = "Tempo estimado é obrigatório")
    @Min(value = 1, message = "Tempo estimado deve ser maior que 0")
    private Integer tempoEstimadoAtendimento;
}
