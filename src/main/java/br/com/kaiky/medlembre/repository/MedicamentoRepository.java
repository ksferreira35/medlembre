package br.com.kaiky.medlembre.repository;

import java.util.List;

import br.com.kaiky.medlembre.model.Medicamento;

/**
 * Contrato de persistência dos medicamentos.
 */
public interface MedicamentoRepository {

    /**
     * Carrega todos os medicamentos persistidos.
     *
     * @return lista de medicamentos
     */
    List<Medicamento> carregar();

    /**
     * Persiste um novo medicamento.
     *
     * @param medicamento medicamento a persistir
     * @return medicamento persistido
     */
    Medicamento cadastrar(Medicamento medicamento);

    /**
     * Remove um medicamento pelo ID.
     *
     * @param id identificador do medicamento
     */
    void removerPorId(int id);

    /**
     * Marca um medicamento como tomado.
     *
     * @param id identificador do medicamento
     */
    void marcarComoTomado(int id);
}
