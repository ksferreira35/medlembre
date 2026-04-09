package br.com.kaiky.medlembre.ui;

import java.util.List;
import java.util.Scanner;

import br.com.kaiky.medlembre.model.Medicamento;
import br.com.kaiky.medlembre.service.MedicamentoService;

/**
 * Interface de linha de comando do MedLembre.
 */
public class MenuCLI {

    private final MedicamentoService service;
    private final Scanner scanner;

    /**
     * Cria uma nova instância do menu CLI,
     * inicializando o serviço de medicamentos e o leitor de entrada.
     */
    public MenuCLI() {
        this.service = new MedicamentoService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia menu principal da aplicação,
     * exibindo o cabeçalho e processando as opções do menu até o usuário sair.
     */
    public void iniciar() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     💊 MedLembre v1.0.0          ║");
        System.out.println("║  Controle de Medicamentos        ║");
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

    /** Exibe as opções do menu principal. */
    private void exibirMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1 - Cadastrar medicamento");
        System.out.println("2 - Listar medicamentos");
        System.out.println("3 - Marcar como tomado hoje");
        System.out.println("4 - Remover medicamento");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    /**
     * Solicita os dados e cadastra um novo medicamento.
     * Aceita horário no formato H:mm ou HH:mm
     */
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

    /** Lista todos os medicamentos cadastrados com seus IDs. */
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

    /**
     * Solicita o ID ou nome do medicamento e o marca como tomado hoje.
     */
    private void marcarComoTomado() {
        List<Medicamento> lista = service.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum medicamento cadastrado.");
            return;
        }

        System.out.println("\n--- MEDICAMENTOS ---");
        for (Medicamento m : lista) {
            System.out.println(m);
        }

        System.out.print("\nDigite o ID ou nome do medicamento tomado: ");
        String entrada = scanner.nextLine();

        boolean sucesso = service.marcarComoTomado(entrada);
        if (sucesso) {
            System.out.println("Marcado como tomado!");
        } else {
            System.out.println("Medicamento não encontrado.");
        }
    }

    /**
     * Lista os medicamentos, solicita o ID ou nome e pede confirmação antes de remover.
     */
    private void removerMedicamento() {
        List<Medicamento> lista = service.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum medicamento cadastrado.");
            return;
        }

        System.out.println("\n--- MEDICAMENTOS ---");
        for (Medicamento m : lista) {
            System.out.println(m);
        }

        System.out.print("\nDigite o ID ou nome do medicamento a remover: ");
        String entrada = scanner.nextLine().trim();

        System.out.print("Tem certeza que deseja remover \"" + entrada + "\"? (s/n): ");
        String confirmacao = scanner.nextLine().trim().toLowerCase();

        if (!confirmacao.equals("s")) {
            System.out.println("Remoção cancelada.");
            return;
        }

        boolean removido = service.remover(entrada);
        if (removido) {
            System.out.println("Medicamento removido.");
        } else {
            System.out.println("Medicamento não encontrado.");
        }
    }

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        new MenuCLI().iniciar();
    }
}