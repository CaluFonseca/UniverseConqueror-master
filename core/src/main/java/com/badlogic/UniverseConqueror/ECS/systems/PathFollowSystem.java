package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class PathFollowSystem extends IteratingSystem {
    /// Mapeadores para componentes usados no sistema
    private ComponentMapper<PathComponent> pathMapper = ComponentMapper.getFor(PathComponent.class);
    private ComponentMapper<PositionComponent> posMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> velMapper = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PhysicsComponent> phyMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<AnimationComponent> animMapper = ComponentMapper.getFor(AnimationComponent.class);

    public PathFollowSystem() {
        /// Configura família para entidades que têm Path, Position e Velocity
        super(Family.all(PathComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        /// Obtém os componentes relevantes da entidade
        PathComponent path = pathMapper.get(entity);
        PhysicsComponent physics = phyMapper.get(entity);
        VelocityComponent velocity = velMapper.get(entity);
        AnimationComponent animation = animMapper.get(entity);

        /// Se não há waypoints, para o movimento
        if (path.waypoints.isEmpty()) {
            velocity.velocity.setZero(); /// para movimento
            return;
        }

        /// Obtém o próximo waypoint para onde a entidade deve ir
        Vector2 target = path.waypoints.peek();

        /// Obtém a posição atual da entidade pelo corpo físico Box2D
        Vector2 currentPos = physics.body.getPosition();

        /// Calcula vetor direção e distância entre posição atual e alvo
        Vector2 direction = new Vector2(target).sub(currentPos);
        float distance = direction.len();

        /// Se estiver perto o suficiente do waypoint
        if (distance < 50f) {
            SoundManager.getInstance().play("wayPoint"); /// toca som de waypoint alcançado
            path.waypoints.poll(); /// remove waypoint alcançado da fila
            velocity.velocity.setZero(); /// para o movimento momentaneamente
        } else {
            /// Define velocidade normalizada multiplicada pela velocidade desejada (100f)
            velocity.velocity.set(direction.nor().scl(100f));
        }

//        if (animation != null) {
//            if (velocity.velocity.x > 0) {
//                animation.facingRight = true;
//            } else if (velocity.velocity.x < 0) {
//                animation.facingRight = false;
//            }
//        }
    }
}
