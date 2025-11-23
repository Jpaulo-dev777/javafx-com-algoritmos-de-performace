import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
<<<<<<< HEAD
import java.time.LocalDate;
import java.util.Random;

public class GerarCSV {

    public static void main(String[] args) {
        int quantidade = 5000; // padrão

        if (args.length > 0) {
            try {
                quantidade = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido para quantidade. Usando 5000 por padrão.");
            }
        }

        new File("data").mkdirs();

        try (FileWriter writer = new FileWriter("data/dados.csv")) {
            writer.write("id,nome,cpf,prioridade,data\n");
            Random rand = new Random();

            System.out.println("Gerando " + quantidade + " registros...");

            for (int i = 1; i <= quantidade; i++) {
                String nome = gerarNomeAleatorio(rand);
                String cpf = gerarCpfAleatorio(rand);
                // prioridade: 1 (mais alta) até 5 (mais baixa), por exemplo
                int prioridade = 1 + rand.nextInt(5);

                // data aleatória nos últimos 5 anos
                LocalDate hoje = LocalDate.now();
                int diasAntes = rand.nextInt(5 * 365);
                LocalDate data = hoje.minusDays(diasAntes);

                writer.write(String.format(
                        "%d,%s,%s,%d,%s%n",
                        i, nome, cpf, prioridade, data.toString()
                ));
=======
import java.util.Random;

public class GerarCSV {
    public static void main(String[] args) {
        // Cria a pasta data se não existir
        new File("data").mkdirs();

        try (FileWriter writer = new FileWriter("data/dados.csv")) {
            writer.write("id,valor,categoria,timestamp\n");
            Random rand = new Random();

            System.out.println("Gerando 5000 registros...");

            for (int i = 1; i <= 5000; i++) {
                int valor = rand.nextInt(10000);
                String categoria = "CAT_" + (char)('A' + rand.nextInt(5));
                long timestamp = System.currentTimeMillis() - rand.nextInt(100000000);

                writer.write(String.format("%d,%d,%s,%d\n", i, valor, categoria, timestamp));
>>>>>>> 4feb8cb38ea803252964391d208c6c8ea242800e

                if (i % 1000 == 0) {
                    System.out.println("  → " + i + " registros gerados...");
                }
            }

            System.out.println("✓ Arquivo dados.csv criado com sucesso!");
            System.out.println("✓ Localização: " + new File("data/dados.csv").getAbsolutePath());
<<<<<<< HEAD
=======

>>>>>>> 4feb8cb38ea803252964391d208c6c8ea242800e
        } catch (IOException e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
<<<<<<< HEAD

    private static String gerarNomeAleatorio(Random rand) {
        String[] primeiros = {"Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda", "Gustavo", "Helena", "Igor", "Julia", "Kaique", "Larissa", "Marcos", "Nathalia", "Otavio", "Patricia", "Rafael", "Sara", "Tiago", "Vanessa"};
        String[] sobrenomes = {"Silva", "Souza", "Oliveira", "Pereira", "Costa", "Santos", "Rodrigues", "Almeida", "Nascimento", "Lima", "Araújo", "Gomes", "Ribeiro", "Carvalho"};

        String primeiro = primeiros[rand.nextInt(primeiros.length)];
        String sobrenome = sobrenomes[rand.nextInt(sobrenomes.length)];
        return primeiro + " " + sobrenome;
    }

    private static String gerarCpfAleatorio(Random rand) {
        // Apenas para teste: gera 11 dígitos numéricos sem validação real de CPF
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}
=======
}



>>>>>>> 4feb8cb38ea803252964391d208c6c8ea242800e
