import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class DialogoConfiguracoes extends JDialog {

    private Estacionamento estacionamento;
    private JSpinner spinValorHora;
    private JSpinner spinQtdVagas;
    private JCheckBox chkSons;
    private JCheckBox chkModoEscuro;
    private JCheckBox chkAutoUpdate;
    private JButton btnCorLivre;
    private JButton btnCorOcupada;
    private JButton btnCorVip;

    private static final String CONFIG_FILE = "config.properties";

    public DialogoConfiguracoes(Frame parent, Estacionamento estacionamento) {
        super(parent, "Configurações do Sistema", true);
        this.estacionamento = estacionamento;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Valores Numéricos ---
        JPanel panelNumeros = new JPanel(new GridLayout(2, 2, 10, 10));
        panelNumeros.setBorder(BorderFactory.createTitledBorder("Parâmetros Gerais"));

        panelNumeros.add(new JLabel("Valor por Hora (R$):"));
        spinValorHora = new JSpinner(new SpinnerNumberModel(estacionamento.getValorHora(), 1.0, 100.0, 0.50));
        panelNumeros.add(spinValorHora);

        panelNumeros.add(new JLabel("Quantidade de Vagas:"));
        // Nota: A alteração da quantidade de vagas geralmente requer reinicialização ou lógica complexa
        spinQtdVagas = new JSpinner(new SpinnerNumberModel(estacionamento.getVagas().size(), 5, 100, 1));
        panelNumeros.add(spinQtdVagas);

        mainPanel.add(panelNumeros);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Cores ---
        JPanel panelCores = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCores.setBorder(BorderFactory.createTitledBorder("Personalização de Cores"));

        btnCorLivre = createColorButton("Vaga Livre", Color.GREEN);
        btnCorOcupada = createColorButton("Vaga Ocupada", Color.RED);
        btnCorVip = createColorButton("Vaga VIP", Color.YELLOW);

        panelCores.add(new JLabel("Vaga Livre:"));
        panelCores.add(btnCorLivre);
        panelCores.add(new JLabel("Vaga Ocupada:"));
        panelCores.add(btnCorOcupada);
        panelCores.add(new JLabel("Vaga VIP:"));
        panelCores.add(btnCorVip);

        mainPanel.add(panelCores);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Opções ---
        JPanel panelOpcoes = new JPanel(new GridLayout(3, 1, 5, 5));
        panelOpcoes.setBorder(BorderFactory.createTitledBorder("Preferências"));

        chkSons = new JCheckBox("Ativar sons de alerta");
        chkModoEscuro = new JCheckBox("Modo Escuro");
        chkAutoUpdate = new JCheckBox("Atualização Automática");

        panelOpcoes.add(chkSons);
        panelOpcoes.add(chkModoEscuro);
        panelOpcoes.add(chkAutoUpdate);

        mainPanel.add(panelOpcoes);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // --- Botões ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRestaurar = new JButton("Restaurar Padrões");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnRestaurar.addActionListener(e -> restaurarPadroes());
        btnSalvar.addActionListener(e -> salvarConfiguracoes());
        btnCancelar.addActionListener(e -> dispose());

        panelBotoes.add(btnRestaurar);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);

        add(panelBotoes, BorderLayout.SOUTH);

        carregarConfiguracoes();
    }

    private JButton createColorButton(String label, Color initialColor) {
        JButton btn = new JButton("   ");
        btn.setBackground(initialColor);
        btn.setOpaque(true);
        btn.setBorderPainted(false); // Para mostrar a cor de fundo melhor em alguns LookAndFeels
        btn.setPreferredSize(new Dimension(50, 25));
        
        btn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Escolher cor para " + label, btn.getBackground());
            if (newColor != null) {
                btn.setBackground(newColor);
            }
        });
        return btn;
    }

    private void salvarConfiguracoes() {
        Properties props = new Properties();
        props.setProperty("valorHora", spinValorHora.getValue().toString());
        props.setProperty("qtdVagas", spinQtdVagas.getValue().toString());
        props.setProperty("somAtivo", String.valueOf(chkSons.isSelected()));
        props.setProperty("modoEscuro", String.valueOf(chkModoEscuro.isSelected()));
        props.setProperty("autoUpdate", String.valueOf(chkAutoUpdate.isSelected()));

        props.setProperty("corLivre", String.valueOf(btnCorLivre.getBackground().getRGB()));
        props.setProperty("corOcupada", String.valueOf(btnCorOcupada.getBackground().getRGB()));
        props.setProperty("corVip", String.valueOf(btnCorVip.getBackground().getRGB()));

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Configurações do Sistema de Estacionamento");
            JOptionPane.showMessageDialog(this, "Configurações salvas com sucesso!\nReinicie o sistema para aplicar alterações estruturais (como qtd de vagas).");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar configurações: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarConfiguracoes() {
        File f = new File(CONFIG_FILE);
        if (!f.exists()) {
            restaurarPadroes();
            return;
        }

        try (FileInputStream in = new FileInputStream(f)) {
            Properties props = new Properties();
            props.load(in);

            try {
                double valor = Double.parseDouble(props.getProperty("valorHora", "10.0"));
                spinValorHora.setValue(valor);

                int vagas = Integer.parseInt(props.getProperty("qtdVagas", "20"));
                spinQtdVagas.setValue(vagas);

                chkSons.setSelected(Boolean.parseBoolean(props.getProperty("somAtivo", "true")));
                chkModoEscuro.setSelected(Boolean.parseBoolean(props.getProperty("modoEscuro", "false")));
                chkAutoUpdate.setSelected(Boolean.parseBoolean(props.getProperty("autoUpdate", "true")));

                btnCorLivre.setBackground(new Color(Integer.parseInt(props.getProperty("corLivre", String.valueOf(Color.GREEN.getRGB())))));
                btnCorOcupada.setBackground(new Color(Integer.parseInt(props.getProperty("corOcupada", String.valueOf(Color.RED.getRGB())))));
                btnCorVip.setBackground(new Color(Integer.parseInt(props.getProperty("corVip", String.valueOf(Color.YELLOW.getRGB())))));

            } catch (NumberFormatException e) {
                // Ignora erros de conversão e mantém valores atuais/padrão
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restaurarPadroes() {
        spinValorHora.setValue(10.0);
        spinQtdVagas.setValue(20);
        chkSons.setSelected(true);
        chkModoEscuro.setSelected(false);
        chkAutoUpdate.setSelected(true);
        btnCorLivre.setBackground(Color.GREEN);
        btnCorOcupada.setBackground(Color.RED);
        btnCorVip.setBackground(Color.YELLOW);
    }
}
