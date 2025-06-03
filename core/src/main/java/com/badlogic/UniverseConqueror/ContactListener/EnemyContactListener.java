package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.EnemyComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.Interfaces.CollisionListener;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Este listener lida com as colisões entre inimigos e o jogador.
 * Quando um inimigo colide com o jogador, o jogador recebe dano.
 */
public class EnemyContactListener implements CollisionListener {

    public EnemyContactListener() {}

    @Override
    public void beginContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Entity entityA = getEntity(fixtureA);
        Entity entityB = getEntity(fixtureB);

        if (entityA == null || entityB == null) return;

        // Verifica se uma das entidades é um inimigo e a outra é o jogador
        if (isEnemy(entityA) && isPlayer(entityB)) {
            applyDamageToPlayer(entityB, entityA);
        } else if (isEnemy(entityB) && isPlayer(entityA)) {
            applyDamageToPlayer(entityA, entityB);
        }
    }

    @Override
    public void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {

    }

    /**
     * Obtém a entidade associada ao fixture.
     * @param fixture O fixture que contém a entidade.
     * @return A entidade associada ao fixture, ou null se não for uma entidade válida.
     */
    private Entity getEntity(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        return userData instanceof Entity ? (Entity) userData : null;
    }

    /**
     * Verifica se a entidade é um inimigo, verificando a presença do componente EnemyComponent.
     * @param entity A entidade a ser verificada.
     * @return Verdadeiro se a entidade for um inimigo.
     */
    private boolean isEnemy(Entity entity) {
        return entity.getComponent(EnemyComponent.class) != null;
    }

    /**
     * Verifica se a entidade é o jogador, verificando a presença do componente PlayerComponent.
     * @param entity A entidade a ser verificada.
     * @return Verdadeiro se a entidade for o jogador.
     */
    private boolean isPlayer(Entity entity) {
        return entity.getComponent(PlayerComponent.class) != null;
    }

    /**
     * Aplica dano ao jogador quando ele colide com um inimigo.
     * @param player A entidade do jogador que recebeu o dano.
     * @param enemy A entidade do inimigo que causou o dano.
     */
    private void applyDamageToPlayer(Entity player, Entity enemy) {
        EventBus.get().notify(new DamageTakenEvent(player, enemy, 10));
    }
}
