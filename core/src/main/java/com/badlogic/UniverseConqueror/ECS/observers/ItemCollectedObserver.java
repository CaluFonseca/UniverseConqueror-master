package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.systems.AttackSystem;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;
import com.badlogic.ashley.core.Entity;

/// Observador responsável por reagir à coleta de itens.
/// Aplica efeitos de ataque ou cura conforme o tipo de item coletado.
public class ItemCollectedObserver implements Observer {

    private final AttackSystem attackSystem;
    private final HealthSystem healthSystem;

    public ItemCollectedObserver(AttackSystem attackSystem, HealthSystem healthSystem) {
        this.attackSystem = attackSystem;
        this.healthSystem = healthSystem;
    }

    @Override
    public void onNotify(GameEvent event) {
        /// Verifica se o evento recebido é de item coletado
        if (event instanceof ItemCollectedEvent e) {
            Entity item = e.item;
            ItemComponent itemComp = item.getComponent(ItemComponent.class);

            if (itemComp == null) return;

            switch (itemComp.name) {
                /// Item que aumenta o ataque em 5
                case "SuperAtaque":
                    attackSystem.increaseAttackPower(5);
                    EventBus.get().notify(new AttackPowerChangedEvent(e.player, attackSystem.getRemainingAttackPower()));
                    break;

                /// Item que aumenta o ataque em 1
                case "Ataque":
                    attackSystem.increaseAttackPower(1);
                    EventBus.get().notify(new AttackPowerChangedEvent(e.player, attackSystem.getRemainingAttackPower()));
                    break;

                /// Item que recupera 20 pontos de vida
                case "Vida":
                    healthSystem.heal(e.player, 20);
                    EventBus.get().notify(new HealthChangedEvent(e.player, healthSystem.getCurrentHealth(e.player)));
                    break;

                /// Outros tipos de itens não tratados
                default:
                    break;
            }
        }
    }
}
