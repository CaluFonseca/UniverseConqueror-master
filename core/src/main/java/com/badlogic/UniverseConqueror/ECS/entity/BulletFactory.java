package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class BulletFactory {

    private final AssetManager assetManager;
    private final PooledEngine engine;
    private final Array<Entity> bulletPool = new Array<>();

    public BulletFactory(AssetManager assetManager, PooledEngine engine) {
        this.assetManager = assetManager;
        this.engine = engine;
    }

    private <T extends Component> T ensureComponent(Entity entity, Class<T> type) {
        T component = entity.getComponent(type);
        if (component == null) {
            component = engine.createComponent(type);
            entity.add(component);
        }
        return component;
    }

    public Entity obtainProjectile(World world, float x, float y, Vector2 target, ProjectileComponent.ProjectileType type) {
        Entity bullet = (bulletPool.size > 0) ? bulletPool.pop() : engine.createEntity();

        ensureComponent(bullet, PositionComponent.class);
        ensureComponent(bullet, VelocityComponent.class);
        ensureComponent(bullet, TransformComponent.class);
        ensureComponent(bullet, PhysicsComponent.class);
        ensureComponent(bullet, TextureComponent.class);
        ensureComponent(bullet, ProjectileComponent.class);

        PositionComponent pc = bullet.getComponent(PositionComponent.class);
        VelocityComponent vc = bullet.getComponent(VelocityComponent.class);
        TransformComponent tc = bullet.getComponent(TransformComponent.class);
        PhysicsComponent ph = bullet.getComponent(PhysicsComponent.class);
        TextureComponent tx = bullet.getComponent(TextureComponent.class);
        ProjectileComponent proj = bullet.getComponent(ProjectileComponent.class);

        Texture texture = (type == ProjectileComponent.ProjectileType.FIREBALL)
            ? assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class)
            : assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);

        float speed = (type == ProjectileComponent.ProjectileType.FIREBALL) ? 900f : 500f;
        Vector2 dir = target.cpy().sub(x, y).nor().scl(speed);

        pc.position.set(x, y);
        vc.velocity.set(dir);
        tc.position.set(x, y, 0);
        tx.texture = texture;

        proj.position.set(x, y);
        proj.speed = speed;
        proj.texture = texture;
        proj.type = type;

        if (ph.body == null) {
            ph.body = createBody(world, x, y, bullet, type);
        } else {
            ph.body.setTransform(x, y, 0);
            ph.body.setActive(true);
        }

        if (type == ProjectileComponent.ProjectileType.FIREBALL) {
            if (bullet.getComponent(ParticleComponent.class) == null) {
                ParticleComponent particle = engine.createComponent(ParticleComponent.class);
                ParticleEffect effect = new ParticleEffect(assetManager.get(AssetPaths.PARTICLE_EXPLOSION, ParticleEffect.class));
                effect.start();
                particle.effect = effect;
                particle.offset.set(texture.getWidth() / 2f, texture.getHeight() / 2f);
                bullet.add(particle);
            }
        }

        return bullet;
    }

    public void free(Entity bullet) {
        PhysicsComponent physics = bullet.getComponent(PhysicsComponent.class);
        if (physics != null && physics.body != null) {
            physics.body.setActive(false);
            physics.body.setLinearVelocity(0, 0);
        } else {
            for (Component comp : bullet.getComponents()) {
                System.err.println(" - " + comp.getClass().getSimpleName());
            }
        }

        VelocityComponent velocity = bullet.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.velocity.setZero();
        }

        PositionComponent position = bullet.getComponent(PositionComponent.class);
        if (position != null) {
            position.position.set(-10000, -10000);
        }

        bulletPool.add(bullet);
    }

    private Body createBody(World world, float x, float y, Entity entity, ProjectileComponent.ProjectileType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        Texture texture = (type == ProjectileComponent.ProjectileType.FIREBALL)
            ? assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class)
            : assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);

        float w = texture.getWidth();
        float h = texture.getHeight();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f, new Vector2(w / 2f, h / 2f), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(type.name().toLowerCase());
        body.setUserData(entity);
        shape.dispose();

        return body;
    }
}
