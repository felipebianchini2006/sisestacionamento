import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PainelControles extends JPanel {

    private Estacionamento estacionamento;
    private JTextArea areaEstatisticas;
    private Timer timerAtualizacao;

    public PainelControles(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 0)); // Largura fixa de 300px
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Painel de Controle", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)
        ));

        // 1. Título
        JLabel lblTitulo = new JLabel("Estatísticas & Ações");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(Box.createVerticalStrut(10));
        add(lblTitulo);
        add(Box.createVerticalStrut(10));

        // 2. Área de Estatísticas
        areaEstatisticas = new JTextArea(8, 20);
        areaEstatisticas.setEditable(false);
        areaEstatisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaEstatisticas.setBackground(new Color(245, 245, 245));
        areaEstatisticas.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JScrollPane scrollStats = new JScrollPane(areaEstatisticas);
        scrollStats.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollStats.setMaximumSize(new Dimension(280, 150));
        
        add(scrollStats);
        add(Box.createVerticalStrut(20));

        // 3. Botões de Ação
        adicionarBotao("Buscar Veículo", e -> buscarVeiculo());
        adicionarBotao("Relatório Completo", e -> gerarRelatorio());
        adicionarBotao("Configurações", e -> mostrarConfiguracoes());
        adicionarBotao("Limpar Histórico", e -> limparHistorico());

        // Timer de atualização
        timerAtualizacao = new Timer(2000, e -> atualizarEstatisticas());
        timerAtualizacao.start();
        
        // Primeira atualização
        atualizarEstatisticas();
    }

    private void adicionarBotao(String texto, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 35));
        btn.addActionListener(acao);
        
        add(btn);
        add(Box.createVerticalStrut(10));
    }

    private void atualizarEstatisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Status em Tempo Real ---\n");
        sb.append(String.format("Total de Vagas:   %d\n", estacionamento.getVagas().size()));
        sb.append(String.format("Vagas Livres:     %d\n", estacionamento.getVagasLivres()));
        sb.append(String.format("Vagas Ocupadas:   %d\n", estacionamento.getVagasOcupadas()));
        sb.append(String.format("Tickets Emitidos: %d\n", estacionamento.getTickets().size()));
        
        double totalArrecadado = 0;
        for (Ticket t : estacionamento.getTickets()) {
            totalArrecadado += t.getValorPago();
        }
        sb.append(String.format("Total Arrecadado: R$ %.2f", totalArrecadado));

        areaEstatisticas.setText(sb.toString());
    }

    private void buscarVeiculo() {
        String placa = JOptionPane.showInputDialog(this, "Digite a placa do veículo:");
        if (placa != null && !placa.isEmpty()) {
            Veiculo v = estacionamento.buscarVeiculoPorPlaca(placa);
            if (v != null) {
                JOptionPane.showMessageDialog(this, 
                    "Veículo Encontrado:\n" + v.toString(), 
                    "Resultado da Busca", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Veículo não encontrado no pátio.", 
                    "Resultado da Busca", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void gerarRelatorio() {
        // Redireciona a saída do console para uma janela ou apenas avisa que foi gerado no console
        estacionamento.gerarRelatorio();
        JOptionPane.showMessageDialog(this, 
            "Relatório gerado no console do sistema.", 
            "Relatório", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarConfiguracoes() {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidade de configurações em desenvolvimento.", 
            "Configurações", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void limparHistorico() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente limpar o histórico de tickets fechados?", 
            "Limpar Histórico", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Implementação futura: limpar lista de tickets antigos
            JOptionPane.showMessageDialog(this, "Histórico limpo (Simulação).");
        }
    }
}
