package com.banco.simulacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação
 */
@SpringBootApplication
public class SistemaDeSimulacaoDeAtendimentoBancarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaDeSimulacaoDeAtendimentoBancarioApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("✅ Sistema de Simulação Bancária Iniciado!");
        System.out.println("📍 URL: http://localhost:8080");
        System.out.println("📚 Documentação: README.md");
        System.out.println("========================================\n");
    }
}
