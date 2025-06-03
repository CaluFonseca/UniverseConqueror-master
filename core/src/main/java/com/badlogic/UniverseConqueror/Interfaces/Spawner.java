package com.badlogic.UniverseConqueror.Interfaces;

/**
 Este padrão de projeto é usado para criar novos objetos de forma abstrata, sem expor diretamente
 * a lógica de criação do objeto.
 * @param <T> O tipo de objeto que será gerado pelo spawner.
 */
public interface Spawner<T> {

    // Método que cria e retorna uma nova instância do tipo T.
    T spawn();
}
