package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsSystem extends EntitySystem {
    /// Mundo físico Box2D onde as simulações ocorrem
    private World world;
    /// Renderer para debug visual do Box2D
    private Box2DDebugRenderer debugRenderer;

    /// Mapeadores para acessar componentes nas entidades
    private ComponentMapper<PhysicsComponent> pm = ComponentMapper.getFor(PhysicsComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> posm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    /// Construtor recebe o mundo físico e inicializa o renderer de debug
    public PhysicsSystem(World world) {
        this.world = world;
        this.debugRenderer = new Box2DDebugRenderer();
    }

    /// Método chamado todo frame para atualizar a física e sincronizar componentes
    @Override
    public void update(float deltaTime) {
        /// Avança a simulação física em passo fixo (60 FPS)
        world.step(1 / 60f, 6, 2);

        /// Para todas as entidades com componentes físicos relevantes
        for (Entity entity : getEngine().getEntitiesFor(
            Family.all(PhysicsComponent.class, VelocityComponent.class, PositionComponent.class).get())) {

            PhysicsComponent physics = pm.get(entity);
            VelocityComponent velocity = vm.get(entity);
            PositionComponent position = posm.get(entity);
            TransformComponent transform = tm.get(entity);

            Body body = physics.body;

            if (body != null) {
                /// Aplica a velocidade do componente Velocity ao corpo físico Box2D
                physics.body.setLinearVelocity(velocity.velocity);

                /// Atualiza os componentes Position e Transform para refletir a posição física atual do corpo
                position.position.set(body.getPosition().x, body.getPosition().y);
                transform.position.set(body.getPosition().x, body.getPosition().y, 0);
            }
        }
    }

    /// Permite configurar o ContactListener para colisões Box2D
    public void setContactListener(com.badlogic.gdx.physics.box2d.ContactListener listener) {
        world.setContactListener(listener);
    }

    /// Renderiza a visualização debug do mundo Box2D (normalmente para desenvolvimento)
    public void render(float deltaTime) {
        debugRenderer.render(world, getEngine().getSystem(CameraSystem.class).getCamera().combined);
    }
}
