package br.com.kaiky.medlembre.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Testes automatizados para MedicamentoService.
 */
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
    @DisplayName("Deve normalizar horário sem zero à esquerda")
    void deveNormalizarHorarioSemZero() {
        service.cadastrar("Losartana", "1 comprimido", "8:00");
        assertEquals("08:00", service.listar().get(0).getHorario());
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
    @DisplayName("Não deve cadastrar medicamento com horário em formato inválido")
    void naoDeveCadastrarComHorarioInvalido() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.cadastrar("Losartana", "1 comprimido", "25:99")
        );
        assertEquals("Horário inválido. Use o formato HH:mm (ex: 08:00).", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar horário no formato correto")
    void deveValidarHorarioCorreto() {
        assertTrue(MedicamentoService.horarioValido("08:00"));
        assertTrue(MedicamentoService.horarioValido("23:59"));
        assertTrue(MedicamentoService.horarioValido("00:00"));
        assertTrue(MedicamentoService.horarioValido("8:00"));
        assertTrue(MedicamentoService.horarioValido("9:59"));
    }

    @Test
    @DisplayName("Deve rejeitar horário em formato inválido")
    void deveRejeitarHorarioInvalido() {
        assertFalse(MedicamentoService.horarioValido("25:00"));
        assertFalse(MedicamentoService.horarioValido("08:60"));
        assertFalse(MedicamentoService.horarioValido("abcd"));
        assertFalse(MedicamentoService.horarioValido(null));
    }

    @Test
    @DisplayName("Deve remover medicamento pelo nome")
    void deveRemoverMedicamentoPeloNome() {
        service.cadastrar("Metformina", "2 comprimidos", "12:00");
        boolean removido = service.remover("Metformina");

        assertTrue(removido);
        assertEquals(0, service.quantidade());
    }

    @Test
    @DisplayName("Deve remover medicamento pelo nome em caixa diferente")
    void deveRemoverMedicamentoPeloNomeCaixaDiferente() {
        service.cadastrar("Metformina", "2 comprimidos", "12:00");
        boolean removido = service.remover("metformina");

        assertTrue(removido);
        assertEquals(0, service.quantidade());
    }

    @Test
    @DisplayName("Deve remover medicamento pelo ID")
    void deveRemoverMedicamentoPorId() {
        service.cadastrar("Aspirina", "1 comprimido", "08:00");
        int id = service.listar().get(0).getId();

        boolean removido = service.remover(String.valueOf(id));

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
    @DisplayName("Deve retornar false ao remover por ID inexistente")
    void deveRetornarFalseAoRemoverPorIdInexistente() {
        boolean removido = service.removerPorId(9999);
        assertFalse(removido);
    }

    @Test
    @DisplayName("Deve atribuir ID único a cada medicamento")
    void deveAtribuirIdUnico() {
        service.cadastrar("Remédio A", "1 cp", "08:00");
        service.cadastrar("Remédio B", "2 cp", "12:00");

        List<Medicamento> lista = service.listar();
        assertNotEquals(lista.get(0).getId(), lista.get(1).getId());
    }

    @Test
    @DisplayName("Deve marcar medicamento como tomado pelo nome")
    void deveMarcarComoTomadoPeloNome() {
        service.cadastrar("Aspirina", "1 comprimido", "20:00");
        boolean marcado = service.marcarComoTomado("Aspirina");

        assertTrue(marcado);
        assertTrue(service.listar().get(0).isTomadoHoje());
    }

    @Test
    @DisplayName("Deve marcar medicamento como tomado pelo nome em caixa diferente")
    void deveMarcarComoTomadoPeloNomeCaixaDiferente() {
        service.cadastrar("Aspirina", "1 comprimido", "20:00");
        boolean marcado = service.marcarComoTomado("ASPIRINA");

        assertTrue(marcado);
        assertTrue(service.listar().get(0).isTomadoHoje());
    }

    @Test
    @DisplayName("Deve marcar medicamento como tomado pelo ID")
    void deveMarcarComoTomadoPorId() {
        service.cadastrar("Aspirina", "1 comprimido", "20:00");
        int id = service.listar().get(0).getId();
        boolean marcado = service.marcarComoTomado(String.valueOf(id));

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