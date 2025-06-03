package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.Context.GameContext;

/**
 * Classe abstrata que serve como base para inicializadores no jogo.
 * Essa classe proporciona uma estrutura comum para inicializar componentes do jogo.
 */
public abstract class AbstractInitializer {
    // Contexto do jogo, onde podem ser acessados sistemas e outras informações do jogo.
    protected final GameContext context;

    /**
     * Construtor que recebe o contexto do jogo e o inicializa.
     *
     * @param context O contexto do jogo, que contém informações e sistemas do jogo.
     */
    public AbstractInitializer(GameContext context) {
        this.context = context;
    }

    /**
     * Método abstrato para inicializar os componentes do jogo.
     * Subclasses devem implementar esse método para realizar a inicialização dos seus respectivos componentes.
     */
    public abstract void initialize();
}
