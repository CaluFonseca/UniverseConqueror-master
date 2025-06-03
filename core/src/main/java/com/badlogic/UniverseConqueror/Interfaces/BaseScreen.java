package com.badlogic.UniverseConqueror.Interfaces;

import com.badlogic.gdx.Screen;

/**
 * Interface `BaseScreen` que serve como contrato para todas as telas do jogo.
 * Todas as telas que implementam esta interface devem fornecer implementações
 * para os métodos de inicialização da UI, sistemas, observadores e recursos.
 */
public interface BaseScreen extends Screen {

    /**
     * Método para inicializar a interface do usuário (UI).
     * Deve configurar todos os elementos visuais da tela, como rótulos, botões, barras de saúde, etc.
     */
    void initializeUI();  /// Método para inicializar os componentes da UI.

    /**
     * Método para inicializar todos os sistemas necessários para a tela.
     * Os sistemas podem incluir lógicas de entrada, animação, física, etc.
     */
    void initializeSystems();  /// Método para inicializar os sistemas da tela.

    /**
     * Método para registrar os observadores que irão reagir aos eventos na tela.
     * Normalmente, isso envolve o uso de um EventBus ou algo semelhante para escutar eventos.
     */
    void registerObservers();  /// Método para registrar observadores de eventos.

    /**
     * Método para liberar os recursos utilizados pela tela quando ela for descartada.
     * Isso inclui liberar memória, descartar textures, sons, etc.
     */
    void disposeResources();  /// Método para liberar recursos quando a tela for descartada.
}
