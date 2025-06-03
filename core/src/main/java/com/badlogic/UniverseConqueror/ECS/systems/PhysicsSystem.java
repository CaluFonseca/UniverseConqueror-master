package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

///Sistema responsável por atualizar a física do Box2D
public class PhysicsSystem extends BaseIteratingSystem {

    /// Mundo físico do Box2D
    private final World world;
    /// Renderizador de debug do mundo físico
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    /// Construtor com referência ao mundo
    public PhysicsSystem(World world) {
        super(Family.all(PhysicsComponent.class, VelocityComponent.class, PositionComponent.class).get());
        this.world = world;
    }

    /// Atualiza o mundo físico e processa entidades uma por uma
    @Override
    public void update(float deltaTime) {
        world.step(1 / 60f, 6, 2);
        super.update(deltaTime);
    }

    /// Lógica aplicada a cada entidade com os componentes necessários
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = ComponentMappers.physics.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);
        TransformComponent transform = ComponentMappers.transform.get(entity);

        Body body = physics.body;
        if (body != null) {
            body.setLinearVelocity(velocity.velocity);
            position.position.set(body.getPosition().x, body.getPosition().y);
            transform.position.set(body.getPosition().x, body.getPosition().y, 0);
        }
    }

    /// Permite configurar o ContactListener do mundo
    public void setContactListener(ContactListener listener) {
        world.setContactListener(listener);
    }

    /// Renderiza visualização de debug do mundo físico
    public void render(float deltaTime) {
        debugRenderer.render(world, getEngine().getSystem(CameraSystem.class).getCamera().combined);
    }
}
