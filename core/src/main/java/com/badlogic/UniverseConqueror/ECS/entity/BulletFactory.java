package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class BulletFactory {

    private static final Texture bulletTexture = new Texture("bullet.png");
    private static final Texture fireballTexture = new Texture("fireball.png");

    public Entity createProjectile(PooledEngine engine, World world, float x, float y, Vector2 target, ProjectileComponent.ProjectileType type) {
        Entity entity = engine.createEntity();

        // Componente de posição
        PositionComponent position = engine.createComponent(PositionComponent.class);
        position.position.set(x, y);
        entity.add(position);

        // Componente de projétil
        ProjectileComponent projectile = engine.createComponent(ProjectileComponent.class);
        projectile.type = type;
        projectile.position.set(x, y);
        projectile.speed = (type == ProjectileComponent.ProjectileType.FIREBALL) ? 900f : 500f;
        projectile.texture = (type == ProjectileComponent.ProjectileType.FIREBALL) ? fireballTexture : bulletTexture;
        entity.add(projectile);

        // Direção e velocidade
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        Vector2 direction = target.cpy().sub(x, y).nor();
        velocity.velocity = direction.scl(projectile.speed);
        entity.add(velocity);

        // Transformação
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.position.set(x, y, 0);
        entity.add(transform);

        // Física
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        physics.body = createBody(world, x, y, entity, type);
        entity.add(physics);

        // Textura para renderização
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        textureComponent.texture = projectile.texture;
        entity.add(textureComponent);

        engine.addEntity(entity);
        return entity;
    }

    private Body createBody(World world, float x, float y, Entity bulletEntity, ProjectileComponent.ProjectileType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        if (type == ProjectileComponent.ProjectileType.FIREBALL) {
            shape.setAsBox(60f, 60f);
        } else {
            shape.setAsBox(80f, 10f);
        }

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(type.name().toLowerCase()); // "bullet" ou "fireball"
        body.setUserData(bulletEntity);
        body.setFixedRotation(false);
        shape.dispose();

        return body;
    }

    public static void dispose() {
        bulletTexture.dispose();
        fireballTexture.dispose();
    }
}
