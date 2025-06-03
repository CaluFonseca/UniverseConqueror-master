package com.badlogic.UniverseConqueror.ECS.events;

import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.ashley.core.Entity;

// Evento disparado quando um item é coletado por um jogador
public class ItemCollectedEvent implements GameEvent {
    // Entidade do jogador que coletou o item
    public final Entity player;

    // Entidade do item coletado
    public final Entity item;

    // Contador de itens coletados até o momento
    public int count = 0;

    // Construtor com contador de itens
    public ItemCollectedEvent(Entity player, Entity item, int collectedCount) {
        this.player = player;
        this.item = item;
        this.count = collectedCount;
    }

}
