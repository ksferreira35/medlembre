package br.com.kaiky.medlembre.model;

public class Medicamento {

    private final String nome;
    private final String dose;
    private final String horario;
    private boolean tomadoHoje;

    public Medicamento(String nome, String dose, String horario) {
        this.nome = nome;
        this.dose = dose;
        this.horario = horario;
        this.tomadoHoje = false;
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
        return status + " " + nome + " | Dose: " + dose + " | Horário: " + horario;
    }
}
