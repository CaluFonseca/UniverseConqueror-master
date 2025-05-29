package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BulletMovementSystem extends IteratingSystem {

    /// Mapeadores para acesso rápido aos componentes
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);

    /// Construtor define que o sistema opera em entidades com Position, Velocity e Physics
    public BulletMovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class, PhysicsComponent.class).get());
    }

    /// Atualiza a posição e velocidade da bala com base no corpo físico
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = vm.get(entity);
        PhysicsComponent physics = phm.get(entity);
        PositionComponent position = pm.get(entity);

        /// Se o corpo estiver ativo, aplica velocidade e sincroniza a posição lógica
        if (physics.body != null && physics.body.isActive()) {
            physics.body.setLinearVelocity(velocity.velocity);       // Aplica a velocidade ao corpo Box2D
            position.position.set(physics.body.getPosition());       // Atualiza a posição lógica da entidade
            // System.out.println("[DEBUG Bullet] Velocidade aplicada: " + velocity.velocity);
        }
    }

    // Versão alternativa sem física, baseada apenas na multiplicação de vetor:
    /*
    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEngine().getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class, TransformComponent.class).get())) {
            PositionComponent position = pm.get(entity);
            VelocityComponent velocity = vm.get(entity);
            position.position.mulAdd(velocity.velocity, deltaTime);
        }
    }
    */
}
