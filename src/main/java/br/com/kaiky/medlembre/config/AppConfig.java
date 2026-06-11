package br.com.kaiky.medlembre.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Configurações da aplicação carregadas do ambiente ou do arquivo .env.
 */
public final class AppConfig {

    private static final Map<String, String> DOT_ENV = carregarDotEnv();

    private AppConfig() {
    }

    /**
     * Verifica se o armazenamento remoto via Supabase está habilitado.
     *
     * @return true quando MEDLEMBRE_STORAGE=supabase
     */
    public static boolean supabaseHabilitado() {
        return "supabase".equalsIgnoreCase(valor("MEDLEMBRE_STORAGE").orElse(""));
    }

    /**
     * Retorna uma configuração obrigatória.
     *
     * @param chave nome da variável
     * @return valor configurado
     * @throws IllegalStateException se a variável não existir
     */
    public static String obrigatorio(String chave) {
        return valor(chave)
            .filter(v -> !v.isBlank())
            .orElseThrow(() -> new IllegalStateException("Configuração obrigatória ausente: " + chave));
    }

    /**
     * Busca uma variável no ambiente do sistema e, se não existir, no arquivo .env.
     *
     * @param chave nome da variável
     * @return valor encontrado
     */
    public static Optional<String> valor(String chave) {
        String ambiente = System.getenv(chave);
        if (ambiente != null && !ambiente.isBlank()) {
            return Optional.of(ambiente.trim());
        }
        return Optional.ofNullable(DOT_ENV.get(chave)).filter(v -> !v.isBlank());
    }

    private static Map<String, String> carregarDotEnv() {
        Path caminho = Path.of(".env");
        if (!Files.exists(caminho)) {
            return Map.of();
        }

        try {
            List<String> linhas = Files.readAllLines(caminho);
            Map<String, String> valores = new HashMap<>();
            for (String linha : linhas) {
                adicionarLinhaEnv(valores, linha);
            }
            return valores;
        } catch (IOException e) {
            return Map.of();
        }
    }

    private static void adicionarLinhaEnv(Map<String, String> valores, String linha) {
        String normalizada = linha.trim();
        if (normalizada.isEmpty() || normalizada.startsWith("#") || !normalizada.contains("=")) {
            return;
        }

        int separador = normalizada.indexOf('=');
        String chave = normalizada.substring(0, separador).trim();
        String valor = normalizada.substring(separador + 1).trim();
        valores.put(chave, removerAspas(valor));
    }

    private static String removerAspas(String valor) {
        if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
            return valor.substring(1, valor.length() - 1);
        }
        if (valor.length() >= 2 && valor.startsWith("'") && valor.endsWith("'")) {
            return valor.substring(1, valor.length() - 1);
        }
        return valor;
    }
}
