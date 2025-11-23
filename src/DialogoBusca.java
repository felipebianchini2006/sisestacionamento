import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class DialogoBusca extends JDialog {

    private Estacionamento estacionamento;
    private JTextField txtBusca;
    private JTable tabelaResultados;
    private DefaultTableModel modeloTabela;
    private JButton btnDetalhes;
    private JButton btnSaida;

    public DialogoBusca(Frame parent, Estacionamento estacionamento) {
        super(parent, "Buscar Veículo", true);
        this.estacionamento = estacionamento;

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Painel de Busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        
        painelBusca.add(new JLabel("Placa:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);

        add(painelBusca, BorderLayout.NORTH);

        // Tabela de Resultados
        String[] colunas = {"Placa", "Modelo", "Cor", "Vaga", "Tempo Estacionado"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaResultados = new JTable(modeloTabela);
        tabelaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaResultados.setRowHeight(25);
        
        JScrollPane scrollTabela = new JScrollPane(tabelaResultados);
        add(scrollTabela, BorderLayout.CENTER);

        // Painel de Ações
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDetalhes = new JButton("Ver Detalhes");
        btnSaida = new JButton("Registrar Saída");
        JButton btnFechar = new JButton("Fechar");

        btnDetalhes.setEnabled(false);
        btnSaida.setEnabled(false);

        painelAcoes.add(btnDetalhes);
        painelAcoes.add(btnSaida);
        painelAcoes.add(btnFechar);

        add(painelAcoes, BorderLayout.SOUTH);

        // Listeners
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarResultados(txtBusca.getText());
            }
        });

        btnBuscar.addActionListener(e -> filtrarResultados(txtBusca.getText()));

        tabelaResultados.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaResultados.getSelectedRow() != -1;
            btnDetalhes.setEnabled(selecionado);
            btnSaida.setEnabled(selecionado);
        });

        tabelaResultados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelaResultados.getSelectedRow() != -1) {
                    mostrarDetalhes();
                }
            }
        });

        btnDetalhes.addActionListener(e -> mostrarDetalhes());
        btnSaida.addActionListener(e -> registrarSaida());
        btnFechar.addActionListener(e -> dispose());

        // Carrega todos os veículos inicialmente
        filtrarResultados("");
    }

    private void filtrarResultados(String termo) {
        modeloTabela.setRowCount(0);
        String termoBusca = termo.toUpperCase().trim();

        List<Vaga> vagasOcupadas = estacionamento.getVagas().stream()
            .filter(Vaga::isOcupada)
            .collect(Collectors.toList());

        for (Vaga vaga : vagasOcupadas) {
            Veiculo v = vaga.getVeiculo();
            if (termoBusca.isEmpty() || v.getPlaca().contains(termoBusca)) {
                // Busca o ticket ativo para calcular o tempo
                Ticket ticket = buscarTicketAtivo(v.getPlaca());
                String tempo = (ticket != null) ? ticket.formatarDuracao() : "N/A";

                Object[] linha = {
                    v.getPlaca(),
                    v.getModelo(),
                    v.getCor(),
                    vaga.getNumero(),
                    tempo
                };
                modeloTabela.addRow(linha);
            }
        }
    }

    private Ticket buscarTicketAtivo(String placa) {
        // Método auxiliar pois o Estacionamento.buscarTicketAtivo é privado
        // Vamos iterar sobre os tickets do estacionamento
        for (Ticket t : estacionamento.getTickets()) {
            if (t.getVeiculo().getPlaca().equalsIgnoreCase(placa) && t.getHoraSaida() == null) {
                return t;
            }
        }
        return null;
    }

    private void mostrarDetalhes() {
        int row = tabelaResultados.getSelectedRow();
        if (row == -1) return;

        String placa = (String) modeloTabela.getValueAt(row, 0);
        Veiculo v = estacionamento.buscarVeiculoPorPlaca(placa);
        
        if (v != null) {
            JOptionPane.showMessageDialog(this, 
                "Detalhes do Veículo:\n\n" + v.toString(), 
                "Detalhes", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void registrarSaida() {
        int row = tabelaResultados.getSelectedRow();
        if (row == -1) return;

        String placa = (String) modeloTabela.getValueAt(row, 0);
        Ticket ticket = buscarTicketAtivo(placa);

        if (ticket != null) {
            DialogoSaida dialogo = new DialogoSaida((Frame) getOwner(), ticket, estacionamento);
            dialogo.mostrarDialogo();

            if (dialogo.isConfirmado()) {
                estacionamento.registrarSaida(placa);
                JOptionPane.showMessageDialog(this, "Saída registrada com sucesso!");
                filtrarResultados(txtBusca.getText()); // Atualiza a tabela
            }
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao localizar ticket ativo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
