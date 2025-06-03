package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento disparado quando o jogo é finalizado
public class EndGameEvent implements GameEvent {

    // Entidade que atingiu o objetivo do fim do jogo
    public final Entity entity;

    // Número de inimigos derrotados até o fim do jogo
    public final int enemiesKilled;

    // Construtor do evento de fim de jogo
    // @param entity Entidade responsável por finalizar o jogo
    // @param enemiesKilled Total de inimigos eliminados
    public EndGameEvent(Entity entity, int enemiesKilled) {
        this.entity = entity;
        this.enemiesKilled = enemiesKilled;
    }
}
