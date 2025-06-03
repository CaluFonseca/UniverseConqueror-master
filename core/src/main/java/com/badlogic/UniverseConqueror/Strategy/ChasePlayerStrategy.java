package com.badlogic.UniverseConqueror.Strategy;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.ashley.core.Entity;

/// Estratégia para perseguir o jogador
public class ChasePlayerStrategy extends AbstractEnemyStrategy {

    private final Entity target;  /// O alvo (jogador) que o inimigo deve perseguir.
    private final float speed;  /// A velocidade do inimigo ao perseguir o jogador.
    private final OrthographicCamera camera;  /// A câmera usada para visualização, pode ser utilizada para efeitos relacionados ao movimento do inimigo.

    /**
     * Construtor da estratégia de perseguição ao jogador.
     *
     * @param target A entidade alvo (o jogador) que será perseguida.
     * @param camera A câmera ortográfica usada para visualização (potencialmente afetando o comportamento do inimigo).
     * @param speed A velocidade com que o inimigo persegue o jogador.
     */
    public ChasePlayerStrategy(Entity target, OrthographicCamera camera, float speed) {
        this.target = target;  /// Atribui o alvo (jogador) a ser perseguido.
        this.camera = camera;  /// Atribui a câmera para manipulação, se necessário.
        this.speed = speed;  /// Atribui a velocidade da perseguição.
    }

    @Override
    public void update(Entity enemy, float deltaTime) {
        if (target == null) return;  /// Se o alvo (jogador) não estiver presente, não faz nada.

        // Obtém os componentes necessários para o inimigo e o alvo
        PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);  /// Componente de física do inimigo.
        PhysicsComponent targetPhysics = target.getComponent(PhysicsComponent.class);  /// Componente de física do alvo (jogador).
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);  /// Componente de velocidade do inimigo.
        StateComponent state = enemy.getComponent(StateComponent.class);  /// Componente de estado (como atacar, patrulhar, etc.).
        HealthComponent health = enemy.getComponent(HealthComponent.class);  /// Componente de saúde do inimigo.

        // Verifica se algum componente necessário está ausente ou se o inimigo está morto
        if (enemyPhysics == null || targetPhysics == null || velocity == null || health == null || health.isDead())
            return;  /// Se qualquer componente essencial estiver ausente ou o inimigo estiver morto, a atualização é interrompida.

        // Obtém a posição do inimigo e do alvo
        Vector2 enemyPos = enemyPhysics.body.getPosition();  /// Posição do inimigo no mundo.
        Vector2 targetPos = targetPhysics.body.getPosition();  /// Posição do alvo (jogador) no mundo.

        // Calcula a direção normalizada entre o inimigo e o alvo
        calculateDirection(enemyPos, targetPos);  /// Método da classe base que calcula a direção entre o inimigo e o alvo.

        float distance = enemyPos.dst(targetPos);  /// Calcula a distância entre o inimigo e o alvo.

        // Obtém o componente de animação do inimigo
        AnimationComponent animation = enemy.getComponent(AnimationComponent.class);

        // Se a distância entre o inimigo e o alvo for menor que 100, o inimigo para de se mover e volta ao estado de patrulha
        if (distance < 100f) {
            velocity.velocity.setZero();  /// Se o inimigo estiver muito perto do alvo, ele para de se mover.
            if (state.currentState != StateComponent.State.HURT) {  /// Se o inimigo não estiver ferido
                if (enemy.getComponent(UfoComponent.class) != null) {  /// Se o inimigo for um UFO
                    if (state.currentState != StateComponent.State.CHASE && state.currentState != StateComponent.State.HURT) {
                        state.set(StateComponent.State.CHASE);  /// Altera o estado do inimigo para "Perseguindo"
                    }
                } else {
                    if (state.currentState != StateComponent.State.PATROL && state.currentState != StateComponent.State.HURT) {
                        state.set(StateComponent.State.PATROL);  /// Se não for um UFO, o inimigo volta ao estado de patrulha
                    }
                }
            }
        } else {
            // Se o inimigo está distante o suficiente, ele continua se movendo em direção ao jogador
            velocity.velocity.set(direction.scl(speed));  /// A velocidade do inimigo é ajustada com base na direção e velocidade
            if (state.currentState != StateComponent.State.CHASE && state.currentState != StateComponent.State.HURT) {
                state.set(StateComponent.State.CHASE);  /// Altera o estado do inimigo para "Perseguindo"
            }
        }
    }
}
