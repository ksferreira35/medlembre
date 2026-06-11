package br.com.kaiky.medlembre.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.kaiky.medlembre.config.AppConfig;
import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Teste de integração opcional com Supabase real.
 */
class SupabaseMedicamentoRepositoryIntegrationTest {

    @Test
    @DisplayName("Deve criar, ler, atualizar e remover medicamento no Supabase")
    void devePersistirMedicamentoNoSupabase() {
        assumeTrue(AppConfig.supabaseHabilitado(), "Supabase não habilitado");
        assumeTrue(AppConfig.valor("SUPABASE_URL").isPresent(), "SUPABASE_URL ausente");
        assumeTrue(AppConfig.valor("SUPABASE_ANON_KEY").isPresent(), "SUPABASE_ANON_KEY ausente");

        SupabaseMedicamentoRepository repository = new SupabaseMedicamentoRepository(
            AppConfig.obrigatorio("SUPABASE_URL"),
            AppConfig.obrigatorio("SUPABASE_ANON_KEY")
        );

        Medicamento salvo = repository.cadastrar(
            new Medicamento("Teste Supabase " + System.currentTimeMillis(), "1 cp", "08:00")
        );

        try {
            List<Medicamento> medicamentos = repository.carregar();
            assertTrue(contemMedicamento(medicamentos, salvo.getId(), false));

            repository.marcarComoTomado(salvo.getId());
            assertTrue(contemMedicamento(repository.carregar(), salvo.getId(), true));
        } finally {
            repository.removerPorId(salvo.getId());
        }

        assertFalse(contemMedicamento(repository.carregar(), salvo.getId(), true));
    }

    private boolean contemMedicamento(List<Medicamento> medicamentos, int id, boolean tomadoHoje) {
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getId() == id && medicamento.isTomadoHoje() == tomadoHoje) {
                return true;
            }
        }
        return false;
    }
}
