package br.com.kaiky.medlembre.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Serviço responsável pelo gerenciamento de medicamentos.
 * Realiza operações de cadastro, remoção, listagem e mantem salvo em JSON.
 */
public class MedicamentoService {

    private static final String ARQUIVO = "medicamentos.json";

    /** Padrão de validação de horário no formato HH:mm. */
    private static final Pattern PADRAO_HORARIO = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private final List<Medicamento> medicamentos;
    private final Gson gson;

    /**
     * O serviço inicializa já tendo os medicamentos carregados no disco.
     */
    public MedicamentoService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.medicamentos = carregarDoDisco();
    }

    /**
     * Cadastra um novo medicamento após validar os campos.
     * Aceita horário no formato H:mm ou HH:mm, completando zero à esquerda se necessário.
     *
     * @param nome    nome do medicamento
     * @param dose    dose a ser tomada
     * @param horario horário no formato H:mm ou HH:mm
     * @throws IllegalArgumentException se algum campo for inválido
     */
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

        // Normaliza H:mm para HH:mm (ex: 8:00 → 08:00)
        String horarioNormalizado = normalizarHorario(horario.trim());

        if (!PADRAO_HORARIO.matcher(horarioNormalizado).matches()) {
            throw new IllegalArgumentException("Horário inválido. Use o formato HH:mm (ex: 08:00).");
        }

        medicamentos.add(new Medicamento(nome.trim(), dose.trim(), horarioNormalizado));
        salvarNoDisco();
    }

    /**
     * Remove um medicamento pelo ID ou pelo nome.
     *
     * @param entrada ID numérico ou nome do medicamento
     * @return true se removido com sucesso, false se não encontrado
     */
    public boolean remover(String entrada) {
        String entradaNormalizada = entrada.trim();
        boolean removido;

        try {
            int id = Integer.parseInt(entradaNormalizada);
            removido = medicamentos.removeIf(m -> m.getId() == id);
        } catch (NumberFormatException e) {
            removido = medicamentos.removeIf(
                m -> m.getNome().equalsIgnoreCase(entradaNormalizada)
            );
        }

        if (removido) {
            salvarNoDisco();
        }
        return removido;
    }

    /**
     * Remove um medicamento pelo ID.
     *
     * @param id identificador do medicamento
     * @return true se removido com sucesso, false se não encontrado
     */
    public boolean removerPorId(int id) {
        boolean removido = medicamentos.removeIf(m -> m.getId() == id);
        if (removido) {
            salvarNoDisco();
        }
        return removido;
    }

    /**
     * Marca um medicamento como tomado hoje pelo ID ou pelo nome.
     *
     * @param entrada ID numérico ou nome do medicamento
     * @return true se marcado com sucesso, false se não encontrado
     */
    public boolean marcarComoTomado(String entrada) {
        String entradaNormalizada = entrada.trim();

        try {
            int id = Integer.parseInt(entradaNormalizada);
            for (Medicamento m : medicamentos) {
                if (m.getId() == id) {
                    m.setTomadoHoje(true);
                    salvarNoDisco();
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            for (Medicamento m : medicamentos) {
                if (m.getNome().equalsIgnoreCase(entradaNormalizada)) {
                    m.setTomadoHoje(true);
                    salvarNoDisco();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Retorna uma cópia da lista de medicamentos cadastrados.
     *
     * @return lista de medicamentos
     */
    public List<Medicamento> listar() {
        return new ArrayList<>(medicamentos);
    }

    /**
     * Retorna a quantidade de medicamentos cadastrados.
     *
     * @return número de medicamentos
     */
    public int quantidade() {
        return medicamentos.size();
    }

    /**
     * Valida se o horário informado está no formato HH:mm ou H:mm.
     *
     * @param horario horário a validar
     * @return true se válido, false caso contrário
     */
    public static boolean horarioValido(String horario) {
        if (horario == null) {
            return false;
        }
        String normalizado = normalizarHorario(horario.trim());
        return PADRAO_HORARIO.matcher(normalizado).matches();
    }

    /**
     * Normaliza horário no formato H:mm para HH:mm.
     * Exemplo: "8:00" → "08:00".
     *
     * @param horario horário a normalizar
     * @return horário normalizado
     */
    private static String normalizarHorario(String horario) {
        if (horario.matches("^\\d:[0-5]\\d$")) {
            return "0" + horario;
        }
        return horario;
    }

    /** A lista de medicamentos fica salvo no arquivo JSON. */
    private void salvarNoDisco() {
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            gson.toJson(medicamentos, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    /**
     * Carrega os medicamentos do arquivo JSON e ajusta o contador de IDs.
     *
     * @return lista carregada ou lista vazia se o arquivo não existir
     */
    private List<Medicamento> carregarDoDisco() {
        try (FileReader reader = new FileReader(ARQUIVO)) {
            Type tipo = new TypeToken<List<Medicamento>>() {}.getType();
            List<Medicamento> lista = gson.fromJson(reader, tipo);
            if (lista != null && !lista.isEmpty()) {
                int maiorId = lista.stream().mapToInt(Medicamento::getId).max().orElse(0);
                Medicamento.setContadorId(maiorId + 1);
                return lista;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}