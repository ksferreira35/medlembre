package br.com.kaiky.medlembre.model;

/**
 * Representa um medicamento com id, nome, dose e horário.
 */
public class Medicamento {

    /** IDs são gerados por um contador estático. */
    private static int contadorId = 1;

    private final int id;
    private final String nome;
    private final String dose;
    private final String horario;
    private boolean tomadoHoje;

    /**
     * Cria um novo medicamento com ID gerado automaticamente.
     *
     * @param nome    nome do medicamento
     * @param dose    dose a ser tomada
     * @param horario horário de ingestão no formato HH:mm
     */
    public Medicamento(String nome, String dose, String horario) {
        this.id = contadorId++;
        this.nome = nome;
        this.dose = dose;
        this.horario = horario;
        this.tomadoHoje = false;
    }

    /**
     * Redefine o contador de IDs. Usado ao carregar dados do disco.
     *
     * @param valor novo valor do contador
     */
    public static void setContadorId(int valor) {
        contadorId = valor;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDose() {
        return dose;
    }

    public String getHorario() {
        return horario;
    }

    public boolean isTomadoHoje() {
        return tomadoHoje;
    }

    public void setTomadoHoje(boolean tomadoHoje) {
        this.tomadoHoje = tomadoHoje;
    }

    @Override
    public String toString() {
        String status = tomadoHoje ? "[✓ Tomado]" : "[  Pendente]";
        return "[#" + id + "] " + status + " " + nome + " | Dose: " + dose + " | Horário: " + horario;
    }
}