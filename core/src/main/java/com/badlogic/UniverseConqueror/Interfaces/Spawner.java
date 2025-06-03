package com.badlogic.UniverseConqueror.Interfaces;

/**
 * Interface `Spawner` define um contrato para objetos que geram ou "spawnam" instâncias de algum tipo.
 * Este padrão de projeto é usado para criar novos objetos de forma abstrata, sem expor diretamente
 * a lógica de criação do objeto.
 *
 * @param <T> O tipo de objeto que será gerado pelo spawner.
 */
public interface Spawner<T> {

    /**
     * Método responsável por criar uma nova instância do tipo especificado.
     * A implementação deve fornecer a lógica necessária para instanciar o objeto.
     *
     * @return Uma nova instância do tipo T.
     */
    T spawn();  /// Método que cria e retorna uma nova instância do tipo T.
}
