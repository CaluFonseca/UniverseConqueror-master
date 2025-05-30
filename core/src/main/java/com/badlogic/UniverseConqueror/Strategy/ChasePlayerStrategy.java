package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Interfaces.EnemyStrategy;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class ChasePlayerStrategy implements EnemyStrategy {

    /// Entidade alvo (normalmente o jogador)
    private final Entity target;
    /// Velocidade de movimentação do inimigo
    private final float speed;
    /// Vetor auxiliar para cálculo de direção
    private final Vector2 direction = new Vector2();
    /// Câmera para uso eventual (ex: culling)
    private final OrthographicCamera camera;

    /// Construtor recebe o alvo, câmera e velocidade
    public ChasePlayerStrategy(Entity target, OrthographicCamera camera, float speed) {
        this.target = target;
        this.camera = camera;
        this.speed = speed;
    }

    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null) return; // Sem alvo, não faz nada

        // Obtém componentes essenciais do inimigo e do alvo
        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        StateComponent state = enemy.getComponent(StateComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);

        // Se algum componente está ausente ou inimigo está morto, não atualiza movimento
        if (enemyPhysics == null || targetPhysics == null || velocity == null || health == null || health.isDead()) return;

        // Posições do inimigo e alvo no mundo
        Vector2 enemyPos = enemyPhysics.body.getPosition();
        Vector2 targetPos = targetPhysics.body.getPosition();

        // Calcula vetor direção do inimigo até o alvo
        direction.set(targetPos).sub(enemyPos);

        // Atualiza animação para virar para esquerda/direita conforme direção
        AnimationComponent animation = enemy.getComponent(AnimationComponent.class);

        float distance = direction.len();
        if (animation != null && distance >= 100f) {
            float threshold = 0.01f;
            if (direction.x > threshold) {
                animation.facingRight = false;
            } else if (direction.x < -threshold) {
                animation.facingRight = true;
            }
        }

        // Se está perto demais, para e muda estado para patrulha ou chase dependendo do tipo
        if (distance < 100f) {
            velocity.velocity.setZero();
            if (state.currentState != StateComponent.State.HURT) {
                if (enemy.getComponent(UfoComponent.class) != null) {
                    state.set(StateComponent.State.CHASE);
                } else {
                    state.set(StateComponent.State.PATROL);
                }
            }
        } else {
            // Caso contrário, normaliza direção e aplica velocidade configurada
            direction.nor().scl(speed);
            velocity.velocity.set(direction);
            if (state.currentState != StateComponent.State.HURT) {
                state.set(StateComponent.State.CHASE);
            }
        }
    }

    @Override
    public Vector2 getDirection() {
        return direction;
    }
}
