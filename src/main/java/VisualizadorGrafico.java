import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualizadorGrafico extends JFrame {

    // ====== TEMA (DARK PROFISSIONAL) ======
    private static final Color BACKGROUND_MAIN    = new Color(18, 18, 26);
    private static final Color BACKGROUND_CONTROL = new Color(24, 24, 34);
    private static final Color BACKGROUND_METRIC  = new Color(34, 34, 48);
    private static final Color TEXT_LIGHT         = new Color(235, 235, 245);
    private static final Color TEXT_MUTED         = new Color(160, 160, 190);
    private static final Color ACCENT_COLOR       = new Color(88, 150, 255);

    // Botões / estados
    private static final Color COLOR_QUICK   = new Color(239, 83, 80);
    private static final Color COLOR_MERGE   = new Color(66, 165, 245);
    private static final Color COLOR_HEAP    = new Color(171, 71, 188);
    private static final Color COLOR_BUBBLE  = new Color(255, 193, 7);
    private static final Color COLOR_SUCCESS = new Color(76, 175, 80);
    private static final Color COLOR_WARN    = new Color(255, 160, 0);

    // ====== COMPONENTES PRINCIPAIS ======
    private PainelOrdenacao painelOrdenacao;

    private JLabel lblAlgoritmo;
    private JLabel lblComparacoes;
    private JLabel lblTrocas;
    private JLabel lblTempo;
    private JLabel lblStatus;

    private JProgressBar progressBar;

    private JButton btnQuickSort;
    private JButton btnMergeSort;
    private JButton btnHeapSort;
    private JButton btnBubbleSort;
    private JButton btnReset;
    private JButton btnPausar;
    private JButton btnRelatorio;

    private JSlider sliderVelocidade;
    private JSlider sliderTamanho;

    private AtomicBoolean pausado    = new AtomicBoolean(false);
    private AtomicBoolean executando = new AtomicBoolean(false);

    // ====== HISTÓRICO PARA RELATÓRIO ======
    private List<ResultadoOrdenacao> historicoResultados = new ArrayList<>();

    static class ResultadoOrdenacao {
        String algoritmo;
        int elementos;
        int comparacoes;
        int trocas;
        double tempo;

        public ResultadoOrdenacao(String algo, int elem, int comp, int troc, double t) {
            this.algoritmo   = algo;
            this.elementos   = elem;
            this.comparacoes = comp;
            this.trocas      = troc;
            this.tempo       = t;
        }
    }

    // ======================= MAIN =======================

    public static void main(String[] args) {
        try {
            // Nimbus fica bonito em dark
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new VisualizadorGrafico().setVisible(true));
    }

    public VisualizadorGrafico() {
        configurarJanela();
        criarComponentes();
        layoutComponentes();
    }

    // ======================= CONFIG JANELA =======================

    private void configurarJanela() {
        setTitle("Visualizador de Algoritmos de Ordenação | Análise de Desempenho");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_MAIN);
    }

    // ======================= CRIA COMPONENTES =======================

    private void criarComponentes() {
        painelOrdenacao = new PainelOrdenacao();

        // Labels topo
        lblAlgoritmo    = criarLabel("Algoritmo: Nenhum em execução", 18, Font.BOLD, TEXT_LIGHT);
        lblComparacoes  = criarLabel("0", 18, Font.BOLD, ACCENT_COLOR);
        lblTrocas       = criarLabel("0", 18, Font.BOLD, ACCENT_COLOR);
        lblTempo        = criarLabel("0,00 s", 18, Font.BOLD, ACCENT_COLOR);
        lblStatus       = criarLabel("Pronto", 14, Font.BOLD, Color.WHITE);
        lblStatus.setOpaque(true);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBorder(new EmptyBorder(4, 12, 4, 12));
        atualizarStatus("Pronto", COLOR_SUCCESS.darker(), new Color(230, 255, 240));

        // Barra de progresso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(220, 24));
        progressBar.setBackground(BACKGROUND_METRIC.darker());
        progressBar.setForeground(new Color(38, 198, 218));
        progressBar.setBorder(new LineBorder(BACKGROUND_METRIC, 1));

        // Botões
        btnQuickSort  = criarBotao("⚡ Quick Sort",   COLOR_QUICK,  Color.WHITE);
        btnMergeSort  = criarBotao("🧬 Merge Sort",   COLOR_MERGE,  Color.WHITE);
        btnHeapSort   = criarBotao("🌲 Heap Sort",    COLOR_HEAP,   Color.WHITE);
        btnBubbleSort = criarBotao("🛁 Bubble Sort",  COLOR_BUBBLE, Color.BLACK);
        btnReset      = criarBotao("🔄 Reset",        COLOR_SUCCESS,Color.WHITE);
        btnPausar     = criarBotao("⏸ Pausar",       COLOR_WARN,   Color.WHITE);
        btnRelatorio  = criarBotao("📊 Relatório",    new Color(38, 198, 218), Color.WHITE);

        // Sliders
        sliderVelocidade = new JSlider(1, 100, 50);
        estilizarSlider(sliderVelocidade);
        sliderVelocidade.setToolTipText("Pausa entre operações (1=rápido, 100=lento)");
        sliderVelocidade.setMajorTickSpacing(25);
        sliderVelocidade.setPaintLabels(true);

        sliderTamanho = new JSlider(10, 5000, 100);
        estilizarSlider(sliderTamanho);
        sliderTamanho.setToolTipText("Quantidade de elementos (10 a 5000)");
        sliderTamanho.setMajorTickSpacing(1000);
        sliderTamanho.setPaintLabels(true);

        // Ações
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

    // ======================= HELPERS UI =======================

    private JLabel criarLabel(String texto, int tamanho, int estilo, Color fg) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", estilo, tamanho));
        label.setForeground(fg);
        return label;
    }

    private JButton criarBotao(String texto, Color corBase, Color corTexto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(corBase);
        btn.setForeground(corTexto);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        Color hover = corBase.brighter().brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(corBase);
            }
        });
        return btn;
    }

    private void estilizarSlider(JSlider slider) {
        slider.setBackground(BACKGROUND_CONTROL);
        slider.setForeground(TEXT_MUTED);
        slider.setPaintTicks(true);
        slider.setPreferredSize(new Dimension(250, 45));
    }

    private JPanel criarCardMetrica(String titulo, JComponent componente) {
        JLabel lblTitulo = criarLabel(titulo.toUpperCase(), 11, Font.BOLD, TEXT_MUTED);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BACKGROUND_METRIC);
        p.setBorder(new EmptyBorder(10, 15, 10, 15));

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lblTitulo);
        p.add(Box.createVerticalStrut(4));
        p.add(componente);
        return p;
    }

    private void atualizarStatus(String texto, Color bg, Color fg) {
        lblStatus.setText(texto);
        lblStatus.setBackground(bg);
        lblStatus.setForeground(fg);
    }

    // ======================= LAYOUT =======================

    private void layoutComponentes() {
        setLayout(new BorderLayout());

        // HEADER (NORTE SUPERIOR)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_CONTROL);
        header.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel titulo = criarLabel("Visualizador de Algoritmos de Ordenação", 22, Font.BOLD, TEXT_LIGHT);
        JLabel subTitulo = criarLabel("Análise comparativa em tempo real", 14, Font.PLAIN, TEXT_MUTED);

        JPanel boxTitulo = new JPanel();
        boxTitulo.setLayout(new BoxLayout(boxTitulo, BoxLayout.Y_AXIS));
        boxTitulo.setOpaque(false);
        boxTitulo.add(titulo);
        boxTitulo.add(Box.createVerticalStrut(2));
        boxTitulo.add(subTitulo);

        JPanel painelStatusTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        painelStatusTopo.setOpaque(false);
        painelStatusTopo.add(lblAlgoritmo);
        painelStatusTopo.add(lblStatus);

        header.add(boxTitulo, BorderLayout.WEST);
        header.add(painelStatusTopo, BorderLayout.EAST);

        // MÉTRICAS
        JPanel painelMetricas = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        painelMetricas.setBackground(BACKGROUND_CONTROL.darker());
        painelMetricas.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelMetricas.add(criarCardMetrica("Comparações", lblComparacoes));
        painelMetricas.add(criarCardMetrica("Trocas", lblTrocas));
        painelMetricas.add(criarCardMetrica("Tempo total (s)", lblTempo));
        painelMetricas.add(criarCardMetrica("Progresso (N)", progressBar));

        JPanel painelNorte = new JPanel(new BorderLayout());
        painelNorte.add(header, BorderLayout.NORTH);
        painelNorte.add(painelMetricas, BorderLayout.SOUTH);

        // CENTRAL (visualização)
        JPanel painelCentralHolder = new JPanel(new BorderLayout());
        painelCentralHolder.setBackground(BACKGROUND_MAIN);
        painelCentralHolder.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painelOrdenacao.setBorder(new LineBorder(BACKGROUND_CONTROL, 1));
        painelCentralHolder.add(painelOrdenacao, BorderLayout.CENTER);

        // CONTROLES (SUL)
        JPanel painelControles = new JPanel(new BorderLayout());
        painelControles.setBackground(BACKGROUND_CONTROL);
        painelControles.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        painelBotoes.setBackground(BACKGROUND_CONTROL);
        painelBotoes.add(btnQuickSort);
        painelBotoes.add(btnMergeSort);
        painelBotoes.add(btnHeapSort);
        painelBotoes.add(btnBubbleSort);
        painelBotoes.add(Box.createHorizontalStrut(30));
        painelBotoes.add(btnPausar);
        painelBotoes.add(btnReset);
        painelBotoes.add(btnRelatorio);

        JPanel painelSliders = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        painelSliders.setBackground(BACKGROUND_CONTROL);
        painelSliders.add(criarLabel("VELOCIDADE:", 13, Font.BOLD, TEXT_MUTED));
        painelSliders.add(sliderVelocidade);
        painelSliders.add(Box.createHorizontalStrut(20));
        painelSliders.add(criarLabel("ELEMENTOS:", 13, Font.BOLD, TEXT_MUTED));
        painelSliders.add(sliderTamanho);

        painelControles.add(painelBotoes, BorderLayout.NORTH);
        painelControles.add(painelSliders, BorderLayout.SOUTH);

        // ADD AO FRAME
        add(painelNorte, BorderLayout.NORTH);
        add(painelCentralHolder, BorderLayout.CENTER);
        add(painelControles, BorderLayout.SOUTH);
    }

    // ======================= CONTROLE EXECUÇÃO =======================

    private void iniciarOrdenacao(String algoritmo) {
        if (executando.get()) return;

        executando.set(true);
        pausado.set(false);
        btnPausar.setText("⏸ Pausar");
        desabilitarBotoes(true);

        lblAlgoritmo.setText("Algoritmo: " + algoritmo);
        atualizarStatus("Executando...", COLOR_BUBBLE, Color.BLACK);
        painelOrdenacao.resetarCores();
        painelOrdenacao.setAlgoritmo(algoritmo);
        painelOrdenacao.setVelocidade(101 - sliderVelocidade.getValue());
        progressBar.setValue(0);
        lblTempo.setText("0,00 s");

        new Thread(() -> {
            long inicio = System.currentTimeMillis();
            try {
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
            } catch (Exception ignored) {}

            painelOrdenacao.marcarTodosOrdenados();
            long fim = System.currentTimeMillis();
            double tempoSeg = (fim - inicio) / 1000.0;

            SwingUtilities.invokeLater(() -> {
                ResultadoOrdenacao resultado = new ResultadoOrdenacao(
                        algoritmo,
                        painelOrdenacao.array.length,
                        painelOrdenacao.comparacoes,
                        painelOrdenacao.trocas,
                        tempoSeg
                );
                historicoResultados.add(resultado);

                lblTempo.setText(String.format("%.2f s", tempoSeg));
                atualizarStatus("Concluído", COLOR_SUCCESS, Color.WHITE);
                progressBar.setValue(100);
                desabilitarBotoes(false);
                executando.set(false);
            });
        }).start();
    }

    private void resetar() {
        if (executando.get()) {
            executando.set(false);
            pausado.set(false);
            synchronized (pausado) {
                pausado.notifyAll();
            }
        }
        painelOrdenacao.resetArray();
        lblAlgoritmo.setText("Algoritmo: Nenhum em execução");
        lblComparacoes.setText("0");
        lblTrocas.setText("0");
        lblTempo.setText("0,00 s");
        atualizarStatus("Pronto", COLOR_SUCCESS.darker(), new Color(230, 255, 240));
        progressBar.setValue(0);
        btnPausar.setText("⏸ Pausar");
        desabilitarBotoes(false);
    }

    private void pausarRetomar() {
        if (!executando.get()) return;

        pausado.set(!pausado.get());
        if (pausado.get()) {
            btnPausar.setText("▶ Retomar");
            atualizarStatus("Pausado", COLOR_WARN.darker(), Color.WHITE);
        } else {
            btnPausar.setText("⏸ Pausar");
            atualizarStatus("Executando...", COLOR_BUBBLE, Color.BLACK);
            synchronized (pausado) {
                pausado.notifyAll();
            }
        }
    }

    private void exibirRelatorio() {
        if (historicoResultados.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhuma ordenação concluída. Execute um algoritmo primeiro.",
                    "Relatório Vazio",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("<html><div style='width: 700px; font-family: Segoe UI, sans-serif; ")
                .append("background-color: #252838; color: #f5f5ff; padding: 10px; border-radius: 8px;'>");
        relatorio.append("<h3 style='color: #66A5FF; margin-top: 0; margin-bottom: 10px;'>Histórico de Desempenho</h3>");
        relatorio.append("<table border='0' cellpadding='8' cellspacing='0' width='100%' ")
                .append("style='border-collapse: collapse; font-size: 11pt;'>");
        relatorio.append("<tr style='background-color: #394264; color: white;'>");
        relatorio.append("<th align='left'>Algoritmo</th>")
                .append("<th align='right'>Elementos</th>")
                .append("<th align='right'>Comparações</th>")
                .append("<th align='right'>Trocas</th>")
                .append("<th align='right'>Tempo (s)</th>");
        relatorio.append("</tr>");

        boolean zebra = false;
        for (ResultadoOrdenacao r : historicoResultados) {
            String bg = zebra ? "#2c2f3f" : "#252838";
            zebra = !zebra;
            relatorio.append("<tr style='background-color: ").append(bg).append("; color: #f5f5ff;'>");
            relatorio.append("<td>").append(r.algoritmo).append("</td>");
            relatorio.append("<td align='right'>").append(String.format("%,d", r.elementos)).append("</td>");
            relatorio.append("<td align='right'>").append(String.format("%,d", r.comparacoes)).append("</td>");
            relatorio.append("<td align='right'>").append(String.format("%,d", r.trocas)).append("</td>");
            relatorio.append("<td align='right' style='color: #76FF03;'>")
                    .append(String.format("%.4f", r.tempo)).append("</td>");
            relatorio.append("</tr>");
        }
        relatorio.append("</table></div></html>");

        JLabel conteudo = new JLabel(relatorio.toString());
        conteudo.setBorder(new EmptyBorder(10, 10, 10, 10));
        JOptionPane pane = new JOptionPane(conteudo, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Relatório de Desempenho");
        dialog.getContentPane().setBackground(new Color(37, 40, 56));
        dialog.setVisible(true);
    }

    private void desabilitarBotoes(boolean desabilitar) {
        btnQuickSort.setEnabled(!desabilitar);
        btnMergeSort.setEnabled(!desabilitar);
        btnHeapSort.setEnabled(!desabilitar);
        btnBubbleSort.setEnabled(!desabilitar);
        // Reset fica sempre ativo para permitir abortar
        btnReset.setEnabled(true);
        btnRelatorio.setEnabled(!desabilitar);
        sliderTamanho.setEnabled(!desabilitar);
    }

    // ====================== PAINEL DE ORDENAÇÃO ======================

    class PainelOrdenacao extends JPanel {
        int[] array;
        int[] colors; // 0=normal, 1=comparando, 2=trocando, 3=ordenado, 4=pivot

        private int tamanho     = 100;
        private int velocidade  = 50;
        private String algoritmo = "";
        int comparacoes = 0;
        int trocas      = 0;

        public PainelOrdenacao() {
            setBackground(new Color(15, 15, 23));
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
            this.trocas      = 0;
        }

        public void resetArray() {
            array  = new int[tamanho];
            colors = new int[tamanho];
            Random rand = new Random();
            int maxH = (getHeight() > 0) ? getHeight() : 400;

            for (int i = 0; i < tamanho; i++) {
                array[i]  = rand.nextInt(Math.max(50, maxH - 50)) + 10;
                colors[i] = 0;
            }
            comparacoes = 0;
            trocas      = 0;
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
            int altura  = getHeight();
            double barWidth = (double) largura / tamanho;

            Font font = new Font("Segoe UI", Font.BOLD, 11);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics(font);

            for (int i = 0; i < tamanho; i++) {
                Color cor;
                switch (colors[i]) {
                    case 1: cor = new Color(255, 214, 0);  break; // comparando
                    case 2: cor = new Color(244, 67, 54);  break; // trocando
                    case 3: cor = new Color(76, 175, 80);  break; // ordenado
                    case 4: cor = new Color(186, 104, 200);break; // pivot
                    default:
                        float ratio = Math.min(1.0f, (float) array[i] / altura);
                        cor = new Color(
                                (int) (40 + ratio * 40),
                                (int) (110 + ratio * 80),
                                (int) (210 + ratio * 45)
                        );
                }

                int x = (int) (i * barWidth);
                int w = (int) Math.ceil(barWidth);
                int h = array[i];
                if (h > altura) h = altura - 5;
                int y = altura - h;

                g2d.setColor(cor);
                g2d.fillRoundRect(x, y, w, h, 4, 4);

                if (tamanho <= 40) {
                    String valor = String.valueOf(array[i]);
                    int stringWidth = fm.stringWidth(valor);
                    if (w > stringWidth + 2) {
                        Color textColor = (h > (altura / 4)) ? Color.BLACK : Color.WHITE;
                        g2d.setColor(textColor);
                        int textX = x + (w / 2) - (stringWidth / 2);
                        int textY;
                        if (h > fm.getAscent() + 6) {
                            textY = y + fm.getAscent() + 2;
                        } else {
                            textY = y - 4;
                            g2d.setColor(Color.WHITE);
                        }
                        g2d.drawString(valor, textX, textY);
                    }
                }
            }
        }

        // ===== Controle de pausa/velocidade =====
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
                lblComparacoes.setText(String.format("%,d", comparacoes));
                lblTrocas.setText(String.format("%,d", trocas));

                double totalEstimado;
                if ("Bubble Sort".equals(algoritmo)) {
                    totalEstimado = (double) tamanho * tamanho; // O(n^2)
                } else {
                    totalEstimado = tamanho * (Math.log(tamanho) / Math.log(2.0)); // O(n log n)
                }

                double ratio = (totalEstimado > 0) ? ((double) comparacoes / totalEstimado) : 0;
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
                    Thread.sleep(Math.max(1, 800 / tamanho));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // ====== Bubble Sort ======
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

        // ====== Quick Sort + partition (AQUI ESTAVA O ERRO) ======
        public void quickSort(int low, int high) {
            if (low < high) {
                int pi = partition(low, high);
                quickSort(low, pi - 1);
                quickSort(pi + 1, high);
            }
        }

        private int partition(int low, int high) {
            colors[high] = 4; // marca pivot em roxo
            int pivot = array[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {  // <- aqui precisa ser "int j = low"
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

        // ====== Merge Sort ======
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

            System.arraycopy(array, left,  L, 0, n1);
            System.arraycopy(array, mid+1, R, 0, n2);

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

        // ====== Heap Sort ======
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
            int left  = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n) {
                marcarComparacao(left, largest);
                if (array[left] > array[largest]) largest = left;
            }
            if (right < n) {
                marcarComparacao(right, largest);
                if (array[right] > array[largest]) largest = right;
            }

            if (largest != i) {
                marcarTroca(i, largest);
                heapify(n, largest);
            }
        }
    }

}
