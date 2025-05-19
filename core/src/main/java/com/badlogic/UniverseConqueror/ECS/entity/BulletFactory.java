package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class BulletFactory {
    private final AssetManager assetManager;

    public BulletFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Entity createProjectile(PooledEngine engine, World world, float x, float y, Vector2 target, ProjectileComponent.ProjectileType type) {
        Entity entity = engine.createEntity();
        Texture bulletTexture = assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);
        Texture fireballTexture = assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class);
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

        if (type == ProjectileComponent.ProjectileType.FIREBALL) {
            ParticleComponent particle = engine.createComponent(ParticleComponent.class);
            ParticleEffect effect = assetManager.get(AssetPaths.PARTICLE_EXPLOSION, ParticleEffect.class);
            particle.effect = new ParticleEffect(effect);
//            float width = projectile.texture.getWidth();
//            float height = projectile.texture.getHeight();
//            particle.effect.setPosition(x + width / 2f, y + height / 2f);

            particle.effect.start();
            float width = projectile.texture.getWidth();
            float height = projectile.texture.getHeight();
            particle.offset.set(width / 2f, height / 2f);
            entity.add(particle);
        }

        engine.addEntity(entity);
        return entity;
    }

    private Body createBody(World world, float x, float y, Entity bulletEntity, ProjectileComponent.ProjectileType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        Texture texture = type == ProjectileComponent.ProjectileType.FIREBALL ?
                assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class) :
                assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);

        float width = texture.getWidth();
        float height = texture.getHeight();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                width / 2f, height / 2f,
                new Vector2(width / 2f, height / 2f),
                0f
        );
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
        GameLauncher.assetManager.dispose();
    }
}
