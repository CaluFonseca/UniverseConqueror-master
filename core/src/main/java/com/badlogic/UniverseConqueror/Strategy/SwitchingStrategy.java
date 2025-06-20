// Estratégia que alterna dinamicamente entre patrulha e perseguição.
// O inimigo persegue o jogador se estiver próximo e visível, caso contrário patrulha.

package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Interfaces.EnemyStrategy;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class SwitchingStrategy extends AbstractEnemyStrategy {
    private final EnemyStrategy patrolStrategy;
    private final EnemyStrategy chaseStrategy;
    private final Entity target;
    private final OrthographicCamera camera;
    private final float triggerDistance = 400f;
    private EnemyStrategy currentStrategy;
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);


    // Construtor que define o alvo, estratégias e câmera.
    public SwitchingStrategy(Entity target, EnemyStrategy patrolStrategy, EnemyStrategy chaseStrategy, OrthographicCamera camera) {
        this.target = target;
        this.patrolStrategy = patrolStrategy;
        this.chaseStrategy = chaseStrategy;
        this.camera = camera;
        this.currentStrategy = patrolStrategy;
    }

    // Verifica se uma posição está visível na câmera.
    private boolean isInCameraView(Vector2 worldPos) {
        float camX = camera.position.x;
        float camY = camera.position.y;
        float halfW = camera.viewportWidth * 0.5f * camera.zoom;
        float halfH = camera.viewportHeight * 0.5f * camera.zoom;

        return worldPos.x >= camX - halfW && worldPos.x <= camX + halfW &&
            worldPos.y >= camY - halfH && worldPos.y <= camY + halfH;
    }

    // Atualiza a estratégia do inimigo com base na visibilidade e distância do jogador.
    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null || camera == null || enemy == null) return;

        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);
        HealthComponent health = enemy.getComponent(HealthComponent.class);
        StateComponent state = sm.get(enemy);

        if (enemyPhysics == null || targetPhysics == null || health == null || state == null || health.isDead()) return;

        Vector2 enemyPos = enemyPhysics.body.getPosition();
        Vector2 playerPos = targetPhysics.body.getPosition();

        boolean playerVisible = isInCameraView(playerPos);
        float distance = enemyPos.dst(playerPos);

        // Bloqueia troca de estratégia se estiver morto.
        if (state.get() == StateComponent.State.DEATH) {
            currentStrategy.update(enemy, deltaTime);
            direction.set(currentStrategy.getDirection());
            return;
        }

        // Alterna para perseguição se o jogador estiver visível e próximo.
        if (playerVisible && distance <= triggerDistance) {
            currentStrategy = chaseStrategy;
        } else {
            currentStrategy = patrolStrategy;
        }

        currentStrategy.update(enemy, deltaTime);
        direction.set(currentStrategy.getDirection());
    }
}
