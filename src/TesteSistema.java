import java.time.LocalDateTime;

/**
 * Classe de testes automatizados para o Sistema de Estacionamento.
 * Verifica os principais cenários de uso e regras de negócio.
 */
public class TesteSistema {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO BATERIA DE TESTES ===\n");

        testarEstacionamentoVazio();
        testarEstacionamentoLotado();
        testarVeiculoJaEstacionado();
        testarSaidaVeiculoInexistente();
        testarCalculoValores();

        System.out.println("\n=== TESTES FINALIZADOS ===");
    }

    private static void testarEstacionamentoVazio() {
        System.out.print("Teste 1: Estacionamento Vazio... ");
        Estacionamento est = new Estacionamento("Teste", 5, 10.0);
        if (est.getVagasLivres() == 5 && est.getVagasOcupadas() == 0) {
            System.out.println("PASSOU");
        } else {
            System.out.println("FALHOU");
        }
    }

    private static void testarEstacionamentoLotado() {
        System.out.print("Teste 2: Estacionamento Lotado... ");
        Estacionamento est = new Estacionamento("Teste", 2, 10.0);
        est.registrarEntrada(new Veiculo("AAA1111", "Modelo", "Cor", TipoVeiculo.CARRO));
        est.registrarEntrada(new Veiculo("BBB2222", "Modelo", "Cor", TipoVeiculo.CARRO));
        
        Ticket t = est.registrarEntrada(new Veiculo("CCC3333", "Modelo", "Cor", TipoVeiculo.CARRO));
        
        if (est.getVagasLivres() == 0 && t == null) {
            System.out.println("PASSOU");
        } else {
            System.out.println("FALHOU");
        }
    }

    private static void testarVeiculoJaEstacionado() {
        System.out.print("Teste 3: Veículo Já Estacionado... ");
        Estacionamento est = new Estacionamento("Teste", 5, 10.0);
        Veiculo v = new Veiculo("AAA1111", "Modelo", "Cor", TipoVeiculo.CARRO);
        est.registrarEntrada(v);
        
        Ticket t = est.registrarEntrada(v); // Tenta entrar de novo
        
        if (t == null) {
            System.out.println("PASSOU");
        } else {
            System.out.println("FALHOU");
        }
    }

    private static void testarSaidaVeiculoInexistente() {
        System.out.print("Teste 4: Saída de Veículo Inexistente... ");
        Estacionamento est = new Estacionamento("Teste", 5, 10.0);
        Ticket t = est.registrarSaida("ZZZ9999");
        
        if (t == null) {
            System.out.println("PASSOU");
        } else {
            System.out.println("FALHOU");
        }
    }

    private static void testarCalculoValores() {
        System.out.print("Teste 5: Cálculo de Valores (Simulado)... ");
        // Como o cálculo depende de LocalDateTime.now(), é difícil testar exato sem mock.
        // Vamos testar a lógica básica de criação e fechamento.
        Estacionamento est = new Estacionamento("Teste", 5, 10.0);
        est.registrarEntrada(new Veiculo("AAA1111", "Modelo", "Cor", TipoVeiculo.CARRO));
        
        // Simula um pequeno delay se possível, ou apenas fecha
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        Ticket t = est.registrarSaida("AAA1111");
        
        // Mínimo de 1 hora cobrada = 10.0 * 1.0 (fator carro) = 10.0
        if (t != null && t.getValorPago() >= 10.0) {
            System.out.println("PASSOU");
        } else {
            System.out.println("FALHOU (Valor: " + (t != null ? t.getValorPago() : "null") + ")");
        }
    }
}
