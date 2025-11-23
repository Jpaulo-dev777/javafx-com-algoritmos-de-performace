import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompararSorts {

    public static void main(String[] args) {
        String caminhoCSV = "data/dados.csv";

        try {
            // 1. Ler registros do CSV
            Registro[] registros = lerRegistros(caminhoCSV);
            int n = registros.length;
            if (n == 0) {
                System.out.println("Nenhum registro encontrado no CSV.");
                return;
            }

            // 2. Criar cópias para cada algoritmo
            Registro[] paraMerge = copiarArray(registros);
            Registro[] paraQuick = copiarArray(registros);
            Registro[] paraHeap = copiarArray(registros);

            // 3. Merge Sort
            long inicioMerge = System.nanoTime();
            AlgoritmosOrdenacao.mergeSort(paraMerge);
            long fimMerge = System.nanoTime();
            long tempoMerge = fimMerge - inicioMerge;

            // 4. Quick Sort
            long inicioQuick = System.nanoTime();
            AlgoritmosOrdenacao.quickSort(paraQuick);
            long fimQuick = System.nanoTime();
            long tempoQuick = fimQuick - inicioQuick;

            // 5. Heap Sort
            long inicioHeap = System.nanoTime();
            AlgoritmosOrdenacao.heapSort(paraHeap);
            long fimHeap = System.nanoTime();
            long tempoHeap = fimHeap - inicioHeap;

            // 6. Validar se todos produziram a mesma ordenação por prioridade
            boolean mergeVsQuick = mesmaOrdenacao(paraMerge, paraQuick);
            boolean mergeVsHeap = mesmaOrdenacao(paraMerge, paraHeap);

            // 7. Gerar relatório
            gerarRelatorio(tempoMerge, tempoQuick, tempoHeap,
                    mergeVsQuick && mergeVsHeap, n);

            System.out.println("✓ Comparação concluída. Relatório gerado em data/relatorio_sorts.txt");
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Registro[] lerRegistros(String caminho) throws IOException {
        List<Registro> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine(); // cabeçalho
            if (linha == null) return new Registro[0];

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");

                if (partes.length < 5) continue;

                int id = Integer.parseInt(partes[0]);
                String nome = partes[1];
                String cpf = partes[2];
                int prioridade = Integer.parseInt(partes[3]);
                String data = partes[4];

                lista.add(new Registro(id, nome, cpf, prioridade, data));
            }
        }

        return lista.toArray(new Registro[0]);
    }

    private static Registro[] copiarArray(Registro[] origem) {
        Registro[] copia = new Registro[origem.length];
        System.arraycopy(origem, 0, copia, 0, origem.length);
        return copia;
    }

    private static boolean mesmaOrdenacao(Registro[] a, Registro[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            // Se a prioridade for igual, poderia conferir outros campos também, mas
            // para este exemplo vamos considerar só prioridade + id
            if (a[i].prioridade != b[i].prioridade ||
                    a[i].id != b[i].id) {
                return false;
            }
        }
        return true;
    }

    private static void gerarRelatorio(long tempoMerge, long tempoQuick, long tempoHeap,
                                       boolean mesmaOrdenacao, int qtdRegistros) throws IOException {
        String caminhoRelatorio = "data/relatorio_sorts.txt";

        try (FileWriter writer = new FileWriter(caminhoRelatorio)) {
            writer.write("RELATÓRIO DE COMPARAÇÃO DE ALGORITMOS DE ORDENAÇÃO\n");
            writer.write("==================================================\n\n");
            writer.write("Quantidade de registros: " + qtdRegistros + "\n");
            writer.write("Campo utilizado para ordenação: prioridade (1 = mais alta)\n\n");

            writer.write("TEMPOS (em milissegundos aproximados)\n");
            writer.write("-------------------------------------\n");
            writer.write(String.format("Merge Sort: %.3f ms%n", tempoMerge / 1_000_000.0));
            writer.write(String.format("Quick Sort: %.3f ms%n", tempoQuick / 1_000_000.0));
            writer.write(String.format("Heap Sort:  %.3f ms%n", tempoHeap / 1_000_000.0));
            writer.write("\n");

            writer.write("Ordenações idênticas entre si? " + (mesmaOrdenacao ? "SIM" : "NÃO") + "\n\n");

            writer.write("ANÁLISE TEÓRICA\n");
            writer.write("----------------\n");
            writer.write("- Merge Sort:\n");
            writer.write("  • Complexidade: O(n log n) no melhor, médio e pior caso.\n");
            writer.write("  • Estável (mantém a ordem relativa de elementos com a mesma prioridade).\n");
            writer.write("  • Usa memória extra proporcional ao tamanho do vetor (não é in-place).\n\n");

            writer.write("- Quick Sort:\n");
            writer.write("  • Complexidade média: O(n log n), mas pior caso O(n^2).\n");
            writer.write("  • Geralmente é o mais rápido na prática para dados em RAM,\n");
            writer.write("    devido a melhor localidade de cache e poucas operações extras.\n");
            writer.write("  • Não é estável na forma clássica.\n");
            writer.write("  • É in-place (usa pouca memória extra).\n\n");

            writer.write("- Heap Sort:\n");
            writer.write("  • Complexidade: O(n log n) em qualquer caso.\n");
            writer.write("  • In-place (não usa memória extra relevante).\n");
            writer.write("  • Normalmente é mais lento que QuickSort na prática, mesmo tendo\n");
            writer.write("    mesma ordem de complexidade, por causa de mais operações de troca\n");
            writer.write("    e acessos menos sequenciais à memória.\n\n");

            writer.write("COMPARAÇÃO PRÁTICA NESTE TESTE\n");
            writer.write("--------------------------------\n");

            // Descobrir quem foi o mais rápido
            long menorTempo = Math.min(tempoMerge, Math.min(tempoQuick, tempoHeap));
            String melhorAlgoritmo;

            if (menorTempo == tempoMerge) {
                melhorAlgoritmo = "Merge Sort";
            } else if (menorTempo == tempoQuick) {
                melhorAlgoritmo = "Quick Sort";
            } else {
                melhorAlgoritmo = "Heap Sort";
            }

            writer.write("Melhor tempo observado: " + melhorAlgoritmo + "\n\n");

            writer.write("POR QUE ESTE FOI O MELHOR AQUI?\n");
            writer.write("--------------------------------\n");

            if (melhorAlgoritmo.equals("Quick Sort")) {
                writer.write("- Quick Sort costuma ser muito eficiente para dados em memória,\n");
                writer.write("  pois tem boa localidade de referência e poucas cópias em comparação\n");
                writer.write("  com Merge Sort. Isso faz com que, na prática, muitas vezes ele\n");
                writer.write("  seja o mais rápido em entradas de tamanho médio/grande.\n");
            } else if (melhorAlgoritmo.equals("Merge Sort")) {
                writer.write("- Merge Sort pode vencer o Quick Sort dependendo da implementação,\n");
                writer.write("  do padrão dos dados e da forma como o Quick Sort foi codado.\n");
                writer.write("  Ele tem garantia de O(n log n) e é estável, o que também pode\n");
                writer.write("  ser uma vantagem quando há muitos elementos com a mesma prioridade.\n");
            } else {
                writer.write("- Heap Sort normalmente não é o mais rápido, mas pode se destacar\n");
                writer.write("  em cenários específicos ou por detalhes da máquina/VM.\n");
                writer.write("  Ele tem complexidade garantida O(n log n) e é in-place.\n");
            }

            writer.write("\nOBSERVAÇÕES IMPORTANTES\n");
            writer.write("------------------------\n");
            writer.write("- Esses resultados valem para ESTE tamanho de entrada (" + qtdRegistros + ") e\n");
            writer.write("  para ESTA máquina/execução.\n");
            writer.write("- Para uma análise mais confiável, seria importante rodar múltiplas vezes\n");
            writer.write("  e calcular a média dos tempos, além de testar tamanhos diferentes\n");
            writer.write("  (ex: 1.000, 5.000, 10.000, 50.000 registros).\n");
        }
    }
}
