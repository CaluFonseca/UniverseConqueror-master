package com.badlogic.UniverseConqueror.Interfaces;

import com.badlogic.gdx.Screen;

/**
 * Interface `BaseScreen` que serve os ecrãs do jogo.
 * Todos os ecrãs que implementam esta interface devem fornecer implementações
 * para os métodos de inicialização da UI, sistemas, observadores e recursos.
 */
public interface BaseScreen extends Screen {

    //Método para inicializar a interface do usuário (UI).
    void initializeUI();

    //Método para inicializar todos os sistemas necessários para o ecrã.
    void initializeSystems();

    //Método para registrar os observadores que irão reagir aos eventos o ecrã.
    void registerObservers();

    // Método para libertar os recursos utilizados pelo ecrã quando for descartada.
    void disposeResources();
}
