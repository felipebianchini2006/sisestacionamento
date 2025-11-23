import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EstacionamentoGUI extends JFrame {

    private Estacionamento estacionamento;
    private PainelVagas painelVagas;
    private PainelControles painelControles;
    private JLabel lblInfo;

    public EstacionamentoGUI() {
        // Inicialização do Estacionamento
        // Em um cenário real, carregaríamos de um arquivo ou banco de dados
        this.estacionamento = new Estacionamento("Estacionamento Central", 20, 10.0);

        // Configurações da Janela Principal
        setTitle("Sistema de Estacionamento");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Controlado pelo WindowListener
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Painel Superior ---
        JPanel painelSuperior = new JPanel();
        painelSuperior.setBackground(new Color(60, 63, 65));
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Sistema de Gerenciamento de Estacionamento");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblInfo = new JLabel(estacionamento.toString());
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(Color.LIGHT_GRAY);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelSuperior.add(lblTitulo);
        painelSuperior.add(Box.createRigidArea(new Dimension(0, 5)));
        painelSuperior.add(lblInfo);

        add(painelSuperior, BorderLayout.NORTH);

        // --- Painel Central ---
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel Esquerdo: Grid de Vagas (Usando a classe PainelVagas criada anteriormente)
        painelVagas = new PainelVagas(estacionamento);
        
        // Painel Direito: Controles e Estatísticas (Usando a classe PainelControles criada anteriormente)
        painelControles = new PainelControles(estacionamento);

        painelCentral.add(new JScrollPane(painelVagas), BorderLayout.CENTER);
        painelCentral.add(painelControles, BorderLayout.EAST);

        add(painelCentral, BorderLayout.CENTER);

        // --- Painel Inferior ---
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelInferior.setBackground(new Color(230, 230, 230));

        JButton btnEntrada = new JButton("Registrar Entrada");
        JButton btnSaida = new JButton("Registrar Saída");
        JButton btnBuscar = new JButton("Buscar Veículo");
        JButton btnRelatorio = new JButton("Relatórios");
        JButton btnConfig = new JButton("Configurações");

        // Estilizando botões
        Dimension btnSize = new Dimension(150, 40);
        btnEntrada.setPreferredSize(btnSize);
        btnSaida.setPreferredSize(btnSize);
        btnBuscar.setPreferredSize(btnSize);
        btnRelatorio.setPreferredSize(btnSize);
        btnConfig.setPreferredSize(btnSize);

        painelInferior.add(btnEntrada);
        painelInferior.add(btnSaida);
        painelInferior.add(btnBuscar);
        painelInferior.add(btnRelatorio);
        painelInferior.add(btnConfig);

        add(painelInferior, BorderLayout.SOUTH);

        // --- Listeners ---
        
        // Botão Entrada
        btnEntrada.addActionListener(e -> {
            DialogoEntrada dialogo = new DialogoEntrada(this);
            Veiculo novoVeiculo = dialogo.getVeiculo();
            if (novoVeiculo != null) {
                Ticket ticket = estacionamento.registrarEntrada(novoVeiculo);
                if (ticket != null) {
                    SistemaNotificacoes.exibirNotificacao("Entrada registrada: " + novoVeiculo.getPlaca(), SistemaNotificacoes.TipoNotificacao.SUCESSO);
                    atualizarInterface();
                } else {
                    SistemaNotificacoes.exibirNotificacao("Erro ao registrar entrada. Estacionamento lotado ou veículo já presente.", SistemaNotificacoes.TipoNotificacao.ERRO);
                }
            }
        });

        // Botão Saída (Abre busca para selecionar qual sair)
        btnSaida.addActionListener(e -> {
            DialogoBusca dialogo = new DialogoBusca(this, estacionamento);
            dialogo.setVisible(true);
            atualizarInterface(); // Atualiza após fechar o diálogo de busca/saída
        });

        // Botão Buscar
        btnBuscar.addActionListener(e -> {
            DialogoBusca dialogo = new DialogoBusca(this, estacionamento);
            dialogo.setVisible(true);
        });

        // Botão Relatórios
        btnRelatorio.addActionListener(e -> {
            TelaRelatorio tela = new TelaRelatorio(estacionamento);
            tela.setVisible(true);
        });

        // Botão Configurações
        btnConfig.addActionListener(e -> {
            DialogoConfiguracoes config = new DialogoConfiguracoes(this, estacionamento);
            config.setVisible(true);
            atualizarInterface(); // Configurações podem mudar cores ou valores
        });

        // Window Listener para salvar ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDados();
                System.exit(0);
            }
        });

        // Atualiza a interface inicialmente
        atualizarInterface();
    }

    public void atualizarInterface() {
        painelVagas.atualizarVagas();
        lblInfo.setText(estacionamento.toString());
        // PainelControles tem seu próprio timer, mas podemos forçar atualização se necessário
        // painelControles.atualizar(); 
        painelVagas.revalidate();
        painelVagas.repaint();
    }

    private void salvarDados() {
        // Implementação futura de persistência de dados (JSON, Banco, etc)
        System.out.println("Salvando dados do sistema...");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Tenta aplicar o Look and Feel do sistema ou Nimbus se preferir
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            EstacionamentoGUI gui = new EstacionamentoGUI();
            gui.setVisible(true);
        });
    }
}
