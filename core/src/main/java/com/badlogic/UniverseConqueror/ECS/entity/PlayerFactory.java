package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.AnimationLoader;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerFactory {

    /// Cria e retorna a entidade do jogador, com todos os componentes necessários.
    public static Entity createPlayer(PooledEngine engine,
                                      Vector2 position,
                                      World world,
                                      AssetManager assetManager) {

        Entity entity = engine.createEntity();

        /// Transformação e posição
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.position.set(position.x, position.y, 0);
        entity.add(transform);

        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.position.set(position.x, position.y);
        entity.add(positionComponent);

        /// Física
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        BodyComponent bodyComponent = createBody(position, world);
        physicsComponent.body = bodyComponent.body;
        entity.add(physicsComponent);
        entity.add(bodyComponent);

        /// Velocidade e estado
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        entity.add(velocity);
        entity.add(state);

        /// Carrega as animações com o AnimationLoader
        AnimationLoader animationLoader = new AnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = animationLoader.loadAnimations();

        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        animationComponent.setAnimations(animations);
        entity.add(animationComponent);

        /// Sons
        SoundComponent sound = engine.createComponent(SoundComponent.class);
        entity.add(sound);

        /// Outros componentes
        entity.add(engine.createComponent(AttackComponent.class));
        entity.add(engine.createComponent(JumpComponent.class));
        entity.add(engine.createComponent(PlayerComponent.class));
        entity.add(engine.createComponent(CameraComponent.class));
        entity.add(engine.createComponent(HealthComponent.class));

        /// Associa o corpo à entidade
        bodyComponent.body.setUserData(entity);

        return entity;
    }

    /// Cria o corpo físico do jogador com Box2D
    public static BodyComponent createBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position); /// Define a posição inicial do corpo no mundo
        bodyDef.type = BodyDef.BodyType.DynamicBody; /// Tipo dinâmico para corpos que se movem

        Body body = world.createBody(bodyDef);

        /// Cria a forma de colisão (retângulo centrado)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(40f, 40f);

        /// Define as propriedades físicas do corpo
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        body.createFixture(fixtureDef);

        /// Libera recursos da forma
        shape.dispose();

        /// Cria e retorna o componente de corpo
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.body = body;
        return bodyComponent;
    }
}
