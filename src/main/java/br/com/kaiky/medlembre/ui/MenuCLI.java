package br.com.kaiky.medlembre.ui;

import java.util.List;
import java.util.Scanner;

import br.com.kaiky.medlembre.model.Medicamento;
import br.com.kaiky.medlembre.service.MedicamentoService;

public class MenuCLI {

    private final MedicamentoService service;
    private final Scanner scanner;

    public MenuCLI() {
        this.service = new MedicamentoService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   💊 MedLembre v1.0-SNAPSHOT     ║");
        System.out.println("║      Controle de Medicamentos    ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> cadastrarMedicamento();
                case "2" -> listarMedicamentos();
                case "3" -> marcarComoTomado();
                case "4" -> removerMedicamento();
                case "0" -> {
                    System.out.println("\nAté logo! Cuide-se.");
                    rodando = false;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1 - Cadastrar medicamento");
        System.out.println("2 - Listar medicamentos");
        System.out.println("3 - Marcar como tomado hoje");
        System.out.println("4 - Remover medicamento");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private void cadastrarMedicamento() {
        System.out.print("Nome do medicamento: ");
        String nome = scanner.nextLine();
        System.out.print("Dose (ex: 1 comprimido): ");
        String dose = scanner.nextLine();
        System.out.print("Horário (ex: 08:00): ");
        String horario = scanner.nextLine();

        try {
            service.cadastrar(nome, dose, horario);
            System.out.println("Medicamento cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarMedicamentos() {
        List<Medicamento> lista = service.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum medicamento cadastrado.");
            return;
        }
        System.out.println("\n--- MEDICAMENTOS ---");
        for (Medicamento m : lista) {
            System.out.println(m);
        }
    }

    private void marcarComoTomado() {
        System.out.print("Nome do medicamento tomado: ");
        String nome = scanner.nextLine();
        boolean sucesso = service.marcarComoTomado(nome);
        if (sucesso) {
            System.out.println("Marcado como tomado!");
        } else {
            System.out.println("Medicamento não encontrado.");
        }
    }

    private void removerMedicamento() {
        System.out.print("Nome do medicamento a remover: ");
        String nome = scanner.nextLine();
        boolean removido = service.remover(nome);
        if (removido) {
            System.out.println("Medicamento removido.");
        } else {
            System.out.println("Medicamento não encontrado.");
        }
    }

    public static void main(String[] args) {
        new MenuCLI().iniciar();
    }
}
