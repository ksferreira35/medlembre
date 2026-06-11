package br.com.kaiky.medlembre.repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Persistência de medicamentos na API REST do Supabase.
 */
public class SupabaseMedicamentoRepository implements MedicamentoRepository {

    private static final String TABELA = "medicamentos";

    private final String endpoint;
    private final String apiKey;
    private final HttpClient httpClient;

    /**
     * Cria o repositório remoto.
     *
     * @param supabaseUrl URL do projeto Supabase
     * @param apiKey      chave anon/public do projeto
     */
    public SupabaseMedicamentoRepository(String supabaseUrl, String apiKey) {
        this(supabaseUrl, apiKey, HttpClient.newHttpClient());
    }

    /**
     * Cria o repositório remoto com cliente HTTP injetável.
     *
     * @param supabaseUrl URL do projeto Supabase
     * @param apiKey      chave anon/public do projeto
     * @param httpClient  cliente HTTP
     */
    public SupabaseMedicamentoRepository(String supabaseUrl, String apiKey, HttpClient httpClient) {
        this.endpoint = supabaseUrl.replaceAll("/+$", "") + "/rest/v1/" + TABELA;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    @Override
    public List<Medicamento> carregar() {
        HttpRequest request = baseRequest(endpoint + "?select=id,nome,dose,horario,tomado_hoje&order=id.asc")
            .GET()
            .build();

        HttpResponse<String> response = enviar(request);
        validarStatus(response, 200);

        JsonArray json = JsonParser.parseString(response.body()).getAsJsonArray();
        List<Medicamento> medicamentos = new ArrayList<>();
        for (JsonElement item : json) {
            JsonObject objeto = item.getAsJsonObject();
            Medicamento medicamento = new Medicamento(
                objeto.get("id").getAsInt(),
                objeto.get("nome").getAsString(),
                objeto.get("dose").getAsString(),
                objeto.get("horario").getAsString(),
                objeto.get("tomado_hoje").getAsBoolean()
            );
            medicamentos.add(medicamento);
        }
        return medicamentos;
    }

    @Override
    public Medicamento cadastrar(Medicamento medicamento) {
        HttpRequest request = baseRequest(endpoint)
            .header("Prefer", "return=representation")
            .POST(HttpRequest.BodyPublishers.ofString(toJson(medicamento).toString()))
            .build();

        HttpResponse<String> response = enviar(request);
        validarStatus(response, 201);

        JsonObject objeto = JsonParser.parseString(response.body()).getAsJsonArray().get(0).getAsJsonObject();
        return new Medicamento(
            objeto.get("id").getAsInt(),
            objeto.get("nome").getAsString(),
            objeto.get("dose").getAsString(),
            objeto.get("horario").getAsString(),
            objeto.get("tomado_hoje").getAsBoolean()
        );
    }

    @Override
    public void removerPorId(int id) {
        HttpRequest request = baseRequest(endpoint + "?id=eq." + id)
            .DELETE()
            .build();

        validarStatus(enviar(request), 204);
    }

    @Override
    public void marcarComoTomado(int id) {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("tomado_hoje", true);

        HttpRequest request = baseRequest(endpoint + "?id=eq." + id)
            .method("PATCH", HttpRequest.BodyPublishers.ofString(corpo.toString()))
            .build();

        validarStatus(enviar(request), 204);
    }

    private HttpRequest.Builder baseRequest(String uri) {
        return HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("apikey", apiKey)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");
    }

    private JsonObject toJson(Medicamento medicamento) {
        JsonObject objeto = new JsonObject();
        objeto.addProperty("nome", medicamento.getNome());
        objeto.addProperty("dose", medicamento.getDose());
        objeto.addProperty("horario", medicamento.getHorario());
        objeto.addProperty("tomado_hoje", medicamento.isTomadoHoje());
        return objeto;
    }

    private HttpResponse<String> enviar(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("Erro de conexão com o Supabase: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operação com Supabase interrompida.", e);
        }
    }

    private void validarStatus(HttpResponse<String> response, int esperado) {
        if (response.statusCode() != esperado) {
            throw new IllegalStateException(
                "Supabase retornou status " + response.statusCode() + ": " + response.body()
            );
        }
    }

}
