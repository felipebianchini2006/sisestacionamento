import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaRelatorio extends JFrame {

    private Estacionamento estacionamento;
    private JTextArea areaRelatorioGeral;
    private JTable tabelaHistorico;
    private DefaultTableModel modeloHistorico;
    private JPanel painelGrafico;
    private JTextArea areaEstatisticasAvancadas;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaRelatorio(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;

        setTitle("Relatórios e Estatísticas");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Aba 1: Relatório Geral
        areaRelatorioGeral = new JTextArea();
        areaRelatorioGeral.setEditable(false);
        areaRelatorioGeral.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaRelatorioGeral.setMargin(new Insets(10, 10, 10, 10));
        tabbedPane.addTab("Relatório Geral", new JScrollPane(areaRelatorioGeral));

        // Aba 2: Gráfico de Ocupação
        painelGrafico = new PainelGraficoOcupacao();
        tabbedPane.addTab("Gráfico de Ocupação", painelGrafico);

        // Aba 3: Histórico de Tickets
        String[] colunas = {"ID", "Placa", "Entrada", "Saída", "Valor Pago", "Status"};
        modeloHistorico = new DefaultTableModel(colunas, 0);
        tabelaHistorico = new JTable(modeloHistorico);
        tabelaHistorico.setRowHeight(25);
        tabbedPane.addTab("Histórico de Tickets", new JScrollPane(tabelaHistorico));

        // Aba 4: Estatísticas Avançadas
        areaEstatisticasAvancadas = new JTextArea();
        areaEstatisticasAvancadas.setEditable(false);
        areaEstatisticasAvancadas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaEstatisticasAvancadas.setMargin(new Insets(10, 10, 10, 10));
        tabbedPane.addTab("Estatísticas Avançadas", new JScrollPane(areaEstatisticasAvancadas));

        add(tabbedPane, BorderLayout.CENTER);

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportarPDF = new JButton("Exportar PDF");
        JButton btnExportarExcel = new JButton("Exportar Excel");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnAtualizar = new JButton("Atualizar");

        btnExportarPDF.addActionListener(e -> mostrarMensagem("Exportar PDF"));
        btnExportarExcel.addActionListener(e -> mostrarMensagem("Exportar Excel"));
        btnImprimir.addActionListener(e -> mostrarMensagem("Imprimir"));
        btnAtualizar.addActionListener(e -> atualizarDados());

        painelBotoes.add(btnExportarPDF);
        painelBotoes.add(btnExportarExcel);
        painelBotoes.add(btnImprimir);
        painelBotoes.add(btnAtualizar);

        add(painelBotoes, BorderLayout.SOUTH);

        // Carrega dados iniciais
        atualizarDados();
    }

    private void mostrarMensagem(String acao) {
        JOptionPane.showMessageDialog(this, "Funcionalidade '" + acao + "' em desenvolvimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarDados() {
        // 1. Relatório Geral
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO GERAL ===\n\n");
        sb.append("Estacionamento: ").append(estacionamento.getNome()).append("\n");
        sb.append("Total de Vagas: ").append(estacionamento.getVagas().size()).append("\n");
        sb.append("Vagas Livres:   ").append(estacionamento.getVagasLivres()).append("\n");
        sb.append("Vagas Ocupadas: ").append(estacionamento.getVagasOcupadas()).append("\n");
        sb.append("Valor Hora:     R$ ").append(String.format("%.2f", estacionamento.getValorHora())).append("\n\n");
        
        double totalArrecadado = estacionamento.getTickets().stream()
                .mapToDouble(Ticket::getValorPago).sum();
        sb.append("Total Arrecadado: R$ ").append(String.format("%.2f", totalArrecadado)).append("\n");
        
        areaRelatorioGeral.setText(sb.toString());

        // 2. Gráfico (Repaint forçará o desenho com dados novos se passarmos o estacionamento para o painel)
        painelGrafico.repaint();

        // 3. Histórico
        modeloHistorico.setRowCount(0);
        List<Ticket> tickets = estacionamento.getTickets();
        for (Ticket t : tickets) {
            String saida = (t.getHoraSaida() != null) ? t.getHoraSaida().format(FORMATTER) : "-";
            String status = (t.getHoraSaida() != null) ? "Fechado" : "Aberto";
            
            Object[] linha = {
                t.getId(),
                t.getVeiculo().getPlaca(),
                t.getHoraEntrada().format(FORMATTER),
                saida,
                String.format("R$ %.2f", t.getValorPago()),
                status
            };
            modeloHistorico.addRow(linha);
        }

        // 4. Estatísticas Avançadas
        long totalCarros = tickets.stream().filter(t -> t.getVeiculo().getTipo() == TipoVeiculo.CARRO).count();
        long totalMotos = tickets.stream().filter(t -> t.getVeiculo().getTipo() == TipoVeiculo.MOTO).count();
        long totalCaminhoes = tickets.stream().filter(t -> t.getVeiculo().getTipo() == TipoVeiculo.CAMINHAO).count();

        StringBuilder sbAdv = new StringBuilder();
        sbAdv.append("Distribuição por Tipo de Veículo:\n");
        sbAdv.append("- Carros: ").append(totalCarros).append("\n");
        sbAdv.append("- Motos: ").append(totalMotos).append("\n");
        sbAdv.append("- Caminhões: ").append(totalCaminhoes).append("\n\n");
        
        if (!tickets.isEmpty()) {
            double mediaValor = totalArrecadado / tickets.size();
            sbAdv.append("Ticket Médio: R$ ").append(String.format("%.2f", mediaValor)).append("\n");
        }

        areaEstatisticasAvancadas.setText(sbAdv.toString());
    }

    // Classe interna para o gráfico customizado
    private class PainelGraficoOcupacao extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            int total = estacionamento.getVagas().size();
            int ocupadas = estacionamento.getVagasOcupadas();
            int livres = estacionamento.getVagasLivres();
            
            if (total == 0) return;

            int larguraBarra = 100;
            int alturaMax = 300;
            int baseY = 350;
            int startX = 150;

            // Título
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.drawString("Ocupação Atual", 50, 30);

            // Barra Ocupadas
            int alturaOcupadas = (int) ((double) ocupadas / total * alturaMax);
            g.setColor(Color.RED);
            g.fillRect(startX, baseY - alturaOcupadas, larguraBarra, alturaOcupadas);
            g.setColor(Color.BLACK);
            g.drawRect(startX, baseY - alturaOcupadas, larguraBarra, alturaOcupadas);
            g.drawString("Ocupadas: " + ocupadas, startX + 10, baseY + 20);

            // Barra Livres
            int alturaLivres = (int) ((double) livres / total * alturaMax);
            g.setColor(Color.GREEN);
            g.fillRect(startX + 150, baseY - alturaLivres, larguraBarra, alturaLivres);
            g.setColor(Color.BLACK);
            g.drawRect(startX + 150, baseY - alturaLivres, larguraBarra, alturaLivres);
            g.drawString("Livres: " + livres, startX + 160, baseY + 20);
            
            // Eixo
            g.drawLine(50, baseY, 500, baseY);
        }
    }
}
