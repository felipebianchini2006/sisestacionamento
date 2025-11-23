import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DialogoSaida extends JDialog {

    private Ticket ticket;
    private Estacionamento estacionamento;
    private JLabel lblTempo;
    private JLabel lblValor;
    private Timer timer;
    private boolean confirmado;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DialogoSaida(Frame parent, Ticket ticket, Estacionamento estacionamento) {
        super(parent, "Registrar Saída", true);
        this.ticket = ticket;
        this.estacionamento = estacionamento;
        this.confirmado = false;

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Painel Principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Seção Veículo
        adicionarSecao(painelPrincipal, "Veículo", 
            ticket.getVeiculo().toString());

        // Seção Entrada
        adicionarSecao(painelPrincipal, "Horário de Entrada", 
            ticket.getHoraEntrada().format(FORMATTER));

        // Seção Tempo (Dinâmico)
        lblTempo = new JLabel("Calculando...");
        lblTempo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTempo.setAlignmentX(Component.LEFT_ALIGNMENT);
        adicionarComponenteComTitulo(painelPrincipal, "Tempo Estacionado", lblTempo);

        // Seção Valor (Dinâmico)
        lblValor = new JLabel("R$ 0,00");
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(new Color(0, 100, 0)); // Verde escuro
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        adicionarComponenteComTitulo(painelPrincipal, "Valor Estimado", lblValor);

        add(painelPrincipal, BorderLayout.CENTER);

        // Painel de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnDesconto = new JButton("Aplicar Desconto");
        JButton btnConfirmar = new JButton("Confirmar Saída");
        JButton btnCancelar = new JButton("Cancelar");

        // Estilizando botão de confirmação
        btnConfirmar.setBackground(new Color(60, 179, 113));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnDesconto.addActionListener(e -> aplicarDesconto());
        btnConfirmar.addActionListener(e -> confirmarSaida());
        btnCancelar.addActionListener(e -> cancelar());

        painelBotoes.add(btnDesconto);
        painelBotoes.add(btnConfirmar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        // Timer para atualizar tempo e valor a cada segundo
        timer = new Timer(1000, e -> atualizarValores());
        timer.start();
        
        // Atualização inicial imediata
        atualizarValores();
    }

    private void adicionarSecao(JPanel painel, String titulo, String valor) {
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adicionarComponenteComTitulo(painel, titulo, lblValor);
    }

    private void adicionarComponenteComTitulo(JPanel painel, String titulo, JComponent componente) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(Color.GRAY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(lblTitulo);
        painel.add(Box.createRigidArea(new Dimension(0, 5)));
        painel.add(componente);
        painel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void atualizarValores() {
        // Simula o cálculo sem fechar o ticket
        Duration duracao = Duration.between(ticket.getHoraEntrada(), LocalDateTime.now());
        long totalMinutos = duracao.toMinutes();
        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        
        lblTempo.setText(String.format("%d horas e %d minutos", horas, minutos));

        // Cálculo estimado (duplicando lógica do Ticket para visualização)
        long horasCobrar = (long) Math.ceil(totalMinutos / 60.0);
        if (horasCobrar == 0 && totalMinutos > 0) horasCobrar = 1;
        
        double valorBase = horasCobrar * estacionamento.getValorHora();
        if (ticket.getVeiculo().getTipo() != null) {
            valorBase *= ticket.getVeiculo().getTipo().getFator();
        }
        
        // Como o desconto é aplicado no ticket apenas ao fechar ou via método específico,
        // aqui teríamos que saber se já foi aplicado. 
        // O método aplicarDesconto do Estacionamento altera o estado interno do ticket se implementado assim.
        // Vamos assumir que o ticket tem um método getDesconto() ou similar, mas na classe Ticket original
        // o desconto é privado e usado apenas no calcularValor.
        // Para simplificar a visualização, mostramos o valor base.
        // Se o desconto for aplicado, ele será refletido no valor final ao confirmar.
        
        lblValor.setText(String.format("R$ %.2f", valorBase));
    }

    private void aplicarDesconto() {
        String input = JOptionPane.showInputDialog(this, "Informe o percentual de desconto (0-100):");
        if (input != null && !input.isEmpty()) {
            try {
                double percentual = Double.parseDouble(input);
                if (percentual < 0 || percentual > 100) {
                    throw new NumberFormatException();
                }
                
                // Aplica no ticket
                ticket.aplicarDesconto(percentual);
                JOptionPane.showMessageDialog(this, "Desconto de " + percentual + "% aplicado!");
                
                // Força atualização visual (embora o cálculo exato dependa do fechar ticket)
                atualizarValores();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao aplicar desconto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void confirmarSaida() {
        timer.stop();
        
        // O processamento real da saída é feito pelo Estacionamento
        // Aqui apenas confirmamos a intenção
        this.confirmado = true;
        
        // Exibe recibo simulado (o valor final real será calculado pelo Estacionamento.registrarSaida)
        // Mas como precisamos fechar o diálogo para o chamador processar, fazemos isso aqui.
        dispose();
    }

    private void cancelar() {
        timer.stop();
        this.confirmado = false;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
    
    public void mostrarDialogo() {
        setVisible(true);
    }
}
