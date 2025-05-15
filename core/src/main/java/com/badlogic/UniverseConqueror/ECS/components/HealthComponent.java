package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    public int currentHealth = 100;
    public int maxHealth = 100;
    public boolean wasDamagedThisFrame = false;
    public float hurtCooldownTimer = 0f;
    public float hurtDuration = 0f;

    // Verifica se a entidade está morta
    public boolean isDead() {
        return currentHealth <= 0;
    }
    // Atualiza o estado do cooldown e da duração do dano
    public void update(float deltaTime) {
       if (hurtCooldownTimer > 0f) {
           hurtCooldownTimer -= deltaTime;  // Diminui o cooldown de invulnerabilidade
       }

       if (hurtDuration > 0f) {
           hurtDuration -= deltaTime;  // Diminui a duração do efeito visual
       }

        // Reseta o flag de dano no final do quadro
       wasDamagedThisFrame = false;
  }
}
