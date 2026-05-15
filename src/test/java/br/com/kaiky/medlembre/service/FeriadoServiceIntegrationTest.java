package br.com.kaiky.medlembre.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes de integração para FeriadoService.
 * Esses testes fazem requisições reais à BrasilAPI.
 */
class FeriadoServiceIntegrationTest {

    private final FeriadoService service = new FeriadoService();

    @Test
    @DisplayName("Deve retornar nome do feriado para o Natal")
    void deveRetornarNomeDoFeriadoParaONatal() throws Exception {
        String resultado = service.verificarFeriado("2026-12-25");
        assertNotNull(resultado, "Natal deve ser reconhecido como feriado");
        assertTrue(resultado.toLowerCase().contains("natal"),
            "Nome do feriado deve conter 'natal', mas foi: " + resultado);
    }

    @Test
    @DisplayName("Deve retornar null para um dia comum")
    void deveRetornarNullParaDiaComum() throws Exception {
        String resultado = service.verificarFeriado("2026-03-10");
        assertNull(resultado, "10 de março não é feriado nacional");
    }

    @Test
    @DisplayName("Deve retornar nome do feriado para o Ano Novo")
    void deveRetornarNomeDoFeriadoParaAnoNovo() throws Exception {
        String resultado = service.verificarFeriado("2026-01-01");
        assertNotNull(resultado, "Ano Novo deve ser reconhecido como feriado");
    }

    @Test
    @DisplayName("Deve executar verificarHoje sem lançar exceção")
    void deveExecutarVerificarHojeSemExcecao() {
        assertDoesNotThrow(() -> service.verificarHoje(),
            "verificarHoje não deve lançar exceção mesmo sem feriado hoje");
    }

    @Test
    @DisplayName("Deve retornar lista com feriados do ano")
    void deveRetornarListaComFeriadosDoAno() throws Exception {
        List<String> feriados = service.listarFeriadosDoAno("2026");
        assertNotNull(feriados, "Lista não deve ser nula");
        assertFalse(feriados.isEmpty(), "Lista de feriados não deve ser vazia");
    }

    @Test
    @DisplayName("Deve retornar feriados no formato dd/MM — Nome")
    void deveRetornarFeriadosNoFormatoCorreto() throws Exception {
        List<String> feriados = service.listarFeriadosDoAno("2026");
        assertFalse(feriados.isEmpty());
        for (String f : feriados) {
            assertTrue(f.matches("\\d{2}/\\d{2} — .+"),
                "Feriado fora do formato esperado: " + f);
        }
    }

    @Test
    @DisplayName("Deve conter Natal na lista de feriados de 2026")
    void deveConterNatalNaListaDe2026() throws Exception {
        List<String> feriados = service.listarFeriadosDoAno("2026");
        boolean contemNatal = feriados.stream()
            .anyMatch(f -> f.contains("25/12") && f.toLowerCase().contains("natal"));
        assertTrue(contemNatal, "Lista deve conter o Natal em 25/12");
    }
}