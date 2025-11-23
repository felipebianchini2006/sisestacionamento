import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;

public class GraficoOcupacao extends JPanel {

    private Estacionamento estacionamento;
    private int targetLivres;
    private int targetOcupadas;
    private int targetVip;
    
    private int currentLivres;
    private int currentOcupadas;
    private int currentVip;
    
    private Timer animationTimer;

    public GraficoOcupacao(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
        this.currentLivres = 0;
        this.currentOcupadas = 0;
        this.currentVip = 0;

        setBackground(Color.WHITE);
        
        // Timer para animação suave
        animationTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean updated = false;
                if (currentLivres != targetLivres) {
                    currentLivres = approach(currentLivres, targetLivres);
                    updated = true;
                }
                if (currentOcupadas != targetOcupadas) {
                    currentOcupadas = approach(currentOcupadas, targetOcupadas);
                    updated = true;
                }
                if (currentVip != targetVip) {
                    currentVip = approach(currentVip, targetVip);
                    updated = true;
                }
                
                if (updated) {
                    repaint();
                } else {
                    animationTimer.stop();
                }
            }
        });

        // Listener para redimensionamento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
        
        atualizarDados();
    }

    private int approach(int current, int target) {
        if (current < target) return current + 1;
        if (current > target) return current - 1;
        return current;
    }

    public void atualizarDados() {
        // Calcula os totais reais
        int totalVip = 0;
        int totalOcupadas = 0;
        int totalLivres = 0;

        for (Vaga v : estacionamento.getVagas()) {
            if (v.isOcupada()) {
                totalOcupadas++;
            } else if (v.isVip()) {
                totalVip++;
            } else {
                totalLivres++;
            }
        }

        this.targetLivres = totalLivres;
        this.targetOcupadas = totalOcupadas;
        this.targetVip = totalVip;

        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Anti-aliasing para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int barWidth = (width - (4 * padding)) / 3;
        int maxBarHeight = height - (2 * padding) - 40; // Espaço para texto

        int totalVagas = estacionamento.getVagas().size();
        if (totalVagas == 0) totalVagas = 1; // Evita divisão por zero

        // Desenha as barras
        desenharBarra(g2d, "Livres", currentLivres, totalVagas, Color.GREEN, padding, height - padding, barWidth, maxBarHeight);
        desenharBarra(g2d, "Ocupadas", currentOcupadas, totalVagas, Color.RED, padding * 2 + barWidth, height - padding, barWidth, maxBarHeight);
        desenharBarra(g2d, "VIP Livres", currentVip, totalVagas, Color.ORANGE, padding * 3 + barWidth * 2, height - padding, barWidth, maxBarHeight);
        
        // Título
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String titulo = "Distribuição de Vagas";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(titulo, (width - fm.stringWidth(titulo)) / 2, 30);
    }

    private void desenharBarra(Graphics2D g2d, String label, int valor, int total, Color cor, int x, int yBase, int largura, int alturaMax) {
        double percentual = (double) valor / total;
        int alturaBarra = (int) (percentual * alturaMax);
        
        // Sombra
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(x + 5, yBase - alturaBarra + 5, largura, alturaBarra);

        // Barra
        g2d.setColor(cor);
        g2d.fillRect(x, yBase - alturaBarra, largura, alturaBarra);
        
        // Borda
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, yBase - alturaBarra, largura, alturaBarra);

        // Texto (Valor e Percentual)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String textoValor = String.valueOf(valor);
        String textoPercent = new DecimalFormat("#0.0%").format(percentual);
        
        FontMetrics fm = g2d.getFontMetrics();
        
        // Centraliza texto na barra
        int textX = x + (largura - fm.stringWidth(textoValor)) / 2;
        g2d.drawString(textoValor, textX, yBase - alturaBarra - 5);
        
        // Legenda abaixo da barra
        int labelX = x + (largura - fm.stringWidth(label)) / 2;
        g2d.drawString(label, labelX, yBase + 20);
        
        // Percentual abaixo da legenda
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        int perX = x + (largura - fm.stringWidth(textoPercent)) / 2;
        g2d.drawString(textoPercent, perX, yBase + 40);
    }
}
