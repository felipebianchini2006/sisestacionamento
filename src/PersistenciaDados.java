import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PersistenciaDados {

    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = builder.create();
    }

    /**
     * Salva o estado atual do estacionamento em um arquivo JSON.
     * @param est O objeto Estacionamento a ser salvo.
     * @param arquivo O caminho do arquivo de destino.
     * @throws IOException Se ocorrer erro na escrita.
     */
    public static void salvarEstado(Estacionamento est, String arquivo) throws IOException {
        try (Writer writer = new FileWriter(arquivo)) {
            gson.toJson(est, writer);
        } catch (IOException e) {
            throw new IOException("Erro ao salvar dados: " + e.getMessage(), e);
        }
    }

    /**
     * Carrega o estado do estacionamento a partir de um arquivo JSON.
     * @param arquivo O caminho do arquivo de origem.
     * @return Um novo objeto Estacionamento com os dados carregados.
     * @throws IOException Se ocorrer erro na leitura ou o arquivo não existir.
     */
    public static Estacionamento carregarEstado(String arquivo) throws IOException {
        File f = new File(arquivo);
        if (!f.exists()) {
            throw new FileNotFoundException("Arquivo de dados não encontrado: " + arquivo);
        }

        try (Reader reader = new FileReader(arquivo)) {
            // O Gson vai tentar instanciar Estacionamento. 
            // Como Estacionamento não tem construtor padrão, o Gson usa UnsafeAllocator se disponível.
            // Caso contrário, pode falhar. Se falhar, precisaríamos de um InstanceCreator.
            // Mas geralmente funciona em JVMs padrão.
            return gson.fromJson(reader, Estacionamento.class);
        } catch (Exception e) {
            throw new IOException("Erro ao carregar dados (formato inválido ou corrompido): " + e.getMessage(), e);
        }
    }

    /**
     * Exporta um relatório do estacionamento para o formato especificado.
     * @param est O estacionamento.
     * @param formato O formato desejado ("TXT", "EXCEL", "PDF").
     * @throws IOException Se ocorrer erro na exportação.
     */
    public static void exportarRelatorio(Estacionamento est, String formato) throws IOException {
        String nomeArquivo = "relatorio_" + System.currentTimeMillis();
        
        switch (formato.toUpperCase()) {
            case "TXT":
                exportarTXT(est, nomeArquivo + ".txt");
                break;
            case "EXCEL":
                exportarCSV(est, nomeArquivo + ".csv");
                break;
            case "PDF":
                throw new IOException("Exportação para PDF requer biblioteca externa (ex: iText) não configurada.");
            default:
                throw new IllegalArgumentException("Formato não suportado: " + formato);
        }
    }

    private static void exportarTXT(Estacionamento est, String arquivo) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
            out.println("=== RELATÓRIO DE ESTACIONAMENTO ===");
            out.println("Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            out.println("Estacionamento: " + est.getNome());
            out.println("-----------------------------------");
            out.println("Vagas Totais: " + est.getVagas().size());
            out.println("Vagas Ocupadas: " + est.getVagasOcupadas());
            out.println("Vagas Livres: " + est.getVagasLivres());
            out.println("-----------------------------------");
            out.println("LISTA DE TICKETS:");
            for (Ticket t : est.getTickets()) {
                out.println(t.toString());
            }
            out.println("-----------------------------------");
            out.println("Fim do Relatório");
        }
    }

    private static void exportarCSV(Estacionamento est, String arquivo) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
            // Header compatível com Excel
            out.println("ID;Placa;Modelo;Cor;Tipo;Entrada;Saida;Valor Pago;Status");
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            for (Ticket t : est.getTickets()) {
                String saida = (t.getHoraSaida() != null) ? t.getHoraSaida().format(fmt) : "";
                String status = (t.getHoraSaida() != null) ? "FECHADO" : "ABERTO";
                String valor = String.format("%.2f", t.getValorPago()).replace(".", ","); // Excel BR usa vírgula
                
                out.printf("%d;%s;%s;%s;%s;%s;%s;%s;%s%n",
                    t.getId(),
                    t.getVeiculo().getPlaca(),
                    t.getVeiculo().getModelo(),
                    t.getVeiculo().getCor(),
                    t.getVeiculo().getTipo(),
                    t.getHoraEntrada().format(fmt),
                    saida,
                    valor,
                    status
                );
            }
        }
    }

    // Adaptador para LocalDateTime (Gson não suporta nativamente Java 8 Time por padrão em versões antigas)
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), FORMATTER);
        }
    }
}
