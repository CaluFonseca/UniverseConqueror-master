package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.EnemyComponent;
import com.badlogic.UniverseConqueror.ECS.components.ProjectileComponent;
import com.badlogic.UniverseConqueror.ECS.components.UfoComponent;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class BulletContactListener implements ContactListener {

    private final BulletFactory bulletFactory;

    public BulletContactListener(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        /// Colisão da bala com o mapa
        if ((isBulletCollision(bodyA) && isMapCollision(bodyB)) || (isBulletCollision(bodyB) && isMapCollision(bodyA))) {
            Body bulletBody = isBulletCollision(bodyA) ? bodyA : bodyB;
            disposeBullet(bulletBody);
        }

        /// Colisão da bala com inimigo
        boolean bulletHitsEnemy = isBulletCollision(bodyA) && isEnemy(bodyB);
        boolean enemyHitsBullet = isBulletCollision(bodyB) && isEnemy(bodyA);

        if (bulletHitsEnemy || enemyHitsBullet) {
            Body bulletBody = bulletHitsEnemy ? bodyA : bodyB;
            Body enemyBody = bulletHitsEnemy ? bodyB : bodyA;

            if (bulletBody.getUserData() instanceof Entity bulletEntity &&
                enemyBody.getUserData() instanceof Entity enemyEntity) {

                ProjectileComponent proj = bulletEntity.getComponent(ProjectileComponent.class);
                int baseDamage = (proj != null && proj.type == ProjectileComponent.ProjectileType.FIREBALL) ? 100 : 10;

                /// Dano adicional para UFO
                int damage = (enemyEntity.getComponent(UfoComponent.class) != null) ? baseDamage + 20 : baseDamage;

                /// Emite evento de dano
                EventBus.get().notify(new DamageTakenEvent(enemyEntity, bulletEntity, damage));

                /// Libera a bala
                disposeBullet(bulletBody);
            }
        }
    }

    /// Verifica se o corpo pertence a um inimigo
    private boolean isEnemy(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("enemy".equals(fixture.getUserData())) return true;
        }
        return false;
    }

    /// Verifica se o corpo pertence a uma bala
    private boolean isBulletCollision(Body body) {
        if (body.getUserData() instanceof Entity entity) {
            if (entity.getComponent(EnemyComponent.class) != null) return false;
        }
        for (Fixture fixture : body.getFixtureList()) {
            Object data = fixture.getUserData();
            if ("bullet".equals(data) || "fireball".equals(data)) return true;
        }
        return false;
    }

    /// Verifica se o corpo é parte do mapa
    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) return true;
        }
        return false;
    }

    /// Libera a bala (devolve ao pool)
    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity bulletEntity) {
            if (bulletEntity.getComponent(EnemyComponent.class) != null) return;
            Gdx.app.postRunnable(() -> bulletFactory.free(bulletEntity));
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
