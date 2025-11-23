public class AlgoritmosOrdenacao {

    // ---------- MERGE SORT ----------
    public static void mergeSort(Registro[] arr) {
        if (arr.length <= 1) return;
        Registro[] aux = new Registro[arr.length];
        mergeSortRec(arr, aux, 0, arr.length - 1);
    }

    private static void mergeSortRec(Registro[] arr, Registro[] aux, int inicio, int fim) {
        if (inicio >= fim) return;
        int meio = (inicio + fim) / 2;
        mergeSortRec(arr, aux, inicio, meio);
        mergeSortRec(arr, aux, meio + 1, fim);
        intercalar(arr, aux, inicio, meio, fim);
    }

    private static void intercalar(Registro[] arr, Registro[] aux, int inicio, int meio, int fim) {
        for (int i = inicio; i <= fim; i++) {
            aux[i] = arr[i];
        }

        int i = inicio;
        int j = meio + 1;
        int k = inicio;

        while (i <= meio && j <= fim) {
            if (Registro.compararPorPrioridade(aux[i], aux[j]) <= 0) {
                arr[k++] = aux[i++];
            } else {
                arr[k++] = aux[j++];
            }
        }

        while (i <= meio) {
            arr[k++] = aux[i++];
        }

        // Se j até fim já estiverem no lugar, não precisa copiar
    }

    // ---------- QUICK SORT ----------
    public static void quickSort(Registro[] arr) {
        quickSortRec(arr, 0, arr.length - 1);
    }

    private static void quickSortRec(Registro[] arr, int inicio, int fim) {
        if (inicio < fim) {
            int p = particiona(arr, inicio, fim);
            quickSortRec(arr, inicio, p - 1);
            quickSortRec(arr, p + 1, fim);
        }
    }

    private static int particiona(Registro[] arr, int inicio, int fim) {
        Registro pivot = arr[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (Registro.compararPorPrioridade(arr[j], pivot) <= 0) {
                i++;
                trocar(arr, i, j);
            }
        }
        trocar(arr, i + 1, fim);
        return i + 1;
    }

    // ---------- HEAP SORT ----------
    public static void heapSort(Registro[] arr) {
        int n = arr.length;

        // Constroi o heap (max-heap, mas vamos inverter a comparação)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Extrai um por um do heap
        for (int i = n - 1; i >= 0; i--) {
            trocar(arr, 0, i);
            heapify(arr, i, 0);
        }
    }

    private static void heapify(Registro[] arr, int n, int i) {
        int maior = i;
        int esq = 2 * i + 1;
        int dir = 2 * i + 2;

        // Para ordenar em ordem CRESCENTE de prioridade,
        // o heap deve ser max-heap baseado na prioridade:
        if (esq < n && Registro.compararPorPrioridade(arr[esq], arr[maior]) > 0) {
            maior = esq;
        }
        if (dir < n && Registro.compararPorPrioridade(arr[dir], arr[maior]) > 0) {
            maior = dir;
        }

        if (maior != i) {
            trocar(arr, i, maior);
            heapify(arr, n, maior);
        }
    }

    private static void trocar(Registro[] arr, int i, int j) {
        Registro temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
