/**
 * Representa um veículo no sistema de estacionamento.
 * Armazena informações básicas como placa, modelo, cor e tipo.
 */
public class Veiculo {
    private String placa;
    private String modelo;
    private String cor;
    private TipoVeiculo tipo;

    /**
     * Construtor da classe Veiculo.
     * 
     * @param placa A placa do veículo (deve seguir formato válido).
     * @param modelo O modelo do veículo.
     * @param cor A cor do veículo.
     * @param tipo O tipo do veículo (Carro, Moto, Caminhão).
     * @throws IllegalArgumentException Se a placa for inválida.
     */
    public Veiculo(String placa, String modelo, String cor, TipoVeiculo tipo) {
        setPlaca(placa);
        this.modelo = modelo;
        this.cor = cor;
        this.tipo = tipo;
    }

    /**
     * Obtém o tipo do veículo.
     * @return O tipo do veículo.
     */
    public TipoVeiculo getTipo() {
        return tipo;
    }

    /**
     * Define o tipo do veículo.
     * @param tipo O novo tipo do veículo.
     */
    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtém a placa do veículo.
     * @return A placa formatada.
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Define a placa do veículo com validação de formato.
     * Aceita formatos ABC1234 ou ABC1D23 (Mercosul).
     * 
     * @param placa A nova placa.
     * @throws IllegalArgumentException Se a placa for nula, vazia ou estiver fora do padrão.
     */
    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa não pode ser vazia ou nula.");
        }
        
        String placaUpper = placa.toUpperCase().trim();
        // Validação básica: 3 letras + 4 números (ABC1234) ou Mercosul (ABC1D23)
        String padraoAntigo = "[A-Z]{3}[0-9]{4}";
        String padraoMercosul = "[A-Z]{3}[0-9][A-Z][0-9]{2}";
        
        if (!placaUpper.matches(padraoAntigo) && !placaUpper.matches(padraoMercosul)) {
             throw new IllegalArgumentException("Placa inválida. Use o formato ABC1234 ou ABC1D23.");
        }
        
        this.placa = placaUpper;
    }

    /**
     * Obtém o modelo do veículo.
     * @return O modelo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define o modelo do veículo.
     * @param modelo O novo modelo.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtém a cor do veículo.
     * @return A cor.
     */
    public String getCor() {
        return cor;
    }

    /**
     * Define a cor do veículo.
     * @param cor A nova cor.
     */
    public void setCor(String cor) {
        this.cor = cor;
    }

    /**
     * Retorna uma representação em string do veículo.
     * @return String formatada com os dados do veículo.
     */
    @Override
    public String toString() {
        return String.format("Veículo [%s - Placa: %s, Modelo: %s, Cor: %s]", 
            tipo != null ? tipo : "N/A", placa, modelo, cor);
    }

    /**
     * Verifica a igualdade entre dois veículos baseando-se na placa.
     * @param o Objeto a ser comparado.
     * @return true se as placas forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return placa.equals(veiculo.placa);
    }

    /**
     * Gera o código hash baseado na placa.
     * @return Código hash.
     */
    @Override
    public int hashCode() {
        return placa.hashCode();
    }
}
