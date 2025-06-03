package com.badlogic.UniverseConqueror.ECS.events;

// Classe utilitária para acesso global ao sistema de eventos do jogo
public class EventBus {

    // Instância única do gestor de eventos
    private static final GameEventManager instance = new GameEventManager();

    // Retorna a instância global do gestor de eventos
    public static GameEventManager get() {
        return instance;
    }
}
