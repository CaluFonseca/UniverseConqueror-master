package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    /// Vida atual da entidade
    public int currentHealth = 100;

    /// Vida máxima da entidade
    public int maxHealth = 100;

    /// Indica se a entidade recebeu dano neste frame
    public boolean wasDamagedThisFrame = false;

    /// Tempo restante de invulnerabilidade após sofrer dano
    public float hurtCooldownTimer = 0f;

    /// Duração total da animação ou estado de dano
    public float hurtDuration = 0f;

    /// Marca a entidade para remoção do mundo
    public boolean scheduledForRemoval = false;

    /// Verifica se a entidade está morta
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /// Atualiza os temporizadores de cooldown e dano
    public void update(float deltaTime) {
        if (hurtCooldownTimer > 0f) {
            hurtCooldownTimer -= deltaTime;
        }

        if (hurtDuration > 0f) {
            hurtDuration -= deltaTime;
        }

        wasDamagedThisFrame = false;
    }
}
