package br.com.kaiky.medlembre.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.kaiky.medlembre.model.Medicamento;

class MedicamentoServiceTest {

    private MedicamentoService service;

    @BeforeEach
    void setUp() {
        service = new MedicamentoService();
        for (Medicamento m : service.listar()) {
            service.remover(m.getNome());
        }
    }

    @Test
    @DisplayName("Deve cadastrar medicamento com dados válidos")
    void deveCadastrarMedicamentoValido() {
        service.cadastrar("Losartana", "1 comprimido", "08:00");

        List<Medicamento> lista = service.listar();
        assertEquals(1, lista.size());
        assertEquals("Losartana", lista.get(0).getNome());
        assertEquals("1 comprimido", lista.get(0).getDose());
        assertEquals("08:00", lista.get(0).getHorario());
    }

    @Test
    @DisplayName("Não deve cadastrar medicamento com nome vazio")
    void naoDeveCadastrarComNomeVazio() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.cadastrar("", "1 comprimido", "08:00")
        );
        assertEquals("Nome do medicamento não pode ser vazio.", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar medicamento com dose vazia")
    void naoDeveCadastrarComDoseVazia() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.cadastrar("Losartana", "", "08:00")
        );
    }

    @Test
    @DisplayName("Não deve cadastrar medicamento com horário vazio")
    void naoDeveCadastrarComHorarioVazio() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.cadastrar("Losartana", "1 comprimido", "")
        );
    }

    @Test
    @DisplayName("Deve remover medicamento existente")
    void deveRemoverMedicamentoExistente() {
        service.cadastrar("Metformina", "2 comprimidos", "12:00");
        boolean removido = service.remover("Metformina");

        assertTrue(removido);
        assertEquals(0, service.quantidade());
    }

    @Test
    @DisplayName("Deve retornar false ao remover medicamento inexistente")
    void deveRetornarFalseAoRemoverInexistente() {
        boolean removido = service.remover("MedicamentoQueNaoExiste");
        assertFalse(removido);
    }

    @Test
    @DisplayName("Deve marcar medicamento como tomado")
    void deveMarcarComoTomado() {
        service.cadastrar("Aspirina", "1 comprimido", "20:00");
        boolean marcado = service.marcarComoTomado("Aspirina");

        assertTrue(marcado);
        assertTrue(service.listar().get(0).isTomadoHoje());
    }

    @Test
    @DisplayName("Deve retornar false ao marcar inexistente como tomado")
    void deveRetornarFalseAoMarcarInexistenteComoTomado() {
        boolean marcado = service.marcarComoTomado("MedicamentoFantasma");
        assertFalse(marcado);
    }

    @Test
    @DisplayName("Deve listar múltiplos medicamentos corretamente")
    void deveListarMultiplosMedicamentos() {
        service.cadastrar("Remédio A", "1 cp", "08:00");
        service.cadastrar("Remédio B", "2 cp", "12:00");
        service.cadastrar("Remédio C", "1 cp", "20:00");

        assertEquals(3, service.quantidade());
    }
}
