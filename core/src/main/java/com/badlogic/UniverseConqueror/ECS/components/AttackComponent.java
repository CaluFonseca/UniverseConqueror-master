package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class AttackComponent implements Component {

    /// Indica se a entidade está atualmente atacando
    public boolean isAttacking = false;

    /// Temporizador que controla a duração do ataque atual
    public float attackTimer = 0f;

    /// Duração de um ataque em segundos
    public float attackDuration = 0.3f;

    /// Tempo mínimo entre dois ataques consecutivos
    public float attackCooldown = 0.5f;

    /// Tempo desde o último ataque
    public float timeSinceLastAttack = 0f;

    /// Capacidade máxima de poder de ataque
    public int maxAttackPower = 100;

    /// Energia restante disponível para ataques
    public int remainingAttackPower = 200;

    /// Custo de energia para cada ataque
    public int attackCost = 10;

    /// Construtor padrão
    public AttackComponent() {}

    /// Construtor com valores personalizados
    public AttackComponent(int maxAttackPower, int attackCost) {
        this.maxAttackPower = maxAttackPower;
        this.remainingAttackPower = maxAttackPower;
        this.attackCost = attackCost;
    }

    /// Verifica se a entidade pode iniciar um novo ataque
    public boolean canAttack() {
        return timeSinceLastAttack >= attackCooldown && remainingAttackPower >= attackCost;
    }

    /// Inicia um ataque e atualiza os valores relacionados
    public void startAttack() {
        isAttacking = true;
        attackTimer = 0f;
        timeSinceLastAttack = 0f;
        remainingAttackPower -= attackCost;

        if (remainingAttackPower < 0) {
            remainingAttackPower = 0;
        }
    }

    /// Restaura uma quantidade específica de poder de ataque
    public void restorePower(int amount) {
        remainingAttackPower += amount;
        if (remainingAttackPower > maxAttackPower) {
            remainingAttackPower = maxAttackPower;
        }
    }
}
