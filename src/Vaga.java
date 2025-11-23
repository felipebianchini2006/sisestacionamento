public class Vaga {
    private int numero;
    private boolean ocupada;
    private Veiculo veiculo;

    public Vaga(int numero) {
        this.numero = numero;
        this.ocupada = false;
        this.veiculo = null;
    }

    public boolean ocuparVaga(Veiculo v) {
        if (this.ocupada) {
            return false;
        }
        this.veiculo = v;
        this.ocupada = true;
        return true;
    }

    public void liberarVaga() {
        this.veiculo = null;
        this.ocupada = false;
    }

    public String getStatus() {
        return this.ocupada ? "OCUPADA" : "LIVRE";
    }

    public int getNumero() {
        return numero;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    @Override
    public String toString() {
        String infoVeiculo = (veiculo != null) ? " - " + veiculo.getPlaca() : "";
        return String.format("Vaga %d [%s]%s", numero, getStatus(), infoVeiculo);
    }
}
