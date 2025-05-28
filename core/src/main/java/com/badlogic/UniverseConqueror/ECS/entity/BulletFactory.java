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
        Entity bullet = null;

        while (bulletPool.size > 0) {
            Entity candidate = bulletPool.pop();
            EnemyComponent enemyComp = candidate.getComponent(EnemyComponent.class);
            ProjectileComponent projComp = candidate.getComponent(ProjectileComponent.class);

            if (enemyComp != null) {
                //System.out.println("[BulletFactory] Descarta entidade com EnemyComponent (UFO/Inimigo) na pool de balas: " + candidate.hashCode());
                continue;
            }

            if (projComp != null && projComp.type == type) {
                bullet = candidate;
             //   System.out.println("[BulletFactory] Reutilizando bullet da pool: " + bullet.hashCode());
                break;
            } else {
              //  System.out.println("[BulletFactory] Entity in bulletPool with wrong ProjectileType. Discarding entity: " + candidate.hashCode());
            }
        }

        if (bullet == null) {
            bullet = engine.createEntity();
            //System.out.println("[BulletFactory] Criando nova bullet: " + bullet.hashCode());
        }

        // Limpar componentes
        bullet.remove(EnemyComponent.class);
        bullet.remove(AnimationComponent.class);
        bullet.remove(HealthComponent.class);
        bullet.remove(StateComponent.class);
        bullet.remove(AIComponent.class);
        bullet.remove(ParticleComponent.class);
        bullet.remove(TransformComponent.class);
        bullet.remove(TextureComponent.class);
        bullet.remove(ProjectileComponent.class);

        // Garantir componentes essenciais
        PositionComponent pc = ensureComponent(bullet, PositionComponent.class);
        VelocityComponent vc = ensureComponent(bullet, VelocityComponent.class);
        TransformComponent tc = ensureComponent(bullet, TransformComponent.class);
        PhysicsComponent ph = ensureComponent(bullet, PhysicsComponent.class);
        TextureComponent tx = ensureComponent(bullet, TextureComponent.class);
        ProjectileComponent proj = ensureComponent(bullet, ProjectileComponent.class);

        // Dados do projétil
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

        // Corpo Box2D
        if (ph.body == null || !ph.body.isActive() || ph.body.getType() != BodyDef.BodyType.DynamicBody) {
            if (ph.body != null) {
               // System.out.println("[BulletFactory] ⚠️ Destruindo corpo inválido antes de recriar: " + ph.body);
                world.destroyBody(ph.body);
            }
            ph.body = createBody(world, x, y, bullet, type);
          //  System.out.println("[BulletFactory] Criado corpo novo para bullet " + bullet.hashCode());
        } else {
            for (Fixture f : ph.body.getFixtureList()) {
                ph.body.destroyFixture(f);
            }
            addFixtureToBody(ph.body, texture, type, bullet);
            ph.body.setTransform(x, y, 0);
            ph.body.setActive(true);
         //   System.out.println("[BulletFactory] Reutilizado corpo existente para bullet " + bullet.hashCode());
        }

        // Partículas
        ParticleComponent pcComp = bullet.getComponent(ParticleComponent.class);
        if (type == ProjectileComponent.ProjectileType.FIREBALL) {
            if (pcComp == null) {
                ParticleComponent particle = engine.createComponent(ParticleComponent.class);
                ParticleEffect effect = new ParticleEffect(assetManager.get(AssetPaths.PARTICLE_EXPLOSION, ParticleEffect.class));
                effect.start();
                particle.effect = effect;
                particle.offset.set(texture.getWidth() / 2f, texture.getHeight() / 2f);
                bullet.add(particle);
            }
        } else {
            if (pcComp != null) {
                pcComp.effect.dispose();
                bullet.remove(ParticleComponent.class);
            }
        }

        if (!engine.getEntities().contains(bullet, true)) {
            engine.addEntity(bullet);
            //System.out.println("[BulletFactory] Adicionado bullet ao engine: " + bullet.hashCode());
        }

        return bullet;
    }

    public void free(Entity bullet) {
        if (bullet.getComponent(EnemyComponent.class) != null) {
            //System.out.println("[BulletFactory] Ignorando liberação de UFO/inimigo na pool de balas: " + bullet.hashCode());
            if (engine.getEntities().contains(bullet, true)) {
                engine.removeEntity(bullet);
            }
            return;
        }

        PhysicsComponent physics = bullet.getComponent(PhysicsComponent.class);
        if (physics != null && physics.body != null) {
            physics.body.setActive(false);
            physics.body.setLinearVelocity(0, 0);
            physics.body.setTransform(-10000f, -10000f, 0f);
            for (Fixture fixture : physics.body.getFixtureList()) {
                physics.body.destroyFixture(fixture);
            }
        }

        VelocityComponent velocity = bullet.getComponent(VelocityComponent.class);
        if (velocity != null) velocity.velocity.setZero();

        PositionComponent position = bullet.getComponent(PositionComponent.class);
        if (position != null) position.position.set(-10000, -10000);

        TextureComponent tx = bullet.getComponent(TextureComponent.class);
        if (tx != null) tx.texture = null;

        if (engine.getEntities().contains(bullet, true)) {
            engine.removeEntity(bullet);
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

        addFixtureToBody(body, texture, type, entity);

        return body;
    }

    private void addFixtureToBody(Body body, Texture texture, ProjectileComponent.ProjectileType type, Entity entity) {
        float w = texture.getWidth() / 2f;
        float h = texture.getHeight() / 2f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w, h, new Vector2(w, h), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(type.name().toLowerCase());
        body.setUserData(entity);

        shape.dispose();
    }
}
