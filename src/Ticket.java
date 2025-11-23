import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa o comprovante de estacionamento de um veículo.
 * Registra horários de entrada e saída, calcula valores e armazena informações financeiras.
 */
public class Ticket {
    private int id;
    private Veiculo veiculo;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private double valorPago;
    private double desconto;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Construtor da classe Ticket.
     * Registra automaticamente a hora de entrada como o momento atual.
     * 
     * @param id Identificador único do ticket.
     * @param veiculo O veículo associado ao ticket.
     */
    public Ticket(int id, Veiculo veiculo) {
        this.id = id;
        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
        this.desconto = 0.0;
    }

    /**
     * Aplica um percentual de desconto ao valor do ticket.
     * @param percentual Valor entre 0 e 100.
     * @throws IllegalArgumentException Se o percentual for inválido.
     */
    public void aplicarDesconto(double percentual) {
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("Desconto deve ser entre 0 e 100.");
        }
        this.desconto = percentual;
    }

    /**
     * Registra o horário de saída como o momento atual.
     */
    public void registrarSaida() {
        this.horaSaida = LocalDateTime.now();
    }

    /**
     * Calcula o valor a ser pago com base no tempo de permanência, tipo de veículo e descontos.
     * Regra: Cobrança por hora cheia (arredondamento para cima).
     * 
     * @param valorHora O valor base da hora de estacionamento.
     * @throws IllegalStateException Se a saída ainda não tiver sido registrada.
     */
    public void calcularValor(double valorHora) {
        if (this.horaSaida == null) {
            throw new IllegalStateException("É necessário registrar a saída antes de calcular o valor.");
        }

        Duration duracao = Duration.between(horaEntrada, horaSaida);
        long minutos = duracao.toMinutes();
        
        long horasCobrar = (long) Math.ceil(minutos / 60.0);
        if (horasCobrar == 0 && minutos > 0) {
            horasCobrar = 1;
        }

        double valorBase = horasCobrar * valorHora;
        
        // Aplica fator do tipo de veículo
        if (veiculo.getTipo() != null) {
            valorBase *= veiculo.getTipo().getFator();
        }
        
        // Aplica desconto
        double valorDesconto = valorBase * (desconto / 100.0);
        this.valorPago = valorBase - valorDesconto;
    }

    /**
     * Calcula a duração da estadia.
     * Se o veículo ainda estiver estacionado, calcula até o momento atual.
     * @return Objeto Duration representando o tempo decorrido.
     */
    public Duration getTempoEstacionado() {
        LocalDateTime fim = (horaSaida != null) ? horaSaida : LocalDateTime.now();
        return Duration.between(horaEntrada, fim);
    }

    /**
     * Formata a duração da estadia em uma string legível.
     * @return String no formato "X horas e Y minutos".
     */
    public String formatarDuracao() {
        Duration d = getTempoEstacionado();
        long totalMinutos = d.toMinutes();
        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        return String.format("%d horas e %d minutos", horas, minutos);
    }

    /**
     * Obtém o ID do ticket.
     * @return O ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtém o veículo associado.
     * @return O objeto Veiculo.
     */
    public Veiculo getVeiculo() {
        return veiculo;
    }

    /**
     * Obtém a hora de entrada.
     * @return LocalDateTime da entrada.
     */
    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    /**
     * Obtém a hora de saída.
     * @return LocalDateTime da saída ou null se ainda estiver estacionado.
     */
    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    /**
     * Obtém o valor final pago.
     * @return O valor em reais.
     */
    public double getValorPago() {
        return valorPago;
    }

    /**
     * Retorna uma representação em string do ticket.
     * @return String formatada com detalhes do ticket.
     */
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
