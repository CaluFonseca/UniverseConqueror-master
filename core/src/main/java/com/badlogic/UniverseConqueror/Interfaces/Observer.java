package com.badlogic.UniverseConqueror.Interfaces;

// Interface para qualquer classe que deseje ser notificada de eventos do jogo
public interface Observer {

    // Método chamado quando um evento ocorre
    void onNotify(GameEvent event);
}
