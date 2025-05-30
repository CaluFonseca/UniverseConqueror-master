package com.badlogic.UniverseConqueror.ECS.observers;

import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/// Observador que atualiza os elementos da UI com base nos eventos do jogo.
public class UIObserver implements Observer {

    private final Label healthLabel;
    private final Label attackPowerLabel;
    private final Label itemsLabel;

    public UIObserver(Label healthLabel, Label attackPowerLabel, Label itemsLabel) {
        this.healthLabel = healthLabel;
        this.attackPowerLabel = attackPowerLabel;
        this.itemsLabel = itemsLabel;
    }

    @Override
    public void onNotify(GameEvent event) {

        /// Atualiza a barra de vida do jogador
        if (event instanceof HealthChangedEvent healthEvent) {
            updateHealthUI(healthEvent.currentHealth);

            /// Atualiza o contador de poder de ataque restante
        } else if (event instanceof AttackPowerChangedEvent attackEvent) {
            updateAttackPowerUI(attackEvent.newPower);

            /// Atualiza o número de itens coletados
        } else if (event instanceof ItemCollectedEvent itemEvent) {
            updateItemCount(itemEvent.count);
        }
    }

    /// Altera o texto da label de vida
    private void updateHealthUI(int currentHealth) {
        healthLabel.setText("Health: " + currentHealth);
    }

    /// Altera o texto da label de ataque
    private void updateAttackPowerUI(int remainingPower) {
        attackPowerLabel.setText("Attack: " + remainingPower);
    }

    /// Altera o texto da label de itens coletados
    private void updateItemCount(int count) {
        if (itemsLabel != null) {
            itemsLabel.setText("Items: " + count);
        }
    }
}
