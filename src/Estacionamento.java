import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe principal que gerencia o estacionamento.
 * Controla vagas, tickets, entradas, saídas e relatórios.
 */
public class Estacionamento {
    private String nome;
    private ArrayList<Vaga> vagas;
    private ArrayList<Ticket> tickets;
    private double valorHora;
    private int proximoIdTicket;

    /**
     * Construtor da classe Estacionamento.
     * Inicializa as vagas e listas de controle.
     * 
     * @param nome Nome do estacionamento.
     * @param quantidadeVagas Número total de vagas (1 a 100).
     * @param valorHora Valor cobrado por hora.
     * @throws IllegalArgumentException Se os parâmetros forem inválidos.
     */
    public Estacionamento(String nome, int quantidadeVagas, double valorHora) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do estacionamento não pode ser nulo ou vazio.");
        }
        if (quantidadeVagas <= 0 || quantidadeVagas > 100) {
            throw new IllegalArgumentException("A quantidade de vagas deve ser entre 1 e 100.");
        }
        if (valorHora < 0) {
            throw new IllegalArgumentException("O valor por hora não pode ser negativo.");
        }

        this.nome = nome;
        this.vagas = new ArrayList<>();
        for (int i = 1; i <= quantidadeVagas; i++) {
            this.vagas.add(new Vaga(i));
        }
        this.tickets = new ArrayList<>();
        this.valorHora = valorHora;
        this.proximoIdTicket = 1;
    }

    /**
     * Calcula a quantidade de vagas livres no momento.
     * @return Número de vagas livres.
     */
    public int getVagasLivres() {
        int livre = 0;
        for (Vaga v : vagas) {
            if (!v.isOcupada()) {
                livre++;
            }
        }
        return livre;
    }

    /**
     * Calcula a quantidade de vagas ocupadas no momento.
     * @return Número de vagas ocupadas.
     */
    public int getVagasOcupadas() {
        return vagas.size() - getVagasLivres();
    }

    /**
     * Busca a primeira vaga livre disponível.
     * @return Objeto Vaga livre ou null se estiver lotado.
     */
    public Vaga buscarVagaLivre() {
        for (Vaga v : vagas) {
            if (!v.isOcupada()) {
                return v;
            }
        }
        return null;
    }

    public String getNome() {
        return nome;
    }

    public double getValorHora() {
        return valorHora;
    }

    public List<Vaga> getVagas() {
        return Collections.unmodifiableList(vagas);
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public int getProximoIdTicket() {
        return proximoIdTicket;
    }

    /**
     * Gera o próximo ID sequencial para tickets.
     * @return Próximo ID disponível.
     */
    public int gerarProximoIdTicket() {
        return proximoIdTicket++;
    }

    /**
     * Registra a entrada de um veículo no estacionamento.
     * Verifica disponibilidade de vagas e se o veículo já está estacionado.
     * 
     * @param v O veículo a ser estacionado.
     * @return O Ticket gerado ou null em caso de erro (lotado ou já estacionado).
     */
    public Ticket registrarEntrada(Veiculo v) {
        if (v == null) return null;
        
        // Verifica se já está estacionado
        if (buscarTicketAtivo(v.getPlaca()) != null) {
            System.out.println("Veículo já está no estacionamento.");
            return null; 
        }

        Vaga vagaLivre = buscarVagaLivre();
        if (vagaLivre == null) {
            System.out.println("Estacionamento lotado.");
            return null; 
        }

        vagaLivre.ocuparVaga(v);
        Ticket novoTicket = new Ticket(gerarProximoIdTicket(), v);
        tickets.add(novoTicket);
        return novoTicket;
    }

    /**
     * Registra a saída de um veículo.
     * Calcula o valor a pagar e libera a vaga.
     * 
     * @param placa A placa do veículo que está saindo.
     * @return O Ticket fechado com o valor calculado ou null se não encontrado.
     */
    public Ticket registrarSaida(String placa) {
        if (placa == null || placa.trim().isEmpty()) return null;

        Ticket ticket = buscarTicketAtivo(placa);
        if (ticket == null) {
            System.out.println("Ticket não encontrado para a placa informada.");
            return null; 
        }

        ticket.registrarSaida();
        ticket.calcularValor(this.valorHora);

        Vaga vaga = buscarVagaPorPlaca(placa);
        if (vaga != null) {
            vaga.liberarVaga();
        }

        return ticket;
    }

    private Ticket buscarTicketAtivo(String placa) {
        for (Ticket t : tickets) {
            // Considera ativo se horaSaida for null
            if (t.getVeiculo().getPlaca().equalsIgnoreCase(placa) && t.getHoraSaida() == null) {
                return t;
            }
        }
        return null;
    }

    private Vaga buscarVagaPorPlaca(String placa) {
        for (Vaga v : vagas) {
            if (v.isOcupada() && v.getVeiculo() != null && v.getVeiculo().getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Exibe no console a lista de todas as vagas e seus status.
     */
    public void listarVagas() {
        System.out.println("\n--- Status das Vagas ---");
        for (Vaga v : vagas) {
            System.out.println(v);
        }
    }

    /**
     * Exibe no console apenas os veículos atualmente estacionados.
     */
    public void listarVeiculosEstacionados() {
        System.out.println("\n--- Veículos Estacionados ---");
        boolean encontrou = false;
        for (Vaga v : vagas) {
            if (v.isOcupada()) {
                System.out.println("Vaga " + v.getNumero() + ": " + v.getVeiculo());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Nenhum veículo estacionado no momento.");
        }
    }

    /**
     * Busca um veículo estacionado pela placa.
     * @param placa A placa a ser pesquisada.
     * @return O objeto Veiculo ou null se não encontrado.
     */
    public Veiculo buscarVeiculoPorPlaca(String placa) {
        Vaga vaga = buscarVagaPorPlaca(placa);
        return (vaga != null) ? vaga.getVeiculo() : null;
    }

    /**
     * Gera e exibe um relatório financeiro e operacional básico.
     */
    public void gerarRelatorio() {
        System.out.println("\n=== RELATÓRIO DO ESTACIONAMENTO: " + nome.toUpperCase() + " ===");
        System.out.println("Valor Hora: R$ " + String.format("%.2f", valorHora));
        System.out.println("Total de Vagas: " + vagas.size());
        System.out.println("Vagas Livres: " + getVagasLivres());
        System.out.println("Vagas Ocupadas: " + getVagasOcupadas());
        
        System.out.println("\n--- Financeiro ---");
        System.out.println("Total de Tickets Emitidos: " + tickets.size());
        
        double totalArrecadado = 0;
        for (Ticket t : tickets) {
            totalArrecadado += t.getValorPago();
        }
        System.out.println("Total Arrecadado: R$ " + String.format("%.2f", totalArrecadado));

        System.out.println("\n--- Últimos 5 Tickets ---");
        int totalTickets = tickets.size();
        int inicio = Math.max(0, totalTickets - 5);
        
        if (totalTickets == 0) {
            System.out.println("Nenhum ticket gerado ainda.");
        } else {
            // Itera do mais recente para o mais antigo dentro do limite de 5
            for (int i = totalTickets - 1; i >= inicio; i--) {
                System.out.println(tickets.get(i));
            }
        }
        System.out.println("=============================================");
    }

    /**
     * Busca tickets emitidos dentro de um período específico.
     * @param inicio Data inicial.
     * @param fim Data final.
     * @return Lista de tickets no período.
     */
    public List<Ticket> buscarTicketsPorPeriodo(LocalDate inicio, LocalDate fim) {
        return tickets.stream()
            .filter(t -> {
                LocalDate dataTicket = t.getHoraEntrada().toLocalDate();
                return (dataTicket.isEqual(inicio) || dataTicket.isAfter(inicio)) &&
                       (dataTicket.isEqual(fim) || dataTicket.isBefore(fim));
            })
            .collect(Collectors.toList());
    }

    /**
     * Aplica um desconto a um ticket ativo.
     * @param placa Placa do veículo.
     * @param percentual Percentual de desconto (0-100).
     * @return true se aplicado com sucesso, false se não encontrar ticket ativo.
     */
    public boolean aplicarDesconto(String placa, double percentual) {
        Ticket ticket = buscarTicketAtivo(placa);
        if (ticket != null) {
            ticket.aplicarDesconto(percentual);
            return true;
        }
        return false;
    }

    /**
     * Marca uma vaga específica como VIP.
     * @param numero Número da vaga.
     * @return true se sucesso, false se número inválido.
     */
    public boolean reservarVaga(int numero) {
        if (numero < 1 || numero > vagas.size()) return false;
        Vaga vaga = vagas.get(numero - 1);
        vaga.setVip(true);
        return true;
    }

    @Override
    public String toString() {
        return String.format("Estacionamento '%s' - Vagas: %d (Livres: %d, Ocupadas: %d) - Valor/h: R$ %.2f",
                nome, vagas.size(), getVagasLivres(), getVagasOcupadas(), valorHora);
    }
}
