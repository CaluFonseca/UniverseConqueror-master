package com.badlogic.UniverseConqueror.Interfaces;

/**
 * Interface `InitializableScreen` para ecrãs que necessitam
 * de inicialização de recursos e da UI.
 */
public interface InitializableScreen {

    // Método para inicializar recursos como assets e sons.
    void initializeResources();

    // Método para configurar e inicializar os componentes visuais da tela.
    void initializeUI();
}
