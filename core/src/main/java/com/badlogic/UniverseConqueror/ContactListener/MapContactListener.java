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

    private final ComponentMapper<KnockbackComponent> knockbackMapper = ComponentMapper.getFor(KnockbackComponent.class);  /// Mapeador de componentes para o Knockback

    private final Engine engine;  /// Referência à engine do ECS (sistema de entidades e componentes).
    private final ItemCollectionSystem itemCollectionSystem;  /// Sistema responsável pela coleta de itens.
    private final java.util.function.Supplier<Integer> enemiesKilledSupplier;  /// Função que retorna o número de inimigos mortos.
    private Runnable onEndLevelCallback;  /// Callback a ser executado quando o nível terminar.

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
        Body bodyA = fixtureA.getBody();  /// Obtém o primeiro corpo da colisão.
        Body bodyB = fixtureB.getBody();  /// Obtém o segundo corpo da colisão.

        Entity playerEntity = getPlayerEntity(bodyA, bodyB);  /// Verifica se algum dos corpos é o jogador.

        boolean colA = isMapCollision(bodyA);  /// Verifica se o primeiro corpo colidiu com o mapa.
        boolean colB = isMapCollision(bodyB);  /// Verifica se o segundo corpo colidiu com o mapa.

        if (colA || colB) {
            // Se uma colisão com o mapa foi detectada, pode aplicar efeitos de Knockback ou outras ações.
            // Comentado no momento.
        }

        if (playerEntity == null) return;  /// Se não for o jogador, não faz nada.

        // Verifica se o jogador colidiu com a nave (fim de nível).
        if (isPlayerAndSpaceshipCollision(bodyA, bodyB)) {
            int enemiesKilled = enemiesKilledSupplier.get();  /// Obtém o número de inimigos mortos.
            EventBus.get().notify(new EndGameEvent(playerEntity, enemiesKilled));  /// Notifica o fim do jogo.
            if (onEndLevelCallback != null) onEndLevelCallback.run();  /// Executa o callback, se definido.
            return;
        }

        // Verifica se o jogador colidiu com um item.
        if (isPlayerAndItemCollision(bodyA, bodyB)) {
            Entity itemEntity = (Entity) bodyB.getUserData();  /// Obtém a entidade do item.
            collectItem(itemEntity, playerEntity);  /// Coleta o item.
            engine.removeEntity(itemEntity);  /// Remove o item da engine.
            return;
        }

        // Caso contrário, aplica dano ao jogador.
        if (!isBulletCollision(bodyA, bodyB) && !isEnemyCollision(bodyA, bodyB)) {
            applyDamageToPlayer(playerEntity, 1);  /// Aplica dano ao jogador se não for uma colisão com bala ou inimigo.
        }
    }

    @Override
    public void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact) {
        // Não há lógica implementada para o fim do contato no momento.
    }

    /**
     * Aplica o efeito de Knockback ao jogador ou a qualquer entidade que colida com o mapa.
     *
     * @param entityBody O corpo da entidade a ser aplicada o efeito de Knockback.
     * @param mapBody O corpo do mapa (objeto com o qual ocorreu a colisão).
     */
    private void applyKnockbackIfEntity(Body entityBody, Body mapBody) {
        Entity entity = getEntity(entityBody);  /// Obtém a entidade associada ao corpo.
        if (entity == null) return;  /// Se não for uma entidade válida, retorna.

        // Verifica se a entidade já tem o componente Knockback.
        if (entity.getComponent(KnockbackComponent.class) != null) return;

        PositionComponent pos = entity.getComponent(PositionComponent.class);  /// Obtém a posição da entidade.
        BodyComponent bodyComp = entity.getComponent(BodyComponent.class);  /// Obtém o componente do corpo da entidade.

        if (pos == null || bodyComp == null) return;  /// Se não tiver a posição ou o corpo, retorna.

        Vector2 entityPos = pos.position;  /// Posição da entidade.
        Vector2 tilePos = mapBody.getPosition();  /// Posição do mapa (objeto com o qual colidiu).

        Vector2 direction = entityPos.cpy().sub(tilePos).nor();  /// Calcula a direção do impulso (oposta à direção da colisão).

        KnockbackComponent knockback = new KnockbackComponent();  /// Cria o componente de Knockback.
        knockback.impulse = direction.scl(100f);  /// Define a força do impulso.
        knockback.timeRemaining = 0.3f;  /// Define o tempo restante para o efeito de Knockback.
        knockback.duration = 0.3f;  /// Define a duração do efeito de Knockback.
        knockback.hasBeenApplied = false;  /// Define que o efeito ainda não foi aplicado.

        entity.add(knockback);  /// Adiciona o componente de Knockback à entidade.
    }

    /**
     * Verifica se houve uma colisão com um inimigo.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se houve colisão com um inimigo.
     */
    private boolean isEnemyCollision(Body bodyA, Body bodyB) {
        return hasComponent(bodyA, EnemyComponent.class) || hasComponent(bodyB, EnemyComponent.class);  /// Verifica se algum dos corpos é um inimigo.
    }

    /**
     * Verifica se houve uma colisão com o mapa.
     *
     * @param body O corpo a ser verificado.
     * @return Verdadeiro se o corpo colidiu com o mapa.
     */
    private boolean isMapCollision(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            if ("map".equals(fixture.getUserData())) return true;  /// Verifica se o fixture tem "map" como dado.
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
        Object data = body.getUserData();  /// Obtém os dados do corpo.
        return (data instanceof Entity && ((Entity) data).getComponent(componentClass) != null);  /// Verifica se a entidade tem o componente.
    }

    /**
     * Obtém a entidade associada ao corpo.
     *
     * @param body O corpo a ser verificado.
     * @return A entidade associada ao corpo.
     */
    private Entity getEntity(Body body) {
        Object userData = body.getUserData();  /// Obtém os dados do corpo.
        return (userData instanceof Entity) ? (Entity) userData : null;  /// Retorna a entidade se for válida.
    }

    /**
     * Obtém a entidade do jogador a partir dos corpos envolvidos na colisão.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return A entidade do jogador, ou null se não for o jogador.
     */
    private Entity getPlayerEntity(Body bodyA, Body bodyB) {
        Entity a = getEntity(bodyA);  /// Obtém a entidade do primeiro corpo.
        Entity b = getEntity(bodyB);  /// Obtém a entidade do segundo corpo.
        if (a != null && a.getComponent(PlayerComponent.class) != null) return a;  /// Se o primeiro corpo for o jogador, retorna a entidade.
        if (b != null && b.getComponent(PlayerComponent.class) != null) return b;  /// Se o segundo corpo for o jogador, retorna a entidade.
        return null;  /// Retorna null se nenhum corpo for o jogador.
    }

    /**
     * Verifica se a colisão foi entre o jogador e a nave (fim do nível).
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi entre o jogador e a nave.
     */
    private boolean isPlayerAndSpaceshipCollision(Body bodyA, Body bodyB) {
        Entity a = getEntity(bodyA);  /// Obtém a entidade do primeiro corpo.
        Entity b = getEntity(bodyB);  /// Obtém a entidade do segundo corpo.

        boolean isPlayerA = a != null && a.getComponent(PlayerComponent.class) != null;  /// Verifica se o primeiro corpo é o jogador.
        boolean isPlayerB = b != null && b.getComponent(PlayerComponent.class) != null;  /// Verifica se o segundo corpo é o jogador.
        boolean isShipA = a != null && a.getComponent(EndLevelComponent.class) != null;  /// Verifica se o primeiro corpo é a nave de fim de nível.
        boolean isShipB = b != null && b.getComponent(EndLevelComponent.class) != null;  /// Verifica se o segundo corpo é a nave de fim de nível.

        return (isPlayerA && isShipB) || (isPlayerB && isShipA);  /// Verifica se houve colisão entre o jogador e a nave.
    }

    /**
     * Verifica se o jogador colidiu com um item.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi com um item.
     */
    private boolean isPlayerAndItemCollision(Body bodyA, Body bodyB) {
        Fixture fixtureB = bodyB.getFixtureList().get(0);  /// Obtém o fixture do segundo corpo.
        return "item".equals(fixtureB.getUserData());  /// Verifica se o fixture representa um item.
    }

    /**
     * Verifica se a colisão foi com uma bala.
     *
     * @param bodyA O primeiro corpo da colisão.
     * @param bodyB O segundo corpo da colisão.
     * @return Verdadeiro se a colisão foi com uma bala ou fireball.
     */
    private boolean isBulletCollision(Body bodyA, Body bodyB) {
        Fixture fixtureB = bodyB.getFixtureList().get(0);  /// Obtém o fixture do segundo corpo.
        String tag = String.valueOf(fixtureB.getUserData());  /// Obtém o dado do fixture como string.
        return "bullet".equals(tag) || "fireball".equals(tag);  /// Verifica se a colisão foi com uma bala ou fireball.
    }

    /**
     * Coleta um item.
     *
     * @param item O item a ser coletado.
     * @param player O jogador que coletou o item.
     */
    private void collectItem(Entity item, Entity player) {
        if (item != null && player != null) {
            itemCollectionSystem.collectItem(item, player);  /// Coleta o item no sistema de coleta.
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
            EventBus.get().notify(new DamageTakenEvent(playerEntity, null, damageAmount));  /// Notifica o sistema de dano.
        }
    }
}
