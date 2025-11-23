/**
 * Representa uma vaga física no estacionamento.
 * Pode estar livre ou ocupada por um veículo, e pode ser marcada como VIP.
 */
public class Vaga {
    private int numero;
    private boolean ocupada;
    private boolean vip;
    private Veiculo veiculo;

    /**
     * Construtor da classe Vaga.
     * @param numero O número identificador da vaga.
     */
    public Vaga(int numero) {
        this.numero = numero;
        this.ocupada = false;
        this.vip = false;
        this.veiculo = null;
    }

    /**
     * Define se a vaga é VIP.
     * @param vip true para VIP, false para comum.
     */
    public void setVip(boolean vip) {
        this.vip = vip;
    }

    /**
     * Verifica se a vaga é VIP.
     * @return true se for VIP, false caso contrário.
     */
    public boolean isVip() {
        return vip;
    }

    /**
     * Tenta ocupar a vaga com um veículo.
     * @param v O veículo a ser estacionado.
     * @return true se a vaga foi ocupada com sucesso, false se já estava ocupada.
     */
    public boolean ocuparVaga(Veiculo v) {
        if (this.ocupada) {
            return false;
        }
        this.veiculo = v;
        this.ocupada = true;
        return true;
    }

    /**
     * Libera a vaga, removendo o veículo e marcando como livre.
     */
    public void liberarVaga() {
        this.veiculo = null;
        this.ocupada = false;
    }

    /**
     * Obtém o status textual da vaga.
     * @return "OCUPADA" ou "LIVRE".
     */
    public String getStatus() {
        return this.ocupada ? "OCUPADA" : "LIVRE";
    }

    /**
     * Obtém o número da vaga.
     * @return O número da vaga.
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Verifica se a vaga está ocupada.
     * @return true se ocupada, false se livre.
     */
    public boolean isOcupada() {
        return ocupada;
    }

    /**
     * Obtém o veículo estacionado na vaga, se houver.
     * @return O objeto Veiculo ou null se estiver livre.
     */
    public Veiculo getVeiculo() {
        return veiculo;
    }

    /**
     * Retorna uma representação em string da vaga.
     * Inclui número, status VIP, status de ocupação e placa do veículo (se houver).
     * @return String formatada.
     */
    @Override
    public String toString() {
        String infoVeiculo = (veiculo != null) ? " - " + veiculo.getPlaca() : "";
        String tipoVaga = vip ? " [VIP]" : "";
        return String.format("Vaga %d%s [%s]%s", numero, tipoVaga, getStatus(), infoVeiculo);
    }
}
