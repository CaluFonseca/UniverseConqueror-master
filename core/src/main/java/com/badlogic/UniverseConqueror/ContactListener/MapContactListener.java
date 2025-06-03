package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EndGameEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.UniverseConqueror.Interfaces.CollisionListener;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Classe que lida com as colisões no mapa, aplicando danos ao jogador, coletando itens e verificando o fim de nível.
 * Implementa a interface `CollisionListener` para ser utilizada com o Box2D.
 */
public class MapContactListener implements CollisionListener {

    private final ComponentMapper<KnockbackComponent> knockbackMapper = ComponentMapper.getFor(KnockbackComponent.class);  // Mapeador de componentes para o Knockback

    private final Engine engine;
    private final ItemCollectionSystem itemCollectionSystem;
    private final java.util.function.Supplier<Integer> enemiesKilledSupplier;
    private Runnable onEndLevelCallback;

    /**
     * Construtor para inicializar os componentes necessários.
     *
     * @param engine Engine do ECS.
     * @param itemCollectionSystem Sistema de coleta de itens.
     * @param enemiesKilledSupplier Função que fornece o número de inimigos mortos.
     */
    public MapContactListener(Engine engine, ItemCollectionSystem itemCollectionSystem,
                              java.util.function.Supplier<Integer> enemiesKilledSupplier) {
        this.engine = engine;
        this.itemCollectionSystem = itemCollectionSystem;
        this.enemiesKilledSupplier = enemiesKilledSupplier;
    }

    /**
     * Define o callback a ser executado quando o nível terminar.
     *
     * @param callback A ação a ser executada quando o nível terminar.
     */
    public void setOnEndLevel(Runnable callback) {
        this.onEndLevelCallback = callback;
    }

    @Override
    public void beginContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Entity playerEntity = getPlayerEntity(bodyA, bodyB);

        boolean colA = isMapCollision(bodyA);
        boolean colB = isMapCollision(bodyB);

        if (colA || colB) {
            // Se uma colisão com o mapa foi detectada,Knockback

        }

        if (playerEntity == null) return;

        // Verifica se o jogador colidiu com a nave (fim de nível).
        if (isPlayerAndSpaceshipCollision(bodyA, bodyB)) {
            int enemiesKilled = enemiesKilledSupplier.get();
            EventBus.get().notify(new EndGameEvent(playerEntity, enemiesKilled));
            if (onEndLevelCallback != null) onEndLevelCallback.run();
            return;
        }

        // Verifica se o jogador colidiu com um item.
        if (isPlayerAndItemCollision(bodyA, bodyB)) {
            Entity itemEntity = (Entity) bodyB.getUserData();
            collectItem(itemEntity, playerEntity);
            engine.removeEntity(itemEntity);
            return;
        }

        // Caso contrário, aplica dano ao jogador.
        if (!isBulletCollision(bodyA, bodyB) && !isEnemyCollision(bodyA, bodyB)) {
            applyDamageToPlayer(playerEntity, 1);
        }
    }

    @Override
    public void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {

    }

    /**
     * Aplica o efeito de Knockback ao jogador ou a qualquer entidade que colida com o mapa.
     *
     * @param entityBody O corpo da entidade a ser aplicada o efeito de Knockback.
     * @param mapBody O corpo do mapa (objeto com o qual ocorreu a colisão).
     */
    private void applyKnockbackIfEntity(Body entityBody, Body mapBody) {
        Entity entity = getEntity(entityBody);
        if (entity == null) return;


        if (entity.getComponent(KnockbackComponent.class) != null) return;

        PositionComponent pos = entity.getComponent(PositionComponent.class);
        BodyComponent bodyComp = entity.getComponent(BodyComponent.class);

        if (pos == null || bodyComp == null) return;

        Vector2 entityPos = pos.position;
        Vector2 tilePos = mapBody.getPosition();

        Vector2 direction = entityPos.cpy().sub(tilePos).nor();

        KnockbackComponent knockback = new KnockbackComponent();
        knockback.impulse = direction.scl(100f);
        knockback.timeRemaining = 0.3f;
        knockback.duration = 0.3f;
        knockback.hasBeenApplied = false;

        entity.add(knockback);  // Adiciona o componente de Knockback à entidade.
    }

    /**
     * Verifica se houve uma colisão com um inimigo.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se houve colisão com um inimigo.
     */
    private boolean isEnemyCollision(Body bodyA, Body bodyB) {
        return hasComponent(bodyA, EnemyComponent.class) || hasComponent(bodyB, EnemyComponent.class);
    }

    /**
     * Verifica se houve uma colisão com o mapa.
     *
     * @param body O corpo a ser verificado.
     * @return Verdadeiro se o corpo colidiu com o mapa.
     */
    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) return true;
        }
        return false;
    }

    /**
     * Verifica se a entidade possui o componente especificado.
     *
     * @param body O corpo a ser verificado.
     * @param componentClass A classe do componente a ser verificado.
     * @return Verdadeiro se a entidade possui o componente.
     */
    private boolean hasComponent(Body body, Class<? extends Component> componentClass) {
        Object data = body.getUserData();
        return (data instanceof Entity && ((Entity) data).getComponent(componentClass) != null);
    }

    /**
     * Obtém a entidade associada ao corpo.
     *
     * @param body O corpo a ser verificado.
     * @return A entidade associada ao corpo.
     */
    private Entity getEntity(Body body) {
        Object userData = body.getUserData();
        return (userData instanceof Entity) ? (Entity) userData : null;
    }

    /**
     * Obtém a entidade do jogador a partir dos corpos envolvidos na colisão.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return A entidade do jogador, ou null se não for o jogador.
     */
    private Entity getPlayerEntity(Body bodyA, Body bodyB) {
        Entity a = getEntity(bodyA);
        Entity b = getEntity(bodyB);
        if (a != null && a.getComponent(PlayerComponent.class) != null) return a;
        if (b != null && b.getComponent(PlayerComponent.class) != null) return b;
        return null;
    }

    /**
     * Verifica se a colisão foi entre o jogador e a nave (fim do nível).
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi entre o jogador e a nave.
     */
    private boolean isPlayerAndSpaceshipCollision(Body bodyA, Body bodyB) {
        Entity a = getEntity(bodyA);
        Entity b = getEntity(bodyB);

        boolean isPlayerA = a != null && a.getComponent(PlayerComponent.class) != null;
        boolean isPlayerB = b != null && b.getComponent(PlayerComponent.class) != null;
        boolean isShipA = a != null && a.getComponent(EndLevelComponent.class) != null;
        boolean isShipB = b != null && b.getComponent(EndLevelComponent.class) != null;

        return (isPlayerA && isShipB) || (isPlayerB && isShipA);
    }

    /**
     * Verifica se o jogador colidiu com um item.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi com um item.
     */
    private boolean isPlayerAndItemCollision(Body bodyA, Body bodyB) {
        Fixture fixtureB = bodyB.getFixtureList().get(0);
        return "item".equals(fixtureB.getUserData());
    }

    /**
     * Verifica se a colisão foi com uma bala.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi com uma bala ou fireball.
     */
    private boolean isBulletCollision(Body bodyA, Body bodyB) {
        Fixture fixtureB = bodyB.getFixtureList().get(0);
        String tag = String.valueOf(fixtureB.getUserData());
        return "bullet".equals(tag) || "fireball".equals(tag);
    }

    /**
     * Coleta um item.
     *
     * @param item O item a ser coletado.
     * @param player O jogador que coletou o item.
     */
    private void collectItem(Entity item, Entity player) {
        if (item != null && player != null) {
            itemCollectionSystem.collectItem(item, player);
        }
    }

    /**
     * Aplica dano ao jogador.
     *
     * @param playerEntity A entidade do jogador.
     * @param damageAmount O valor do dano a ser aplicado.
     */
    private void applyDamageToPlayer(Entity playerEntity, int damageAmount) {
        if (playerEntity != null) {
            EventBus.get().notify(new DamageTakenEvent(playerEntity, null, damageAmount));
        }
    }
}
