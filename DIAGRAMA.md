# Diagrama de Classes - Sistema de Estacionamento

```mermaid
classDiagram
    class Veiculo {
        -String placa
        -String modelo
        -String cor
        +Veiculo(String placa, String modelo, String cor)
        +getPlaca() String
        +getModelo() String
        +getCor() String
    }

    class Ticket {
        -int id
        -Veiculo veiculo
        -LocalDateTime horaEntrada
        -LocalDateTime horaSaida
        -double valorPago
        +Ticket(int id, Veiculo veiculo)
        +calcularValor(double valorHora) void
        +registrarSaida() void
        +getValorPago() double
    }

    class Vaga {
        -int numero
        -boolean ocupada
        -Veiculo veiculo
        +Vaga(int numero)
        +estacionar(Veiculo veiculo) boolean
        +liberar() Veiculo
        +isOcupada() boolean
        +getVeiculo() Veiculo
    }

    class Estacionamento {
        -String nome
        -List~Vaga~ vagas
        -List~Ticket~ tickets
        -double valorHora
        +Estacionamento(String nome, double valorHora, int quantidadeVagas)
        +registrarEntrada(Veiculo veiculo) Ticket
        +registrarSaida(String placa) double
        +consultarVagasDisponiveis() int
        -buscarVagaLivre() Vaga
    }

    %% Relacionamentos
    Estacionamento "1" *-- "*" Vaga : compõe
    Estacionamento "1" o-- "*" Ticket : armazena
    Vaga "0..1" --> "0..1" Veiculo : tem
    Ticket "*" --> "1" Veiculo : referente a
```

## Detalhes da Implementação

### Relacionamentos
1. **Estacionamento -> Vaga**: Composição (*--). O estacionamento é composto por vagas. Se o estacionamento deixar de existir, as vagas também deixam.
2. **Estacionamento -> Ticket**: Agregação (o--). O estacionamento mantém um histórico de tickets.
3. **Vaga -> Veiculo**: Associação simples. Uma vaga pode ter um veículo ou estar vazia (0..1).
4. **Ticket -> Veiculo**: Associação unidirecional. O ticket precisa saber a qual veículo se refere.

### Encapsulamento
- Todos os atributos definidos como privados (`-`).
- Métodos de acesso e operações principais definidos como públicos (`+`).
