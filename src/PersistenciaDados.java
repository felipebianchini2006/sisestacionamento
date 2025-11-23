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
     * @return O caminho absoluto do arquivo gerado.
     * @throws IOException Se ocorrer erro na exportação.
     */
    public static String exportarRelatorio(Estacionamento est, String formato) throws IOException {
        // Cria diretório de relatórios se não existir
        File dirRelatorios = new File("relatorios");
        if (!dirRelatorios.exists()) {
            dirRelatorios.mkdirs();
        }

        String nomeArquivo = "relatorio_" + System.currentTimeMillis();
        String caminhoArquivo = "";
        
        switch (formato.toUpperCase()) {
            case "TXT":
                caminhoArquivo = new File(dirRelatorios, nomeArquivo + ".txt").getPath();
                exportarTXT(est, caminhoArquivo);
                break;
            case "EXCEL":
                caminhoArquivo = new File(dirRelatorios, nomeArquivo + ".csv").getPath();
                exportarCSV(est, caminhoArquivo);
                break;
            case "PDF":
                // Como não temos biblioteca de PDF, exportamos para HTML que pode ser impresso/salvo como PDF
                caminhoArquivo = new File(dirRelatorios, nomeArquivo + ".html").getPath();
                exportarHTML(est, caminhoArquivo);
                break;
            default:
                throw new IllegalArgumentException("Formato não suportado: " + formato);
        }
        return new File(caminhoArquivo).getAbsolutePath();
    }

    private static void exportarHTML(Estacionamento est, String arquivo) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
            out.println("<html><head><title>Relatório de Estacionamento</title>");
            out.println("<style>");
            out.println("body{font-family: Arial, sans-serif; margin: 20px;}");
            out.println("table{width: 100%; border-collapse: collapse; margin-top: 20px;}");
            out.println("th, td{border: 1px solid #ddd; padding: 8px; text-align: left;}");
            out.println("th{background-color: #f2f2f2;}");
            out.println(".header{margin-bottom: 20px; border-bottom: 2px solid #333; padding-bottom: 10px;}");
            out.println("</style>");
            out.println("</head><body>");
            
            out.println("<div class='header'>");
            out.println("<h1>Relatório de Estacionamento: " + est.getNome() + "</h1>");
            out.println("<p><strong>Data de Emissão:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>");
            out.println("</div>");
            
            out.println("<h2>Resumo Operacional</h2>");
            out.println("<ul>");
            out.println("<li><strong>Vagas Totais:</strong> " + est.getVagas().size() + "</li>");
            out.println("<li><strong>Vagas Ocupadas:</strong> " + est.getVagasOcupadas() + "</li>");
            out.println("<li><strong>Vagas Livres:</strong> " + est.getVagasLivres() + "</li>");
            out.println("</ul>");

            out.println("<h2>Histórico de Tickets</h2>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Placa</th><th>Veículo</th><th>Entrada</th><th>Saída</th><th>Valor Pago</th><th>Status</th></tr>");
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Ticket t : est.getTickets()) {
                String saida = (t.getHoraSaida() != null) ? t.getHoraSaida().format(fmt) : "-";
                String status = (t.getHoraSaida() != null) ? "FECHADO" : "ABERTO";
                String valor = String.format("R$ %.2f", t.getValorPago());
                
                out.println("<tr>");
                out.println("<td>" + t.getId() + "</td>");
                out.println("<td>" + t.getVeiculo().getPlaca() + "</td>");
                out.println("<td>" + t.getVeiculo().getModelo() + " (" + t.getVeiculo().getCor() + ")</td>");
                out.println("<td>" + t.getHoraEntrada().format(fmt) + "</td>");
                out.println("<td>" + saida + "</td>");
                out.println("<td>" + valor + "</td>");
                out.println("<td>" + status + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            out.println("<div style='margin-top: 30px; font-size: 0.8em; color: #666;'>");
            out.println("<p>Sistema de Estacionamento - Gerado automaticamente.</p>");
            out.println("</div>");
            
            out.println("</body></html>");
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
