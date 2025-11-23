import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private int id;
    private Veiculo veiculo;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private double valorPago;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Ticket(int id, Veiculo veiculo) {
        this.id = id;
        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
    }

    public void registrarSaida() {
        this.horaSaida = LocalDateTime.now();
    }

    public void calcularValor(double valorHora) {
        if (this.horaSaida == null) {
            throw new IllegalStateException("É necessário registrar a saída antes de calcular o valor.");
        }

        Duration duracao = Duration.between(horaEntrada, horaSaida);
        long minutos = duracao.toMinutes();
        
        // Arredonda para cima a cada hora iniciada
        // Ex: 1 min -> 1 hora. 61 min -> 2 horas.
        long horasCobrar = (long) Math.ceil(minutos / 60.0);
        
        // Garante cobrança mínima de 1 hora se houve algum tempo de permanência (caso Math.ceil retorne 0 para < 1 min se fosse double puro, mas aqui minutos é long)
        // Na verdade, Math.ceil(1/60.0) é 1. Math.ceil(0) é 0.
        if (horasCobrar == 0 && minutos > 0) {
            horasCobrar = 1;
        }

        this.valorPago = horasCobrar * valorHora;
    }

    public Duration getTempoEstacionado() {
        LocalDateTime fim = (horaSaida != null) ? horaSaida : LocalDateTime.now();
        return Duration.between(horaEntrada, fim);
    }

    public int getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public double getValorPago() {
        return valorPago;
    }

    @Override
    public String toString() {
        String saidaStr = (horaSaida != null) ? horaSaida.format(FORMATTER) : "Em aberto";
        String valorStr = (horaSaida != null) ? String.format("R$ %.2f", valorPago) : "A calcular";
        
        return String.format(
            "Ticket #%d | Veículo: %s | Entrada: %s | Saída: %s | Valor: %s",
            id, veiculo.getPlaca(), horaEntrada.format(FORMATTER), saidaStr, valorStr
        );
    }
}
