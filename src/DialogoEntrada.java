import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogoEntrada extends JDialog {

    private JTextField txtPlaca;
    private JTextField txtModelo;
    private JTextField txtCor;
    private JComboBox<TipoVeiculo> cmbTipo;
    private Veiculo veiculoCriado;
    private boolean confirmado;

    public DialogoEntrada(Frame parent) {
        super(parent, "Registrar Entrada de Veículo", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Painel do Formulário
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Componentes
        txtPlaca = new JTextField(8);
        txtModelo = new JTextField();
        txtCor = new JTextField();
        cmbTipo = new JComboBox<>(TipoVeiculo.values());

        // Adicionando componentes ao grid
        adicionarCampo(painelForm, gbc, 0, "Placa:", txtPlaca);
        adicionarCampo(painelForm, gbc, 1, "Modelo:", txtModelo);
        adicionarCampo(painelForm, gbc, 2, "Cor:", txtCor);
        adicionarCampo(painelForm, gbc, 3, "Tipo:", cmbTipo);

        add(painelForm, BorderLayout.CENTER);

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        btnConfirmar.addActionListener(e -> confirmar());
        btnCancelar.addActionListener(e -> cancelar());

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnConfirmar);

        add(painelBotoes, BorderLayout.SOUTH);
        
        // Define botão padrão
        getRootPane().setDefaultButton(btnConfirmar);
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String label, JComponent campo) {
        gbc.gridy = linha;
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        painel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        painel.add(campo, gbc);
    }

    private void confirmar() {
        String placa = txtPlaca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String cor = txtCor.getText().trim();
        TipoVeiculo tipo = (TipoVeiculo) cmbTipo.getSelectedItem();

        // Validação básica de campos vazios
        if (placa.isEmpty() || modelo.isEmpty() || cor.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos os campos são obrigatórios.", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Tenta criar o veículo (o construtor valida a placa)
            this.veiculoCriado = new Veiculo(placa, modelo, cor, tipo);
            this.confirmado = true;
            dispose(); // Fecha o diálogo
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Dados Inválidos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        this.veiculoCriado = null;
        this.confirmado = false;
        dispose();
    }

    /**
     * Exibe o diálogo e retorna o veículo criado.
     * @return O objeto Veiculo ou null se cancelado.
     */
    public Veiculo getVeiculo() {
        setVisible(true);
        return confirmado ? veiculoCriado : null;
    }
}
