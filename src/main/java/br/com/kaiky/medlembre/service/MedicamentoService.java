package br.com.kaiky.medlembre.service;

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

public class MedicamentoService {

    private static final String ARQUIVO = "medicamentos.json";
    private final List<Medicamento> medicamentos;
    private final Gson gson;

    public MedicamentoService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.medicamentos = carregarDoDisco();
    }

    public void cadastrar(String nome, String dose, String horario) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do medicamento não pode ser vazio.");
        }
        if (dose == null || dose.trim().isEmpty()) {
            throw new IllegalArgumentException("Dose não pode ser vazia.");
        }
        if (horario == null || horario.trim().isEmpty()) {
            throw new IllegalArgumentException("Horário não pode ser vazio.");
        }
        medicamentos.add(new Medicamento(nome.trim(), dose.trim(), horario.trim()));
        salvarNoDisco();
    }

    public boolean remover(String nome) {
        boolean removido = medicamentos.removeIf(
            m -> m.getNome().equalsIgnoreCase(nome.trim())
        );
        if (removido) {
            salvarNoDisco();
        }
        return removido;
    }

    public boolean marcarComoTomado(String nome) {
        for (Medicamento m : medicamentos) {
            if (m.getNome().equalsIgnoreCase(nome.trim())) {
                m.setTomadoHoje(true);
                salvarNoDisco();
                return true;
            }
        }
        return false;
    }

    public List<Medicamento> listar() {
        return new ArrayList<>(medicamentos);
    }

    public int quantidade() {
        return medicamentos.size();
    }

    private void salvarNoDisco() {
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            gson.toJson(medicamentos, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private List<Medicamento> carregarDoDisco() {
        try (FileReader reader = new FileReader(ARQUIVO)) {
            Type tipo = new TypeToken<List<Medicamento>>() {}.getType();
            List<Medicamento> lista = gson.fromJson(reader, tipo);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
