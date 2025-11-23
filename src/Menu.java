import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal que fornece a interface de usuário (CLI) para o sistema.
 * Gerencia a interação com o usuário e chama os métodos da classe Estacionamento.
 */
public class Menu {
    // Cores ANSI
    /** Código ANSI para resetar cor */
    public static final String ANSI_RESET = "\u001B[0m";
    /** Código ANSI para cor vermelha */
    public static final String ANSI_RED = "\u001B[31m";
    /** Código ANSI para cor verde */
    public static final String ANSI_GREEN = "\u001B[32m";
    /** Código ANSI para cor amarela */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /** Código ANSI para cor azul */
    public static final String ANSI_BLUE = "\u001B[34m";
    /** Código ANSI para cor ciano */
    public static final String ANSI_CYAN = "\u001B[36m";

    private static Estacionamento estacionamento;
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Ponto de entrada da aplicação.
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        inicializarSistema();
        exibirMenu();
    }

    private static void inicializarSistema() {
        System.out.println("Inicializando Sistema de Estacionamento...");
        // Configuração inicial fixa para demonstração
        estacionamento = new Estacionamento("Estacionamento Central", 10, 15.00);
        
        inicializarDadosTeste();
        
        System.out.println("Sistema iniciado: " + estacionamento.getNome());
        System.out.println("Valor por hora: R$ " + estacionamento.getValorHora());
        System.out.println("Total de vagas: " + estacionamento.getVagas().size());
        System.out.println("--------------------------------------------------");
    }

    private static void inicializarDadosTeste() {
        try {
            System.out.println("Gerando dados de teste...");
            estacionamento.registrarEntrada(new Veiculo("ABC1234", "Fiat Uno", "Branco", TipoVeiculo.CARRO));
            estacionamento.registrarEntrada(new Veiculo("XYZ9876", "VW Gol", "Preto", TipoVeiculo.CARRO));
            estacionamento.registrarEntrada(new Veiculo("DEF5G67", "Ford Ka", "Vermelho", TipoVeiculo.CARRO));
            
            // Simular saída do primeiro veículo
            Ticket t = estacionamento.registrarSaida("ABC1234");
            if(t != null) {
                 System.out.println("Saída simulada para ABC1234.");
            }
            System.out.println("Dados de teste carregados com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao gerar dados de teste: " + e.getMessage());
        }
    }

    private static void exibirMenu() {
        int opcao = -1;
        do {
            try {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1 - Registrar Entrada");
                System.out.println("2 - Registrar Saída");
                System.out.println("3 - Consultar Vagas");
                System.out.println("4 - Listar Veículos Estacionados");
                System.out.println("5 - Buscar Veículo por Placa");
                System.out.println("6 - Gerar Relatório");
                System.out.println("7 - Reservar Vaga VIP");
                System.out.println("8 - Aplicar Desconto");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");

                String entrada = scanner.nextLine();
                opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1:
                        registrarEntrada();
                        break;
                    case 2:
                        registrarSaida();
                        break;
                    case 3:
                        consultarVagas();
                        break;
                    case 4:
                        listarVeiculos();
                        break;
                    case 5:
                        buscarVeiculo();
                        break;
                    case 6:
                        gerarRelatorioCompleto();
                        break;
                    case 7:
                        reservarVaga();
                        break;
                    case 8:
                        aplicarDesconto();
                        break;
                    case 0:
                        System.out.println("Encerrando sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Por favor, digite um número.");
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static void registrarEntrada() {
        System.out.println("\n--- Registrar Entrada ---");
        if (estacionamento.getVagasLivres() == 0) {
            System.out.println("Não há vagas disponíveis no momento.");
            return;
        }

        System.out.print("Placa: ");
        String placa = scanner.nextLine().toUpperCase();
        
        System.out.print("Modelo: ");
        String modelo = scanner.nextLine();
        
        System.out.print("Cor: ");
        String cor = scanner.nextLine();

        System.out.println("Tipo de Veículo:");
        System.out.println("1 - Carro");
        System.out.println("2 - Moto");
        System.out.println("3 - Caminhão");
        System.out.print("Opção: ");
        
        TipoVeiculo tipo = TipoVeiculo.CARRO;
        try {
            int tipoOpcao = Integer.parseInt(scanner.nextLine());
            switch (tipoOpcao) {
                case 2: tipo = TipoVeiculo.MOTO; break;
                case 3: tipo = TipoVeiculo.CAMINHAO; break;
                default: tipo = TipoVeiculo.CARRO; break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida, assumindo CARRO.");
        }

        try {
            Veiculo veiculo = new Veiculo(placa, modelo, cor, tipo);
            Ticket ticket = estacionamento.registrarEntrada(veiculo);
            
            if (ticket != null) {
                System.out.println(ANSI_GREEN + "✓ Entrada registrada com sucesso!" + ANSI_RESET);
                
                // Busca a vaga onde o veículo foi estacionado para exibir o número
                for (Vaga v : estacionamento.getVagas()) {
                    if (v.isOcupada() && v.getVeiculo().getPlaca().equals(placa)) {
                        System.out.println("Vaga: " + v.getNumero());
                        break;
                    }
                }
                
                System.out.println("Horário: " + ticket.getHoraEntrada().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                System.out.println(ticket);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ANSI_RED + "Erro ao criar veículo: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void registrarSaida() {
        System.out.println("\n--- Registrar Saída ---");
        System.out.print("Digite a placa do veículo: ");
        String placa = scanner.nextLine().toUpperCase();

        if (placa.trim().isEmpty()) {
            System.out.println("Erro: A placa deve ser informada.");
            return;
        }

        try {
            Ticket ticket = estacionamento.registrarSaida(placa);
            if (ticket != null) {
                System.out.println("\n=== RECIBO DE SAÍDA ===");
                System.out.println("Veículo: " + ticket.getVeiculo());
                
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                System.out.println("Entrada: " + ticket.getHoraEntrada().format(formatter));
                System.out.println("Saída:   " + ticket.getHoraSaida().format(formatter));
                
                long totalMinutos = ticket.getTempoEstacionado().toMinutes();
                long horas = totalMinutos / 60;
                long minutos = totalMinutos % 60;
                System.out.println(String.format("Tempo Total: %dh %dmin", horas, minutos));
                
                System.out.println(String.format("VALOR A PAGAR: R$ %.2f", ticket.getValorPago()));
                System.out.println("=======================");
            }
        } catch (Exception e) {
            System.out.println("Erro inesperado ao registrar saída: " + e.getMessage());
        }
    }

    private static void buscarVeiculo() {
        limparTela();
        exibirCabecalho("Buscar Veículo");
        System.out.print("Digite a placa: ");
        String placa = scanner.nextLine().toUpperCase();

        Veiculo v = estacionamento.buscarVeiculoPorPlaca(placa);
        if (v != null) {
            System.out.println("\nVeículo encontrado:");
            System.out.println("Placa: " + v.getPlaca());
            System.out.println("Modelo: " + v.getModelo());
            System.out.println("Cor: " + v.getCor());
            
            Ticket ticketAtivo = null;
            for(Ticket t : estacionamento.getTickets()) {
                if(t.getVeiculo().getPlaca().equals(placa) && t.getHoraSaida() == null) {
                    ticketAtivo = t;
                    break;
                }
            }
            
            if(ticketAtivo != null) {
                System.out.println("Tempo estacionado: " + ticketAtivo.formatarDuracao());
            }
        } else {
            System.out.println("Veículo não encontrado no estacionamento.");
        }
        pausar();
    }

    private static void consultarVagas() {
        limparTela();
        exibirCabecalho("Status das Vagas");
        System.out.println("Vagas Livres: " + estacionamento.getVagasLivres());
        System.out.println("Vagas Ocupadas: " + estacionamento.getVagasOcupadas());
        exibirLinha();
        estacionamento.listarVagas();
        pausar();
    }

    private static void listarVeiculos() {
        limparTela();
        exibirCabecalho("Veículos Estacionados");
        System.out.printf("%-5s | %-10s | %-15s | %-10s%n", "Vaga", "Placa", "Modelo", "Cor");
        exibirLinha();
        boolean encontrou = false;
        for (Vaga v : estacionamento.getVagas()) {
            if (v.isOcupada()) {
                Veiculo veic = v.getVeiculo();
                System.out.printf("%-5d | %-10s | %-15s | %-10s%n", 
                    v.getNumero(), veic.getPlaca(), veic.getModelo(), veic.getCor());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Nenhum veículo estacionado.");
        }
        pausar();
    }

    private static void limparBuffer() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private static void exibirCabecalho(String titulo) {
        System.out.println("\n" + ANSI_CYAN + "=== " + titulo.toUpperCase() + " ===" + ANSI_RESET);
    }

    private static void exibirLinha() {
        System.out.println("--------------------------------------------------");
    }

    private static String formatarMoeda(double valor) {
        return String.format("R$ %.2f", valor);
    }

    private static void pausar() {
        System.out.println("\nPressione Enter para continuar...");
        scanner.nextLine();
    }

    private static void limparTela() {
        // Simula limpeza de tela pulando linhas
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static void gerarRelatorioCompleto() {
        limparTela();
        exibirCabecalho("Relatório Gerencial Completo");
        
        // Chama o relatório básico da classe Estacionamento
        estacionamento.gerarRelatorio();
        
        System.out.println("\n--- Estatísticas Avançadas ---");
        
        List<Ticket> tickets = estacionamento.getTickets();
        if (tickets.isEmpty()) {
            System.out.println("Dados insuficientes para estatísticas avançadas.");
        } else {
            // Tempo médio
            long somaMinutos = 0;
            int ticketsFechados = 0;
            Ticket ticketMaiorValor = null;
            int[] entradasPorHora = new int[24];

            for (Ticket t : tickets) {
                // Contabiliza hora de entrada para pico
                entradasPorHora[t.getHoraEntrada().getHour()]++;

                if (t.getHoraSaida() != null) {
                    long minutos = t.getTempoEstacionado().toMinutes();
                    somaMinutos += minutos;
                    ticketsFechados++;

                    if (ticketMaiorValor == null || t.getValorPago() > ticketMaiorValor.getValorPago()) {
                        ticketMaiorValor = t;
                    }
                }
            }

            if (ticketsFechados > 0) {
                long mediaMinutos = somaMinutos / ticketsFechados;
                System.out.printf("Tempo Médio de Permanência: %d min%n", mediaMinutos);
                
                if (ticketMaiorValor != null) {
                    System.out.printf("Ticket de Maior Valor: %s (R$ %.2f)%n", 
                        ticketMaiorValor.getVeiculo().getPlaca(), ticketMaiorValor.getValorPago());
                }
            } else {
                System.out.println("Nenhum ticket finalizado para cálculo de médias.");
            }

            // Horário de pico
            int horaPico = 0;
            int maxEntradas = 0;
            for (int i = 0; i < 24; i++) {
                if (entradasPorHora[i] > maxEntradas) {
                    maxEntradas = entradasPorHora[i];
                    horaPico = i;
                }
            }
            System.out.printf("Horário de Pico: %02d:00 - %02d:59 (%d entradas)%n", 
                horaPico, horaPico, maxEntradas);
        }
        
        exibirLinha();
        pausar();
    }

    private static void reservarVaga() {
        limparTela();
        exibirCabecalho("Reservar Vaga VIP");
        estacionamento.listarVagas();
        System.out.print("Digite o número da vaga para tornar VIP: ");
        try {
            int numero = Integer.parseInt(scanner.nextLine());
            if (estacionamento.reservarVaga(numero)) {
                System.out.println(ANSI_GREEN + "Vaga " + numero + " marcada como VIP com sucesso!" + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "Erro: Vaga inválida." + ANSI_RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Erro: Digite um número válido." + ANSI_RESET);
        }
        pausar();
    }

    private static void aplicarDesconto() {
        limparTela();
        exibirCabecalho("Aplicar Desconto");
        System.out.print("Digite a placa do veículo: ");
        String placa = scanner.nextLine().toUpperCase();
        
        System.out.print("Digite o percentual de desconto (0-100): ");
        try {
            double percentual = Double.parseDouble(scanner.nextLine());
            if (estacionamento.aplicarDesconto(placa, percentual)) {
                System.out.println(ANSI_GREEN + "Desconto aplicado com sucesso!" + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "Erro: Veículo não encontrado ou ticket já fechado." + ANSI_RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "Erro: Valor inválido." + ANSI_RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(ANSI_RED + "Erro: " + e.getMessage() + ANSI_RESET);
        }
        pausar();
    }
}
