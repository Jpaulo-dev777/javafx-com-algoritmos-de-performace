import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    // Classe para armazenar dados do CSV
    static class Registro {
        int id, valor;
        String categoria;
        long timestamp;

        Registro(int id, int valor, String categoria, long timestamp) {
            this.id = id;
            this.valor = valor;
            this.categoria = categoria;
            this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     BENCHMARK DE ALGORITMOS - 5000 REGISTROS CSV      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // 1. Ler CSV
        List<Registro> registros = lerCSV("data/dados.csv");

        if (registros.isEmpty()) {
            System.err.println("✗ Nenhum registro lido! Execute GerarCSV.java primeiro.");
            return;
        }

        // 2. Extrair valores para ordenar
        int[] valores = registros.stream().mapToInt(r -> r.valor).toArray();

        System.out.println("✓ " + valores.length + " valores carregados\n");
        System.out.println("Executando testes...\n");

        // 3. Testar cada algoritmo
        testarAlgoritmo("Bubble Sort", valores, Main::bubbleSort, valores.length <= 5000);
        testarAlgoritmo("Merge Sort", valores, Main::mergeSort, true);
        testarAlgoritmo("Quick Sort", valores, Main::quickSort, true);
        testarAlgoritmo("Heap Sort", valores, Main::heapSort, true);
        testarAlgoritmo("Java Arrays.sort (TimSort)", valores, Main::javaSort, true);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    CONCLUSÃO                           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("🏆 Para 5000 elementos, Quick Sort geralmente é o mais rápido!");
        System.out.println("📊 Mas Arrays.sort() do Java é otimizado e muito confiável.");
    }

    // ========== LEITURA CSV ==========
    static List<Registro> lerCSV(String caminho) {
        List<Registro> registros = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine(); // Pula cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length == 4) {
                    registros.add(new Registro(
                            Integer.parseInt(partes[0]),
                            Integer.parseInt(partes[1]),
                            partes[2],
                            Long.parseLong(partes[3])
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("✗ Erro ao ler CSV: " + e.getMessage());
        }

        return registros;
    }

    // ========== TESTE DE ALGORITMO ==========
    static void testarAlgoritmo(String nome, int[] original, SortFunction sortFunc, boolean executar) {
        if (!executar) {
            System.out.printf("%-30s PULADO (muito lento)\n", nome + ":");
            return;
        }

        int[] arr = Arrays.copyOf(original, original.length);

        long inicio = System.nanoTime();
        sortFunc.sort(arr);
        long fim = System.nanoTime();

        double tempoMs = (fim - inicio) / 1_000_000.0;
        boolean correto = verificarOrdenado(arr);

        String status = correto ? "✓" : "✗";
        System.out.printf("%-30s %8.2f ms  %s\n", nome + ":", tempoMs, status);
    }

    static boolean verificarOrdenado(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }

    // ========== ALGORITMOS ==========

    interface SortFunction {
        void sort(int[] arr);
    }

    // Bubble Sort
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    trocou = true;
                }
            }
            if (!trocou) break;
        }
    }

    // Merge Sort
    static void mergeSort(int[] arr) {
        mergeSortHelper(arr, 0, arr.length - 1);
    }

    static void mergeSortHelper(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(arr, left, mid);
            mergeSortHelper(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Quick Sort
    static void quickSort(int[] arr) {
        quickSortHelper(arr, 0, arr.length - 1);
    }

    static void quickSortHelper(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSortHelper(arr, low, pi - 1);
            quickSortHelper(arr, pi + 1, high);
        }
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    // Heap Sort
    static void heapSort(int[] arr) {
        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;

        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }

    // Java Sort
    static void javaSort(int[] arr) {
        Arrays.sort(arr);
    }
}
