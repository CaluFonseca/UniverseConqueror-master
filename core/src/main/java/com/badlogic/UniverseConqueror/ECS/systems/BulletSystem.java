package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletSystem extends EntitySystem {
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<PhysicsComponent> em = ComponentMapper.getFor(PhysicsComponent.class);

    private final OrthographicCamera camera;
    private final PooledEngine engine;
    private final BulletFactory bulletFactory;

    private final Array<Entity> activeBullets = new Array<>();

    public BulletSystem(OrthographicCamera camera, AssetManager assetManager, PooledEngine engine) {
        this.camera = camera;
        this.engine = engine;
        this.bulletFactory = new BulletFactory(assetManager, engine);
    }

    @Override
    public void update(float deltaTime) {

        for (int i = activeBullets.size - 1; i >= 0; i--) {

            Entity bullet = activeBullets.get(i);
            PositionComponent position = pm.get(bullet);
            VelocityComponent velocity = vm.get(bullet);
            PhysicsComponent physics = em.get(bullet);
            if (position == null || velocity == null) {
                activeBullets.removeIndex(i);
                continue;
            }
            if (position != null && velocity != null) {
                position.position.mulAdd(velocity.velocity, deltaTime);
            }

            if (isOutOfBounds(position)) {
                if (physics != null && physics.body != null) {
                    physics.body.setActive(false);
                }
                engine.removeEntity(bullet);
                activeBullets.removeIndex(i);
                bulletFactory.free(bullet); // devolve à pool
            }
        }
        for (Entity bullet : activeBullets) {
            if (bullet.getComponent(EnemyComponent.class) != null) {
                System.err.println("[ERRO FATAL] Bullet ainda tem EnemyComponent! ID: " + bullet.hashCode());
            }
        }
    }

    private boolean isOutOfBounds(PositionComponent position) {
        float margin = 100f;
        return position.position.x < camera.position.x - camera.viewportWidth / 2 - margin ||
            position.position.x > camera.position.x + camera.viewportWidth / 2 + margin ||
            position.position.y < camera.position.y - camera.viewportHeight / 2 - margin ||
            position.position.y > camera.position.y + camera.viewportHeight / 2 + margin;
    }

    public void spawnProjectile(float x, float y, Vector2 target, Body body, ProjectileComponent.ProjectileType type) {
        Entity bullet = bulletFactory.obtainProjectile(body.getWorld(), x, y, target, type);
        if (bullet.getComponent(ProjectileComponent.class) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] ERRO: entidade retornada pelo BulletFactory não tem ProjectileComponent! ID: " + bullet.hashCode());
        }
    }

    public void dispose() {

    }
    public void spawnedFromFactory(Entity bullet) {
        if (bullet.getComponent(ProjectileComponent.class) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] Tentativa de adicionar entidade sem ProjectileComponent à lista de projéteis: " + bullet.hashCode());
        }
    }

}
