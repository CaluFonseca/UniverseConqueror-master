package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.systems.ItemCollectionSystem;
import com.badlogic.UniverseConqueror.Interfaces.CollisionListener;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que gerencia colisões entre balas e outras entidades, como inimigos e o mapa.
 * Implementa a interface `ContactListener` do Box2D.
 */
public class ContactListenerWrapper implements ContactListener {

    private final List<CollisionListener> listeners = new ArrayList<>();  /// Lista que armazena os listeners de colisão.

    private final MapContactListener mapContactListener;  /// Listener para colisões com o mapa.
    private final BulletContactListener bulletContactListener;  /// Listener para colisões de balas.
    private final EnemyContactListener enemyListener;  /// Listener para colisões com inimigos.

    private int enemiesKilledCount;  /// Contador de inimigos mortos.

    /**
     * Construtor que inicializa os listeners de colisão.
     *
     * @param engine Engine do ECS (Sistema de Componentes Entidade).
     * @param itemCollectionSystem Sistema de coleta de itens.
     * @param bulletFactory Fábrica de balas.
     */
    public ContactListenerWrapper(Engine engine, ItemCollectionSystem itemCollectionSystem, BulletFactory bulletFactory) {
        this.mapContactListener = new MapContactListener(engine, itemCollectionSystem, () -> this.enemiesKilledCount);  /// Inicializa o listener de mapa.
        this.bulletContactListener = new BulletContactListener(bulletFactory);  /// Inicializa o listener de balas.
        this.enemyListener = new EnemyContactListener();  /// Inicializa o listener de inimigos.

        listeners.add(mapContactListener);  /// Adiciona o listener de mapa à lista.
        listeners.add(bulletContactListener);  /// Adiciona o listener de balas à lista.
        listeners.add(enemyListener);  /// Adiciona o listener de inimigos à lista.
    }

    /**
     * Define um callback que é executado quando o nível termina.
     *
     * @param callback Ação a ser executada quando o nível terminar.
     */
    public void setOnEndLevel(Runnable callback) {
        mapContactListener.setOnEndLevel(callback);  /// Configura o callback para o listener de mapa.
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();  /// Obtém o primeiro fixture da colisão.
        Fixture fixtureB = contact.getFixtureB();  /// Obtém o segundo fixture da colisão.

        /// Chama o método `beginContact` de cada listener para processar a colisão.
        for (CollisionListener listener : listeners) {
            listener.beginContact(fixtureA, fixtureB, contact);  /// Inicia a colisão para cada listener.
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();  /// Obtém o primeiro fixture quando a colisão termina.
        Fixture fixtureB = contact.getFixtureB();  /// Obtém o segundo fixture quando a colisão termina.

        /// Chama o método `endContact` de cada listener para processar o final da colisão.
        for (CollisionListener listener : listeners) {
            listener.endContact(fixtureA, fixtureB, contact);  /// Finaliza a colisão para cada listener.
        }
    }

    /**
     * Este método é chamado antes da resolução da colisão. Pode ser usado para lógica avançada.
     * (Não utilizado neste código.)
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Pode ser implementado se houver necessidade de lógica adicional antes da resolução da colisão.
    }

    /**
     * Este método é chamado após a resolução da colisão. Pode ser usado para lógica avançada.
     * (Não utilizado neste código.)
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Pode ser implementado se houver necessidade de lógica adicional após a resolução da colisão.
    }
}
