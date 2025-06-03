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

// Responsável por criar, configurar e reaproveitar projéteis no jogo.
// Utiliza pooling manual de entidades para evitar criação/destruição frequente.
public class BulletFactory {

    private final AssetManager assetManager;
    private final PooledEngine engine;
    private final Array<Entity> bulletPool = new Array<>();

    public BulletFactory(AssetManager assetManager, PooledEngine engine) {
        this.assetManager = assetManager;
        this.engine = engine;
    }

    // Garante que a entidade tenha o componente especificado, criando se necessário.
    private <T extends Component> T ensureComponent(Entity entity, Class<T> type) {
        T component = entity.getComponent(type);
        if (component == null) {
            component = engine.createComponent(type);
            entity.add(component);
        }
        return component;
    }

    // Obtém ou cria um projétil, aplica direção, corpo físico e textura.
    public Entity obtainProjectile(World world, float x, float y, Vector2 target, ProjectileComponent.ProjectileType type) {
        Entity bullet = null;

        // Tenta reutilizar projétil do pool
        while (bulletPool.size > 0) {
            Entity candidate = bulletPool.pop();
            EnemyComponent enemyComp = candidate.getComponent(EnemyComponent.class);
            ProjectileComponent projComp = candidate.getComponent(ProjectileComponent.class);

            if (enemyComp != null) continue;
            if (projComp != null && projComp.type == type) {
                bullet = candidate;
                break;
            }
        }

        // Cria nova entidade se nenhuma reutilizável for encontrada
        if (bullet == null) {
            bullet = engine.createEntity();
        }

        // Remove componentes que não devem estar em projéteis
        bullet.remove(EnemyComponent.class);
        bullet.remove(AnimationComponent.class);
        bullet.remove(HealthComponent.class);
        bullet.remove(StateComponent.class);
        bullet.remove(AIComponent.class);
        bullet.remove(ParticleComponent.class);
        bullet.remove(TransformComponent.class);
        bullet.remove(TextureComponent.class);
        bullet.remove(ProjectileComponent.class);

        // Garante e configura os componentes essenciais
        PositionComponent pc = ensureComponent(bullet, PositionComponent.class);
        VelocityComponent vc = ensureComponent(bullet, VelocityComponent.class);
        TransformComponent tc = ensureComponent(bullet, TransformComponent.class);
        PhysicsComponent ph = ensureComponent(bullet, PhysicsComponent.class);
        TextureComponent tx = ensureComponent(bullet, TextureComponent.class);
        ProjectileComponent proj = ensureComponent(bullet, ProjectileComponent.class);

        // Carrega textura e velocidade baseada no tipo
        Texture texture = (type == ProjectileComponent.ProjectileType.FIREBALL)
            ? assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class)
            : assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);
        float speed = (type == ProjectileComponent.ProjectileType.FIREBALL) ? 1900f : 100f;

        // Define direção com fallback
        Vector2 dir = target.cpy().sub(x, y);
        if (dir.isZero(0.001f)) dir.set(1, 0);
        dir.nor().scl(speed);

        // Define componentes
        pc.position.set(x, y);
        tc.position.set(x, y, 0);
        tx.texture = texture;
        vc.velocity.set(dir);
        proj.position.set(x, y);
        proj.speed = speed;
        proj.texture = texture;
        proj.type = type;

        // Atualiza corpo físico se já existe
        if (ph.body != null) {
            ph.body.setLinearVelocity(dir);
            ph.body.setAwake(true);
        }

        // Cria corpo novo se necessário
        if (ph.body == null || !ph.body.isActive() || ph.body.getType() != BodyDef.BodyType.DynamicBody) {
            if (ph.body != null) {
                world.destroyBody(ph.body);
            }
            ph.body = createBody(world, x, y, bullet, type);
        } else {
            for (Fixture f : ph.body.getFixtureList()) {
                ph.body.destroyFixture(f);
            }
            addFixtureToBody(ph.body, texture, type, bullet);
            ph.body.setTransform(x, y, 0);
            ph.body.setActive(true);
        }

        // Adiciona partícula se fireball
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

        // Adiciona à engine se ainda não estiver
        if (!engine.getEntities().contains(bullet, true)) {
            engine.addEntity(bullet);
        }

        return bullet;
    }

    // Liberta o projétil e devolve ao pool
    public void free(Entity bullet) {
        if (bullet.getComponent(EnemyComponent.class) != null) {
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

    // Cria um corpo físico Box2D com base no tipo de projétil
    private Body createBody(World world, float x, float y, Entity entity, ProjectileComponent.ProjectileType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        body.setBullet(true);

        Texture texture = (type == ProjectileComponent.ProjectileType.FIREBALL)
            ? assetManager.get(AssetPaths.FIREBALL_TEXTURE, Texture.class)
            : assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);

        addFixtureToBody(body, texture, type, entity);

        return body;
    }

    // Adiciona uma fixture circular baseada na textura ao corpo
    private void addFixtureToBody(Body body, Texture texture, ProjectileComponent.ProjectileType type, Entity entity) {
        CircleShape shape = new CircleShape();
        shape.setRadius(Math.min(texture.getWidth(), texture.getHeight()) / 2f);
        shape.setPosition(new Vector2(texture.getWidth() / 2f, texture.getHeight() / 2f));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0f;
        fixtureDef.friction = 0f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(type.name().toLowerCase());
        body.setUserData(entity);

        shape.dispose();
    }
}
