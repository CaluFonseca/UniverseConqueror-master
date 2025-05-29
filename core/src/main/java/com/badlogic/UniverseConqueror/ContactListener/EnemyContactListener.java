package com.badlogic.UniverseConqueror.ContactListener;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.DamageTakenEvent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;

public class EnemyContactListener {

    /// Construtor vazio.
    public EnemyContactListener() {}

    /// Detecta o início de uma colisão.
    public void beginContact(Contact contact) {
        Entity a = getEntity(contact.getFixtureA());
        Entity b = getEntity(contact.getFixtureB());

        if (a == null || b == null) return;

        if (isEnemy(a) && isPlayer(b)) {
            applyDamageToPlayer(b, a);
        } else if (isEnemy(b) && isPlayer(a)) {
            applyDamageToPlayer(a, b);
        }
    }

    /// Fim de contato - não utilizado, mas presente para compatibilidade com a interface.
    public void endContact(Contact contact) {}

    /// Chamado antes da resolução do contato - não utilizado no momento.
    public void preSolve(Contact contact, Manifold oldManifold) {}

    /// Chamado após a resolução do contato - não utilizado no momento.
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    /// Extrai a entidade associada ao corpo da fixture.
    private Entity getEntity(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        return userData instanceof Entity ? (Entity) userData : null;
    }

    /// Verifica se a entidade possui componente de inimigo.
    private boolean isEnemy(Entity entity) {
        return entity.getComponent(EnemyComponent.class) != null;
    }

    /// Verifica se a entidade possui componente de jogador.
    private boolean isPlayer(Entity entity) {
        return entity.getComponent(PlayerComponent.class) != null;
    }

    /// Aplica dano ao jogador (valor fixo de 10).
    private void applyDamageToPlayer(Entity player, Entity enemy) {
        EventBus.get().notify(new DamageTakenEvent(player, enemy, 10));
    }
}
