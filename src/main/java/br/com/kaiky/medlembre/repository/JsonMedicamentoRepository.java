package br.com.kaiky.medlembre.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Persistência local em arquivo JSON.
 */
public class JsonMedicamentoRepository implements MedicamentoRepository {

    private static final String ARQUIVO = "medicamentos.json";

    private final Gson gson;

    /** Cria o repositório local. */
    public JsonMedicamentoRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public List<Medicamento> carregar() {
        try (FileReader reader = new FileReader(ARQUIVO)) {
            Type tipo = new TypeToken<List<Medicamento>>() {}.getType();
            List<Medicamento> lista = gson.fromJson(reader, tipo);
            if (lista != null) {
                return lista;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Medicamento cadastrar(Medicamento medicamento) {
        List<Medicamento> medicamentos = carregar();
        medicamentos.add(medicamento);
        salvar(medicamentos);
        return medicamento;
    }

    @Override
    public void removerPorId(int id) {
        List<Medicamento> medicamentos = carregar();
        medicamentos.removeIf(m -> m.getId() == id);
        salvar(medicamentos);
    }

    @Override
    public void marcarComoTomado(int id) {
        List<Medicamento> medicamentos = carregar();
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getId() == id) {
                medicamento.setTomadoHoje(true);
            }
        }
        salvar(medicamentos);
    }

    private void salvar(List<Medicamento> medicamentos) {
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            gson.toJson(medicamentos, writer);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao salvar dados localmente: " + e.getMessage(), e);
        }
    }
}
