package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.EnemyComponent;
import com.badlogic.UniverseConqueror.ECS.components.ProjectileComponent;
import com.badlogic.UniverseConqueror.ECS.components.UfoComponent;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.Interfaces.CollisionListener;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Classe que escuta as colisões entre as balas e outras entidades do jogo.
 * Processa a lógica de colisões, aplica danos e lida com o descarte das balas.
 */
public class BulletContactListener implements CollisionListener {

    private final BulletFactory bulletFactory;  // Fábrica de balas para gerenciar o reaproveitamento de balas.

    // Construtor que recebe a fábrica de balas
    public BulletContactListener(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    @Override
    public void beginContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        // Caso a bala tenha colidido com o mapa descarta a bala
        if ((isBullet(bodyA) && isMap(bodyB)) || (isBullet(bodyB) && isMap(bodyA))) {
            Body bulletBody = isBullet(bodyA) ? bodyA : bodyB;
            disposeBullet(bulletBody);
        }

        // Caso a bala tenha colidido com um inimigo
        boolean bulletHitsEnemy = isBullet(bodyA) && isEnemy(bodyB);
        boolean enemyHitsBullet = isBullet(bodyB) && isEnemy(bodyA);

        if (bulletHitsEnemy || enemyHitsBullet) {
            Body bulletBody = bulletHitsEnemy ? bodyA : bodyB;
            Body enemyBody = bulletHitsEnemy ? bodyB : bodyA;

            // Verificar se ambos os corpos são entidades válidas
            if (bulletBody.getUserData() instanceof Entity bulletEntity &&
                enemyBody.getUserData() instanceof Entity enemyEntity) {

                // Obter o componente de projétil da bala para determinar o dano
                ProjectileComponent proj = bulletEntity.getComponent(ProjectileComponent.class);
                int baseDamage = (proj != null && proj.type == ProjectileComponent.ProjectileType.FIREBALL) ? 100 : 10;

                // Aumentar o dano se o inimigo for um UFO
                int damage = (enemyEntity.getComponent(UfoComponent.class) != null) ? baseDamage + 20 : baseDamage;

                // Notificar o evento de dano recebido para o inimigo
                EventBus.get().notify(new DamageTakenEvent(enemyEntity, bulletEntity, damage));
                disposeBullet(bulletBody);
            }
        }
    }

    @Override
    public void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        // Nenhuma lógica para o fim de contato de balas
    }

    /**
     * Verifica se o corpo é um inimigo, verificando os fixtures.
     * @param body O corpo a ser verificado.
     * @return Verdadeiro se o corpo for um inimigo.
     */
    private boolean isEnemy(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("enemy".equals(fixture.getUserData())) return true;  // Verifica se o fixture do corpo tem "enemy" como dado
        }
        return false;
    }

    /**
     * Verifica se o corpo é uma bala (ou fireball), verificando seus fixtures.
     * @param body O corpo a ser verificado.
     * @return Verdadeiro se o corpo for uma bala ou fireball.
     */
    private boolean isBullet(Body body) {
        if (body.getUserData() instanceof Entity entity) {
            // Se a entidade for um inimigo, então não é uma bala
            if (entity.getComponent(EnemyComponent.class) != null) return false;
        }
        // Verifica se o fixture tem "bullet" ou "fireball" como dado
        for (Fixture fixture : body.getFixtureList()) {
            Object data = fixture.getUserData();
            if ("bullet".equals(data) || "fireball".equals(data)) return true;
        }
        return false;
    }

    /**
     * Verifica se o corpo pertence ao mapa (superfície não colidível).
     * @param body O corpo a ser verificado.
     * @return Verdadeiro se o corpo pertencer ao mapa.
     */
    private boolean isMap(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) return true;  // Verifica se o fixture tem "map" como dado
        }
        return false;
    }

    /**
     * Descartar a bala após ela ter colidido e causado dano.
     * @param bulletBody O corpo da bala a ser descartada.
     */
    private void disposeBullet(Body bulletBody) {
        if (bulletBody.getUserData() instanceof Entity bulletEntity) {
            // Se a entidade for um inimigo, não deve ser descartada
            if (bulletEntity.getComponent(EnemyComponent.class) != null) return;
            Gdx.app.postRunnable(() -> bulletFactory.free(bulletEntity));  // Descartar a bala usando a BulletFactory
        }
    }
}
