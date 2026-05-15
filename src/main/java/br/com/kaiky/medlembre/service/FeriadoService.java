package br.com.kaiky.medlembre.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Serviço responsável por verificar feriados nacionais via BrasilAPI.
 */
public class FeriadoService {

    private static final String URL_BASE = "https://brasilapi.com.br/api/feriados/v1/";
    private final HttpClient httpClient;

    /** Inicializa o serviço com um HttpClient padrão. */
    public FeriadoService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Permite injetar um HttpClient customizado (útil para testes).
     *
     * @param httpClient cliente HTTP a ser utilizado
     */
    public FeriadoService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Verifica se a data informada é um feriado nacional.
     *
     * @param data data a verificar no formato yyyy-MM-dd
     * @return nome do feriado se for feriado, ou null se não for
     * @throws IOException          em caso de erro de rede
     * @throws InterruptedException se a requisição for interrompida
     */
    public String verificarFeriado(String data) throws IOException, InterruptedException {
        String ano = data.substring(0, 4);
        JsonArray feriados = buscarFeriados(ano);
        if (feriados == null) {
            return null;
        }
        for (JsonElement elemento : feriados) {
            String dataFeriado = elemento.getAsJsonObject().get("date").getAsString();
            if (dataFeriado.equals(data)) {
                return elemento.getAsJsonObject().get("name").getAsString();
            }
        }
        return null;
    }

    /**
     * Retorna a lista de todos os feriados nacionais do ano informado.
     *
     * @param ano ano no formato yyyy
     * @return lista de strings no formato "dd/MM — Nome do Feriado"
     * @throws IOException          em caso de erro de rede
     * @throws InterruptedException se a requisição for interrompida
     */
    public List<String> listarFeriadosDoAno(String ano) throws IOException, InterruptedException {
        List<String> resultado = new ArrayList<>();
        JsonArray feriados = buscarFeriados(ano);
        if (feriados == null) {
            return resultado;
        }
        DateTimeFormatter entrada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter saida = DateTimeFormatter.ofPattern("dd/MM");
        for (JsonElement elemento : feriados) {
            String data = elemento.getAsJsonObject().get("date").getAsString();
            String nome = elemento.getAsJsonObject().get("name").getAsString();
            String dataFormatada = LocalDate.parse(data, entrada).format(saida);
            resultado.add(dataFormatada + " — " + nome);
        }
        return resultado;
    }

    /**
     * Verifica se hoje é um feriado nacional.
     *
     * @return nome do feriado se for feriado, ou null se não for
     */
    public String verificarHoje() {
        String hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            return verificarFeriado(hoje);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Faz a requisição HTTP e retorna o JSON de feriados do ano.
     *
     * @param ano ano no formato yyyy
     * @return JsonArray com os feriados ou null em caso de erro
     * @throws IOException          em caso de erro de rede
     * @throws InterruptedException se a requisição for interrompida
     */
    private JsonArray buscarFeriados(String ano) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL_BASE + ano))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }

        return JsonParser.parseString(response.body()).getAsJsonArray();
    }
}