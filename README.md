# Sistema de Estacionamento

Sistema em Java para gerenciamento de estacionamento com controle de vagas, tickets, tarifas diferenciadas por tipo de veículo e relatórios financeiros.

## 🚀 Como Executar

1. **Compile**:
   ```bash
   cd src
   javac *.java
   ```
2. **Execute**:
   ```bash
   java Menu
   ```
3. **Testes**:
   ```bash
   java TesteSistema
   ```

## 📋 Funcionalidades Principais
- Entrada e saída de veículos (Carro, Moto, Caminhão).
- Cálculo automático de valor por tempo.
- Gestão de vagas (incluindo VIP).
- Relatórios gerenciais e financeiros.
- Interface via console com cores.

## 📂 Estrutura
- `Menu.java`: Interface (CLI).
- `Estacionamento.java`: Regras de negócio.
- `Veiculo.java`, `Ticket.java`, `Vaga.java`: Modelos.
- `TesteSistema.java`: Testes automatizados.
