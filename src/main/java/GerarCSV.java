import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

                if (i % 1000 == 0) {
                    System.out.println("  → " + i + " registros gerados...");
                }
            }

            System.out.println("✓ Arquivo dados.csv criado com sucesso!");
            System.out.println("✓ Localização: " + new File("data/dados.csv").getAbsolutePath());

        } catch (IOException e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



