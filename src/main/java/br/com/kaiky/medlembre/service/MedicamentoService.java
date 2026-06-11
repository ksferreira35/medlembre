package br.com.kaiky.medlembre.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.kaiky.medlembre.config.AppConfig;
import br.com.kaiky.medlembre.model.Medicamento;
import br.com.kaiky.medlembre.repository.JsonMedicamentoRepository;
import br.com.kaiky.medlembre.repository.MedicamentoRepository;
import br.com.kaiky.medlembre.repository.SupabaseMedicamentoRepository;

/**
 * Serviço responsável pelo gerenciamento de medicamentos.
 * Realiza operações de cadastro, remoção, listagem e persistência.
 */
public class MedicamentoService {

    /** Padrão de validação de horário no formato HH:mm. */
    private static final Pattern PADRAO_HORARIO = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private final List<Medicamento> medicamentos;
    private final MedicamentoRepository repository;

    /**
     * O serviço inicializa já tendo os medicamentos carregados no disco.
     */
    public MedicamentoService() {
        this(criarRepositoryPadrao());
    }

    /**
     * Inicializa o serviço com um repositório específico.
     *
     * @param repository repositório de persistência
     */
    public MedicamentoService(MedicamentoRepository repository) {
        this.repository = repository;
        this.medicamentos = repository.carregar();
        ajustarContadorId();
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

        Medicamento medicamento = new Medicamento(nome.trim(), dose.trim(), horarioNormalizado);
        Medicamento medicamentoPersistido = repository.cadastrar(medicamento);
        medicamentos.add(medicamentoPersistido);
        ajustarContadorId();
    }

    /**
     * Remove um medicamento pelo ID ou pelo nome.
     *
     * @param entrada ID numérico ou nome do medicamento
     * @return true se removido com sucesso, false se não encontrado
     */
    public boolean remover(String entrada) {
        String entradaNormalizada = entrada.trim();
        Medicamento medicamento = buscarPorEntrada(entradaNormalizada);

        if (medicamento == null) {
            return false;
        }

        repository.removerPorId(medicamento.getId());
        medicamentos.removeIf(m -> m.getId() == medicamento.getId());
        return true;
    }

    /**
     * Remove um medicamento pelo ID.
     *
     * @param id identificador do medicamento
     * @return true se removido com sucesso, false se não encontrado
     */
    public boolean removerPorId(int id) {
        Medicamento medicamento = buscarPorId(id);
        if (medicamento == null) {
            return false;
        }

        repository.removerPorId(id);
        medicamentos.removeIf(m -> m.getId() == id);
        return true;
    }

    /**
     * Marca um medicamento como tomado hoje pelo ID ou pelo nome.
     *
     * @param entrada ID numérico ou nome do medicamento
     * @return true se marcado com sucesso, false se não encontrado
     */
    public boolean marcarComoTomado(String entrada) {
        String entradaNormalizada = entrada.trim();
        Medicamento medicamento = buscarPorEntrada(entradaNormalizada);

        if (medicamento == null) {
            return false;
        }

        repository.marcarComoTomado(medicamento.getId());
        medicamento.setTomadoHoje(true);
        return true;
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

    private void ajustarContadorId() {
        int maiorId = medicamentos.stream().mapToInt(Medicamento::getId).max().orElse(0);
        Medicamento.setContadorId(maiorId + 1);
    }

    private Medicamento buscarPorEntrada(String entrada) {
        try {
            return buscarPorId(Integer.parseInt(entrada));
        } catch (NumberFormatException e) {
            for (Medicamento medicamento : medicamentos) {
                if (medicamento.getNome().equalsIgnoreCase(entrada)) {
                    return medicamento;
                }
            }
            return null;
        }
    }

    private Medicamento buscarPorId(int id) {
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getId() == id) {
                return medicamento;
            }
        }
        return null;
    }

    private static MedicamentoRepository criarRepositoryPadrao() {
        if (AppConfig.supabaseHabilitado()) {
            return new SupabaseMedicamentoRepository(
                AppConfig.obrigatorio("SUPABASE_URL"),
                AppConfig.obrigatorio("SUPABASE_ANON_KEY")
            );
        }
        return new JsonMedicamentoRepository();
    }
}
