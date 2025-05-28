package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    public int currentHealth = 100;
    public int maxHealth = 100;
    public boolean wasDamagedThisFrame = false;
    public float hurtCooldownTimer = 0f;
    public float hurtDuration = 0f;
    public boolean scheduledForRemoval = false;
    // Verifica se a entidade está morta
    public boolean isDead() {
        return currentHealth <= 0;
    }

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
