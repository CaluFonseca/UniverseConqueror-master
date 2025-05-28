package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.EnemyComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.components.ProjectileComponent;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.UniverseConqueror.ECS.systems.HealthSystem;

public class BulletContactListener implements ContactListener {
    private final Engine engine;
    private final World world;
    private final BulletFactory bulletFactory;
    private final HealthSystem healthSystem;

    public BulletContactListener(Engine engine, World world, BulletFactory bulletFactory, HealthSystem healthSystem) {
        this.engine = engine;
        this.world = world;
        this.bulletFactory = bulletFactory;
        this.healthSystem = healthSystem;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        // Colisão da bala com o mapa (paredes, chão etc)
        if ((isBulletCollision(bodyA) && isMapCollision(bodyB)) || (isBulletCollision(bodyB) && isMapCollision(bodyA))) {
            Body bulletBody = isBulletCollision(bodyA) ? bodyA : bodyB;
            disposeBullet(bulletBody);
        }

        // Colisão da bala com inimigo
        boolean bulletHitsEnemy = isBulletCollision(bodyA) && isEnemy(bodyB);
        boolean enemyHitsBullet = isBulletCollision(bodyB) && isEnemy(bodyA);

        if (bulletHitsEnemy || enemyHitsBullet) {
            Body bulletBody = bulletHitsEnemy ? bodyA : bodyB;
            Body enemyBody = bulletHitsEnemy ? bodyB : bodyA;

            if (bulletBody.getUserData() instanceof Entity bulletEntity &&
                enemyBody.getUserData() instanceof Entity enemyEntity) {

                ProjectileComponent proj = bulletEntity.getComponent(ProjectileComponent.class);
                int damage = (proj != null && proj.type == ProjectileComponent.ProjectileType.FIREBALL) ? 100 : 10;

                // Aplica dano ao inimigo
                healthSystem.damage(enemyEntity, damage);

                // Libera a bala corretamente
                disposeBullet(bulletBody);
            }
        }
    }

    // Checa se o corpo é um inimigo
    private boolean isEnemy(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("enemy".equals(fixture.getUserData())) {
                return true;
            }
        }
        return false;
    }

    // Checa se o corpo é uma bala (bullet ou fireball)
    private boolean isBulletCollision(Body body) {
        // Ignora se o corpo for entidade inimiga
        if (body.getUserData() instanceof Entity entity) {
            if (entity.getComponent(EnemyComponent.class) != null) {
                return false;  // É inimigo, não bala
            }
        }

        for (Fixture fixture : body.getFixtureList()) {
            Object data = fixture.getUserData();
            if ("bullet".equals(data) || "fireball".equals(data)) {
                return true;
            }
        }
        return false;
    }

    // Checa se o corpo é colisão do mapa
    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) {
                return true;
            }
        }
        return false;
    }

    // Libera a bala do mundo e do engine, thread-safe via postRunnable
    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity bulletEntity) {
            if (bulletEntity.getComponent(EnemyComponent.class) != null) {
                //System.out.println("[BulletContactListener] Tentativa de liberar UFO/inimigo como bala ignorada: " + bulletEntity.hashCode());
                return; // NÃO libera inimigos aqui
            }
            Gdx.app.postRunnable(() -> bulletFactory.free(bulletEntity));
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
