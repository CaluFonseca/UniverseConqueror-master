package com.badlogic.UniverseConqueror.ECS.events;

/// Classe utilitária para acesso global ao sistema de eventos do jogo
public class EventBus {

    /// Instância única do gerenciador de eventos
    private static final GameEventManager instance = new GameEventManager();

    /// Retorna a instância global do gerenciador de eventos
    public static GameEventManager get() {
        return instance;
    }
}
