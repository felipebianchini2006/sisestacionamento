/**
 * Enumeração que define os tipos de veículos aceitos no estacionamento.
 * Cada tipo possui um fator multiplicador para o cálculo do valor do estacionamento.
 */
public enum TipoVeiculo {
    /** Motocicletas (Fator 0.5) */
    MOTO(0.5), 
    /** Carros de passeio (Fator 1.0) */
    CARRO(1.0), 
    /** Caminhões e veículos pesados (Fator 2.0) */
    CAMINHAO(2.0);
    
    private final double fator;
    
    /**
     * Construtor do enum.
     * @param fator Fator multiplicador de preço para o tipo de veículo.
     */
    TipoVeiculo(double fator) {
        this.fator = fator;
    }
    
    /**
     * Obtém o fator multiplicador do tipo de veículo.
     * @return O valor do fator.
     */
    public double getFator() {
        return fator;
    }
}
