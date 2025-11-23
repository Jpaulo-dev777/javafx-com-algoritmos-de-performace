import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualizadorGrafico extends JFrame {

    // Painel principal de visualização
    private PainelOrdenacao painelOrdenacao;
    private JLabel lblAlgoritmo, lblComparacoes, lblTrocas, lblTempo, lblStatus;
    private JProgressBar progressBar;
    private JButton btnQuickSort, btnMergeSort, btnHeapSort, btnBubbleSort, btnReset, btnPausar, btnRelatorio;
    private JSlider sliderVelocidade, sliderTamanho;
    private AtomicBoolean pausado = new AtomicBoolean(false);
    private AtomicBoolean executando = new AtomicBoolean(false);

    // Estrutura para o Relatório de Desempenho
    private List<ResultadoOrdenacao> historicoResultados = new ArrayList<>();

    // Classe para armazenar os resultados de cada execução
    static class ResultadoOrdenacao {
        String algoritmo;
        int elementos;
        int comparacoes;
        int trocas;
        double tempo;

        public ResultadoOrdenacao(String algo, int elem, int comp, int troc, double t) {
            this.algoritmo = algo;
            this.elementos = elem;
            this.comparacoes = comp;
            this.trocas = troc;
            this.tempo = t;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VisualizadorGrafico().setVisible(true);
        });
    }

    public VisualizadorGrafico() {
        configurarJanela();
        criarComponentes();
        layoutComponentes();
    }

    private void configurarJanela() {
        setTitle("Visualizador Profissional de Algoritmos de Ordenação");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(25, 25, 35));
    }

    private void criarComponentes() {
        painelOrdenacao = new PainelOrdenacao();

        // Labels de informação
        lblAlgoritmo = criarLabel("Algoritmo: Aguardando...", 20, Font.BOLD);
        lblComparacoes = criarLabel("Comparações: 0", 16, Font.PLAIN);
        lblTrocas = criarLabel("Trocas: 0", 16, Font.PLAIN);
        lblTempo = criarLabel("Tempo: 0.00s", 16, Font.PLAIN);
        lblStatus = criarLabel("● Pronto", 16, Font.PLAIN);
        lblStatus.setForeground(new Color(50, 255, 100));

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setBackground(new Color(40, 40, 50));
        progressBar.setForeground(new Color(100, 150, 255));

        // Botões de algoritmos
        btnQuickSort = criarBotao("Quick Sort", new Color(255, 100, 100));
        btnMergeSort = criarBotao("Merge Sort", new Color(100, 150, 255));
        btnHeapSort = criarBotao("Heap Sort", new Color(150, 100, 255));
        btnBubbleSort = criarBotao("Bubble Sort", new Color(255, 200, 100));
        btnReset = criarBotao("🔄 Reset", new Color(100, 255, 150));
        btnPausar = criarBotao("⏸ Pausar", new Color(255, 150, 50));
        btnRelatorio = criarBotao("📄 Relatório", new Color(50, 200, 255));

        // Sliders
        sliderVelocidade = new JSlider(1, 100, 50);
        sliderVelocidade.setBackground(new Color(35, 35, 45));
        sliderVelocidade.setForeground(Color.WHITE);
        sliderVelocidade.setPaintTicks(false);

        // ALTERAÇÃO CRÍTICA: Permite até 5000 elementos
        sliderTamanho = new JSlider(10, 5000, 100);
        sliderTamanho.setBackground(new Color(35, 35, 45));
        sliderTamanho.setForeground(Color.WHITE);
        sliderTamanho.setPaintTicks(false);

        // Ações dos botões
        btnQuickSort.addActionListener(e -> iniciarOrdenacao("Quick Sort"));
        btnMergeSort.addActionListener(e -> iniciarOrdenacao("Merge Sort"));
        btnHeapSort.addActionListener(e -> iniciarOrdenacao("Heap Sort"));
        btnBubbleSort.addActionListener(e -> iniciarOrdenacao("Bubble Sort"));
        btnReset.addActionListener(e -> resetar());
        btnPausar.addActionListener(e -> pausarRetomar());
        btnRelatorio.addActionListener(e -> exibirRelatorio());

        sliderTamanho.addChangeListener(e -> {
            if (!executando.get()) {
                painelOrdenacao.setTamanho(sliderTamanho.getValue());
                painelOrdenacao.resetArray();
            }
        });
    }

    private JLabel criarLabel(String texto, int tamanho, int estilo) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", estilo, tamanho));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(btn.isEnabled()) btn.setBackground(cor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if(btn.isEnabled()) btn.setBackground(cor);
            }
        });

        return btn;
    }

    private void layoutComponentes() {
        setLayout(new BorderLayout(0, 0));

        // Painel superior - Informações
        JPanel painelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelInfo.setBackground(new Color(35, 35, 45));
        painelInfo.add(lblAlgoritmo);
        painelInfo.add(lblComparacoes);
        painelInfo.add(lblTrocas);
        painelInfo.add(lblTempo);
        painelInfo.add(lblStatus);
        painelInfo.add(progressBar);

        // Painel central - Visualização
        JPanel painelCentralHolder = new JPanel(new BorderLayout());
        painelCentralHolder.setBackground(new Color(25, 25, 35));
        painelCentralHolder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelCentralHolder.add(painelOrdenacao, BorderLayout.CENTER);

        // Painel inferior - Controles
        JPanel painelControles = new JPanel(new BorderLayout());
        painelControles.setBackground(new Color(35, 35, 45));
        painelControles.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoes.setBackground(new Color(35, 35, 45));
        painelBotoes.add(btnQuickSort);
        painelBotoes.add(btnMergeSort);
        painelBotoes.add(btnHeapSort);
        painelBotoes.add(btnBubbleSort);
        painelBotoes.add(Box.createHorizontalStrut(20));
        painelBotoes.add(btnPausar);
        painelBotoes.add(btnReset);
        painelBotoes.add(btnRelatorio);

        JPanel painelSliders = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        painelSliders.setBackground(new Color(35, 35, 45));

        JLabel lblVel = criarLabel("Velocidade:", 14, Font.PLAIN);
        JLabel lblTam = criarLabel("Qtd Elementos:", 14, Font.PLAIN);

        painelSliders.add(lblVel);
        painelSliders.add(sliderVelocidade);
        painelSliders.add(lblTam);
        painelSliders.add(sliderTamanho);

        painelControles.add(painelBotoes, BorderLayout.NORTH);
        painelControles.add(painelSliders, BorderLayout.SOUTH);

        add(painelInfo, BorderLayout.NORTH);
        add(painelCentralHolder, BorderLayout.CENTER);
        add(painelControles, BorderLayout.SOUTH);
    }

    private void iniciarOrdenacao(String algoritmo) {
        if (executando.get()) {
            return;
        }

        executando.set(true);
        pausado.set(false);
        btnPausar.setText("⏸ Pausar");
        desabilitarBotoes(true);

        lblAlgoritmo.setText("Algoritmo: " + algoritmo);
        lblStatus.setText("● Executando...");
        lblStatus.setForeground(new Color(255, 200, 50));

        painelOrdenacao.resetarCores();
        painelOrdenacao.setAlgoritmo(algoritmo);
        painelOrdenacao.setVelocidade(101 - sliderVelocidade.getValue());

        new Thread(() -> {
            long inicio = System.currentTimeMillis();

            switch (algoritmo) {
                case "Quick Sort":
                    painelOrdenacao.quickSort(0, painelOrdenacao.array.length - 1);
                    break;
                case "Merge Sort":
                    painelOrdenacao.mergeSort(0, painelOrdenacao.array.length - 1);
                    break;
                case "Heap Sort":
                    painelOrdenacao.heapSort();
                    break;
                case "Bubble Sort":
                    painelOrdenacao.bubbleSort();
                    break;
            }

            painelOrdenacao.marcarTodosOrdenados();

            long fim = System.currentTimeMillis();
            double tempoSeg = (fim - inicio) / 1000.0;

            SwingUtilities.invokeLater(() -> {
                // REGISTRA O RESULTADO para o relatório
                ResultadoOrdenacao resultado = new ResultadoOrdenacao(
                        algoritmo,
                        painelOrdenacao.array.length,
                        painelOrdenacao.comparacoes,
                        painelOrdenacao.trocas,
                        tempoSeg
                );
                historicoResultados.add(resultado);

                lblTempo.setText(String.format("Tempo: %.2fs", tempoSeg));
                lblStatus.setText("● Concluído!");
                lblStatus.setForeground(new Color(50, 255, 100));
                progressBar.setValue(100);
                desabilitarBotoes(false);
                executando.set(false);
            });

        }).start();
    }

    private void resetar() {
        if (executando.get()) return;

        painelOrdenacao.resetArray();
        lblAlgoritmo.setText("Algoritmo: Aguardando...");
        lblComparacoes.setText("Comparações: 0");
        lblTrocas.setText("Trocas: 0");
        lblTempo.setText("Tempo: 0.00s");
        lblStatus.setText("● Pronto");
        lblStatus.setForeground(new Color(50, 255, 100));
        progressBar.setValue(0);
    }

    private void pausarRetomar() {
        if (!executando.get()) return;

        pausado.set(!pausado.get());
        if (pausado.get()) {
            btnPausar.setText("▶ Retomar");
            lblStatus.setText("● Pausado");
            lblStatus.setForeground(new Color(255, 150, 50));
        } else {
            btnPausar.setText("⏸ Pausar");
            lblStatus.setText("● Executando...");
            lblStatus.setForeground(new Color(255, 200, 50));
            synchronized (pausado) {
                pausado.notifyAll();
            }
        }
    }

    private void exibirRelatorio() {
        if (historicoResultados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma ordenação concluída. Execute um algoritmo primeiro.",
                    "Relatório Vazio",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("<html><div style='width: 600px; font-family: Segoe UI;'>");
        relatorio.append("<h2>📋 Histórico de Desempenho</h2>");

        relatorio.append("<table border='1' cellpadding='5' cellspacing='0' width='100%' style='border-collapse: collapse; font-size: 11pt;'>");
        relatorio.append("<tr style='background-color: #505070; color: white;'>");
        relatorio.append("<th>Algoritmo</th><th>Elementos</th><th>Comparações</th><th>Trocas</th><th>Tempo (s)</th>");
        relatorio.append("</tr>");

        for (ResultadoOrdenacao r : historicoResultados) {
            // Garante que o número de elementos seja o correto no relatório (linha a linha)
            relatorio.append("<tr style='background-color: #353545; color: white;'>");
            relatorio.append("<td>").append(r.algoritmo).append("</td>");
            relatorio.append("<td style='text-align: right;'>").append(String.format("%,d", r.elementos)).append("</td>");
            relatorio.append("<td style='text-align: right;'>").append(String.format("%,d", r.comparacoes)).append("</td>");
            relatorio.append("<td style='text-align: right;'>").append(String.format("%,d", r.trocas)).append("</td>");
            relatorio.append("<td style='text-align: right;'>").append(String.format("%.4f", r.tempo)).append("</td>");
            relatorio.append("</tr>");
        }

        relatorio.append("</table>");
        relatorio.append("</div></html>");

        JOptionPane.showMessageDialog(this, new JLabel(relatorio.toString()),
                "Relatório de Desempenho", JOptionPane.PLAIN_MESSAGE);
    }

    private void desabilitarBotoes(boolean desabilitar) {
        btnQuickSort.setEnabled(!desabilitar);
        btnMergeSort.setEnabled(!desabilitar);
        btnHeapSort.setEnabled(!desabilitar);
        btnBubbleSort.setEnabled(!desabilitar);
        btnReset.setEnabled(!desabilitar);
        sliderTamanho.setEnabled(!desabilitar);
    }

    // ========== PAINEL DE ORDENAÇÃO ==========
    class PainelOrdenacao extends JPanel {
        int[] array;
        int[] colors; // 0=normal, 1=comparando, 2=trocando, 3=ordenado, 4=pivot
        private int tamanho = 100;
        private int velocidade = 50;
        private String algoritmo = "";
        private int comparacoes = 0;
        private int trocas = 0;

        public PainelOrdenacao() {
            setBackground(new Color(20, 20, 30));
            setTamanho(tamanho);
        }

        public void setTamanho(int tamanho) {
            this.tamanho = tamanho;
            resetArray();
        }

        public void setVelocidade(int velocidade) {
            this.velocidade = velocidade;
        }

        public void setAlgoritmo(String algoritmo) {
            this.algoritmo = algoritmo;
            this.comparacoes = 0;
            this.trocas = 0;
        }

        public void resetArray() {
            array = new int[tamanho];
            colors = new int[tamanho];
            Random rand = new Random();
            int maxH = (getHeight() > 0) ? getHeight() : 400;

            for (int i = 0; i < tamanho; i++) {
                array[i] = rand.nextInt(Math.max(50, maxH - 50)) + 10;
                colors[i] = 0;
            }
            comparacoes = 0;
            trocas = 0;
            repaint();
        }

        public void resetarCores() {
            if (colors == null) return;
            for (int i = 0; i < colors.length; i++) {
                colors[i] = 0;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            double barWidth = (double) largura / tamanho;

            // Configurações da Fonte
            Font font = new Font("Arial", Font.BOLD, 12);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics(font);

            for (int i = 0; i < tamanho; i++) {
                Color cor;
                switch (colors[i]) {
                    case 1: cor = new Color(255, 200, 0); break;    // Comparando (amarelo)
                    case 2: cor = new Color(255, 50, 50); break;    // Trocando (vermelho)
                    case 3: cor = new Color(50, 255, 100); break;   // Ordenado (verde)
                    case 4: cor = new Color(255, 100, 255); break;  // Pivot (roxo)
                    default:
                        // Gradiente azulado baseado na altura
                        float ratio = Math.min(1.0f, (float) array[i] / altura);
                        cor = new Color(
                                (int)(50 + ratio * 50),
                                (int)(100 + ratio * 100),
                                255
                        );
                }

                int x = (int) (i * barWidth);
                int w = (int)Math.ceil(barWidth);
                int h = array[i];
                if (h > altura) h = altura - 5;
                int y = altura - h;

                // 1. Desenha a Barra
                g2d.setColor(cor);
                g2d.fillRect(x, y, w, h);

                // 2. Desenha o Valor Numérico (Até 50 elementos)
                if (tamanho <= 50) {
                    String valor = String.valueOf(array[i]);
                    int stringWidth = fm.stringWidth(valor);

                    if (w > stringWidth + 2) {
                        g2d.setColor(h > (altura / 4) ? Color.BLACK : Color.WHITE);
                        int textX = x + (w / 2) - (stringWidth / 2);

                        int textY;
                        if (h > fm.getAscent() + 5) {
                            textY = y + fm.getAscent() + 2;
                        } else {
                            g2d.setColor(Color.WHITE);
                            textY = y - 5;
                        }

                        g2d.drawString(valor, textX, textY);
                    }
                }
            }
        }

        private void aguardarPausa() {
            while (pausado.get()) {
                try {
                    synchronized (pausado) {
                        pausado.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void sleep() {
            aguardarPausa();
            try {
                Thread.sleep(velocidade);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void atualizarInfo() {
            SwingUtilities.invokeLater(() -> {
                lblComparacoes.setText("Comparações: " + String.format("%,d", comparacoes));
                lblTrocas.setText("Trocas: " + String.format("%,d", trocas));

                // 2. CORREÇÃO DA FÓRMULA DE PROGRESSO PARA GRANDES N
                double totalEstimado;

                if (algoritmo.equals("Bubble Sort")) {
                    // O(N^2)
                    totalEstimado = (double)tamanho * tamanho;
                } else {
                    // O(N log N) para Quick, Merge e Heap Sort
                    totalEstimado = tamanho * (Math.log(tamanho) / Math.log(2.0)); // Log base 2
                }

                // Normaliza o progresso, evitando divisão por zero e números muito altos
                double ratio = (totalEstimado > 0) ? ((double)comparacoes / totalEstimado) : 0;

                int progresso = (int) (ratio * 100);
                progressBar.setValue(Math.min(Math.max(progresso, 0), 99));
            });
        }

        private void marcarComparacao(int i, int j) {
            if (i >= 0 && i < colors.length) colors[i] = 1;
            if (j >= 0 && j < colors.length) colors[j] = 1;
            comparacoes++;
            atualizarInfo();
            repaint();
            sleep();
            if (i >= 0 && i < colors.length) colors[i] = 0;
            if (j >= 0 && j < colors.length) colors[j] = 0;
        }

        private void marcarTroca(int i, int j) {
            colors[i] = 2;
            colors[j] = 2;
            trocas++;
            atualizarInfo();
            repaint();
            sleep();

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;

            colors[i] = 0;
            colors[j] = 0;
            repaint();
        }

        private void marcarOrdenado(int i) {
            colors[i] = 3;
            repaint();
        }

        public void marcarTodosOrdenados() {
            for (int i = 0; i < tamanho; i++) {
                colors[i] = 3;
                repaint();
                try {
                    Thread.sleep(Math.max(1, 1000/tamanho));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        // ========== BUBBLE SORT ==========
        public void bubbleSort() {
            int n = array.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    marcarComparacao(j, j + 1);
                    if (array[j] > array[j + 1]) {
                        marcarTroca(j, j + 1);
                    }
                }
                marcarOrdenado(n - i - 1);
            }
            marcarOrdenado(0);
        }

        // ========== QUICK SORT ==========
        public void quickSort(int low, int high) {
            if (low < high) {
                int pi = partition(low, high);
                quickSort(low, pi - 1);
                quickSort(pi + 1, high);
            } else if (low == high) {
            }
        }

        private int partition(int low, int high) {
            colors[high] = 4; // Pivot
            int pivot = array[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                marcarComparacao(j, high);
                if (array[j] < pivot) {
                    i++;
                    marcarTroca(i, j);
                }
            }
            marcarTroca(i + 1, high);
            colors[high] = 0;
            return i + 1;
        }

        // ========== MERGE SORT ==========
        public void mergeSort(int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;
                mergeSort(left, mid);
                mergeSort(mid + 1, right);
                merge(left, mid, right);
            }
        }

        private void merge(int left, int mid, int right) {
            int n1 = mid - left + 1;
            int n2 = right - mid;
            int[] L = new int[n1];
            int[] R = new int[n2];

            System.arraycopy(array, left, L, 0, n1);
            System.arraycopy(array, mid + 1, R, 0, n2);

            int i = 0, j = 0, k = left;

            while (i < n1 && j < n2) {
                marcarComparacao(k, -1);
                colors[k] = 2;
                repaint();
                sleep();

                if (L[i] <= R[j]) {
                    array[k] = L[i++];
                } else {
                    array[k] = R[j++];
                }
                trocas++;
                colors[k] = 0;
                k++;
            }

            while (i < n1) {
                colors[k] = 2;
                repaint();
                sleep();
                array[k] = L[i++];
                trocas++;
                colors[k] = 0;
                k++;
            }

            while (j < n2) {
                colors[k] = 2;
                repaint();
                sleep();
                array[k] = R[j++];
                trocas++;
                colors[k] = 0;
                k++;
            }
            atualizarInfo();
        }

        // ========== HEAP SORT ==========
        public void heapSort() {
            int n = array.length;

            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i);
            }

            for (int i = n - 1; i > 0; i--) {
                marcarTroca(0, i);
                marcarOrdenado(i);
                heapify(i, 0);
            }
            marcarOrdenado(0);
        }

        private void heapify(int n, int i) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n) {
                marcarComparacao(left, largest);
                if (array[left] > array[largest]) {
                    largest = left;
                }
            }

            if (right < n) {
                marcarComparacao(right, largest);
                if (array[right] > array[largest]) {
                    largest = right;
                }
            }

            if (largest != i) {
                marcarTroca(i, largest);
                heapify(n, largest);
            }
        }
    }
}