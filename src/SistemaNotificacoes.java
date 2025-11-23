import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class SistemaNotificacoes {

    public enum TipoNotificacao {
        SUCESSO(new Color(46, 204, 113), "✓"),
        ERRO(new Color(231, 76, 60), "✕"),
        AVISO(new Color(241, 196, 15), "!"),
        INFO(new Color(52, 152, 219), "i");

        private final Color cor;
        private final String icone;

        TipoNotificacao(Color cor, String icone) {
            this.cor = cor;
            this.icone = icone;
        }
    }

    private static final int MAX_SIMULTANEAS = 3;
    private static final int DURACAO_MS = 3000;
    private static final int LARGURA = 300;
    private static final int ALTURA = 80;
    private static final int MARGEM = 10;

    private static final Queue<NotificacaoDialog> ativas = new LinkedList<>();
    private static final Queue<Runnable> filaEspera = new LinkedList<>();

    public static void exibirNotificacao(String mensagem, TipoNotificacao tipo) {
        if (ativas.size() >= MAX_SIMULTANEAS) {
            filaEspera.add(() -> criarEExibir(mensagem, tipo));
        } else {
            criarEExibir(mensagem, tipo);
        }
    }

    private static void criarEExibir(String mensagem, TipoNotificacao tipo) {
        NotificacaoDialog dialog = new NotificacaoDialog(mensagem, tipo);
        ativas.add(dialog);
        reorganizarPosicoes();
        dialog.animarEntrada();
    }

    private static void removerNotificacao(NotificacaoDialog dialog) {
        ativas.remove(dialog);
        dialog.dispose();
        reorganizarPosicoes();
        
        if (!filaEspera.isEmpty()) {
            filaEspera.poll().run();
        }
    }

    private static void reorganizarPosicoes() {
        int index = 0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int baseX = screenSize.width - LARGURA - MARGEM;
        int baseY = screenSize.height - MARGEM - 40; // 40px para barra de tarefas aprox.

        for (NotificacaoDialog dialog : ativas) {
            int targetY = baseY - ((ALTURA + MARGEM) * (index + 1));
            dialog.setLocation(baseX, targetY);
            index++;
        }
    }

    private static class NotificacaoDialog extends JDialog {
        private Timer timerVida;
        private Timer timerAnimacao;
        private int targetX;
        
        public NotificacaoDialog(String mensagem, TipoNotificacao tipo) {
            setUndecorated(true);
            setAlwaysOnTop(true);
            setSize(LARGURA, ALTURA);
            setLayout(new BorderLayout());
            
            // Borda colorida à esquerda
            JPanel painelCor = new JPanel();
            painelCor.setBackground(tipo.cor);
            painelCor.setPreferredSize(new Dimension(10, ALTURA));
            add(painelCor, BorderLayout.WEST);

            // Conteúdo
            JPanel painelConteudo = new JPanel(new BorderLayout(10, 0));
            painelConteudo.setBackground(Color.WHITE);
            painelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Ícone
            JLabel lblIcone = new JLabel(tipo.icone);
            lblIcone.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblIcone.setForeground(tipo.cor);
            painelConteudo.add(lblIcone, BorderLayout.WEST);

            // Mensagem
            JTextArea txtMsg = new JTextArea(mensagem);
            txtMsg.setWrapStyleWord(true);
            txtMsg.setLineWrap(true);
            txtMsg.setOpaque(false);
            txtMsg.setEditable(false);
            txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            painelConteudo.add(txtMsg, BorderLayout.CENTER);

            add(painelConteudo, BorderLayout.CENTER);
            
            // Sombra simples (borda)
            getRootPane().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        public void animarEntrada() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int finalX = screenSize.width - LARGURA - MARGEM;
            int startX = screenSize.width;
            
            // Define posição Y inicial baseada na fila (será ajustada pelo reorganizarPosicoes, mas precisamos de um Y válido)
            int index = ativas.size() - 1; // É o último adicionado
            int baseY = screenSize.height - MARGEM - 40;
            int y = baseY - ((ALTURA + MARGEM) * (index + 1));
            
            setLocation(startX, y);
            setVisible(true);

            // Animação de slide
            timerAnimacao = new Timer(10, new ActionListener() {
                int currentX = startX;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentX > finalX) {
                        currentX -= 20; // Velocidade do slide
                        setLocation(currentX, getY());
                    } else {
                        setLocation(finalX, getY());
                        timerAnimacao.stop();
                        iniciarTimerVida();
                    }
                }
            });
            timerAnimacao.start();
        }

        private void iniciarTimerVida() {
            timerVida = new Timer(DURACAO_MS, e -> fecharComAnimacao());
            timerVida.setRepeats(false);
            timerVida.start();
        }

        private void fecharComAnimacao() {
            // Animação de fade out (simulada com slide out para direita)
            timerAnimacao = new Timer(10, new ActionListener() {
                int currentX = getX();
                int targetX = Toolkit.getDefaultToolkit().getScreenSize().width;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentX < targetX) {
                        currentX += 20;
                        setLocation(currentX, getY());
                    } else {
                        timerAnimacao.stop();
                        SistemaNotificacoes.removerNotificacao(NotificacaoDialog.this);
                    }
                }
            });
            timerAnimacao.start();
        }
    }
}
