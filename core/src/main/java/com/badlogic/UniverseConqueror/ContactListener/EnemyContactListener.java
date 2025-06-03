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

    // Construtor padrão
    public EnemyContactListener() {}

    @Override
    public void beginContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Entity entityA = getEntity(fixtureA);  /// Obtém a entidade do primeiro fixture da colisão
        Entity entityB = getEntity(fixtureB);  /// Obtém a entidade do segundo fixture da colisão

        if (entityA == null || entityB == null) return;  /// Se não houver entidades válidas, não faz nada

        // Verifica se uma das entidades é um inimigo e a outra é o jogador
        if (isEnemy(entityA) && isPlayer(entityB)) {
            applyDamageToPlayer(entityB, entityA);  /// Aplica dano ao jogador
        } else if (isEnemy(entityB) && isPlayer(entityA)) {
            applyDamageToPlayer(entityA, entityB);  /// Aplica dano ao jogador
        }
    }

    @Override
    public void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        // Nenhuma lógica adicional no fim do contato
    }

    /**
     * Obtém a entidade associada ao fixture.
     * @param fixture O fixture que contém a entidade.
     * @return A entidade associada ao fixture, ou null se não for uma entidade válida.
     */
    private Entity getEntity(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();  /// Obtém os dados do corpo do fixture
        return userData instanceof Entity ? (Entity) userData : null;  /// Se for uma entidade, retorna ela, caso contrário, retorna null
    }

    /**
     * Verifica se a entidade é um inimigo, verificando a presença do componente EnemyComponent.
     * @param entity A entidade a ser verificada.
     * @return Verdadeiro se a entidade for um inimigo.
     */
    private boolean isEnemy(Entity entity) {
        return entity.getComponent(EnemyComponent.class) != null;  /// Verifica se a entidade possui o componente de inimigo
    }

    /**
     * Verifica se a entidade é o jogador, verificando a presença do componente PlayerComponent.
     * @param entity A entidade a ser verificada.
     * @return Verdadeiro se a entidade for o jogador.
     */
    private boolean isPlayer(Entity entity) {
        return entity.getComponent(PlayerComponent.class) != null;  /// Verifica se a entidade possui o componente do jogador
    }

    /**
     * Aplica dano ao jogador quando ele colide com um inimigo.
     * @param player A entidade do jogador que recebeu o dano.
     * @param enemy A entidade do inimigo que causou o dano.
     */
    private void applyDamageToPlayer(Entity player, Entity enemy) {
        EventBus.get().notify(new DamageTakenEvent(player, enemy, 10));  /// Notifica o sistema de dano com o valor de 10
    }
}
