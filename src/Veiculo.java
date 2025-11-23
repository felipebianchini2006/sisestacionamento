public class Veiculo {
    private String placa;
    private String modelo;
    private String cor;

    public Veiculo(String placa, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa não pode ser vazia ou nula.");
        }
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa não pode ser vazia ou nula.");
        }
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    @Override
    public String toString() {
        return String.format("Veículo [Placa: %s, Modelo: %s, Cor: %s]", placa, modelo, cor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return placa.equals(veiculo.placa);
    }

    @Override
    public int hashCode() {
        return placa.hashCode();
    }
}
