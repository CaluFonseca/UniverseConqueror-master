package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Pathfinding.AStarPathfinder;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

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
        if (animation != null) {
            animation.facingRight = direction.x <= 0; // se direção x <=0, inimigo está olhando para esquerda
        }

        float distance = direction.len();

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

    // Método comentado para recálculo do caminho usando A* após colisão (opcional)
    /*
    public void recalculatePath(MapGraphBuilder graphBuilder, AStarPathfinder pathfinder, Entity target, Entity enemy) {
        System.out.println("[DEBUG] Recalculando rota para o inimigo após colisão.");

        Vector2 start = enemy.getComponent(PositionComponent.class).position;
        Vector2 goal = target.getComponent(PositionComponent.class).position;

        Node startNode = graphBuilder.toNode(start);
        Node goalNode = graphBuilder.toNode(goal);

        System.out.println("[DEBUG] Posição start: " + start);
        System.out.println("[DEBUG] Posição goal: " + goal);
        System.out.println("[DEBUG] StartNode: " + (startNode != null ? startNode.x + "," + startNode.y : "null"));
        System.out.println("[DEBUG] GoalNode: " + (goalNode != null ? goalNode.x + "," + goalNode.y : "null"));

        if (startNode == null || goalNode == null) {
            System.out.println("[DEBUG] Falha ao converter posições em nós do grafo.");
            return;
        }

        List<Node> path = pathfinder.findPath(startNode, goalNode);

        if (path != null && path.size() > 1) {
            System.out.println("[DEBUG] Caminho encontrado com " + path.size() + " nós:");
            for (int i = 0; i < path.size(); i++) {
                Node n = path.get(i);
                System.out.println("  -> Nó " + i + ": (" + n.x + ", " + n.y + ")");
            }

            Node nextNode = path.get(1); // Pula o nó atual
            Vector2 nextWorld = graphBuilder.toWorldPosition(nextNode);
            Vector2 direction = new Vector2(nextWorld).sub(start).nor().scl(speed);

            VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
            if (velocity != null) {
                velocity.velocity.set(direction);
                System.out.println("[DEBUG] Direção aplicada ao inimigo: " + direction);
            } else {
                System.out.println("[ERRO] VelocityComponent não encontrado no inimigo.");
            }

        } else {
            System.out.println("[DEBUG] Caminho vazio ou nulo.");
        }
    }
    */

    @Override
    public Vector2 getDirection() {
        return direction;
    }
}
