package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class MovementSystem extends IteratingSystem {

    /// Mapeadores para acesso rápido aos componentes
    private ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    public MovementSystem() {
        /// Sistema processa entidades com corpo físico, velocidade e transformação
        super(Family.all(PhysicsComponent.class, VelocityComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = phm.get(entity);
        VelocityComponent velocity = vm.get(entity);
        TransformComponent transform = tm.get(entity);
        AnimationComponent animation = am.get(entity);

        if (physics.body != null && velocity != null) {
            /// Aplica a velocidade ao corpo físico (Box2D)
            physics.body.setLinearVelocity(velocity.velocity);

//            /// Atualiza a orientação do personagem com base na direção da velocidade
//            if (velocity != null && animation != null) {
//                float vx = velocity.velocity.x;
//
//                if (vx > 0.01f) {
//                    animation.facingRight = true;
//                } else if (vx < -0.01f) {
//                    animation.facingRight = false;
//                }
//
//            }

            /// Atualiza a posição lógica (TransformComponent) com a posição do corpo físico
            transform.position.set(
                physics.body.getPosition().x,
                physics.body.getPosition().y,
                transform.position.z
            );
        }
    }
}
