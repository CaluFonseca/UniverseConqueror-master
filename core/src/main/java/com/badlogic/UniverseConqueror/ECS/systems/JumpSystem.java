package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.JumpComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.IdleEvent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class JumpSystem extends EntitySystem {

    /// Mappers para acessar componentes relevantes
    private ComponentMapper<JumpComponent> jm = ComponentMapper.getFor(JumpComponent.class);
    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    private ImmutableArray<Entity> entities;
    private World world;

    public JumpSystem(World world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        /// Seleciona todas as entidades com componentes Jump e Body
        entities = engine.getEntitiesFor(Family.all(JumpComponent.class, BodyComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            JumpComponent jump = jm.get(entity);
            BodyComponent body = bm.get(entity);
            StateComponent state = sm.get(entity);

            boolean isOnGround = isPlayerOnGround(body); /// Verifica se o jogador está no chão

            /// Se está tentando pular e está no chão
            if (jump.isJumping && jump.canJump && isOnGround) {
                /// Aplica impulso para cima
                body.body.applyLinearImpulse(new Vector2(0, jump.jumpForce), body.body.getWorldCenter(), true);
                jump.isJumping = false;
                jump.canJump = false;
                state.currentState = StateComponent.State.JUMP;
                jump.jumpDuration = 0.6f;
            }

            /// Reduz a duração do pulo a cada frame
            if (jump.jumpDuration > 0) {
                jump.jumpDuration -= deltaTime;
            }

            /// Se estiver no chão, permite pular novamente
            if (isOnGround) {
                jump.canJump = true;
            }

            /// Quando o pulo termina, troca para estado IDLE
            if (jump.jumpDuration <= 0 &&
                body.body.getLinearVelocity().x <= 0 &&
                (state.currentState == StateComponent.State.JUMP || state.currentState == StateComponent.State.WALK)) {

                state.set(StateComponent.State.IDLE);
                EventBus.get().notify(new IdleEvent(entity));
            }
        }
    }

    /// Verifica se há chão embaixo do corpo usando RayCast
    private boolean isPlayerOnGround(BodyComponent bodyComponent) {
        Vector2 rayStart = new Vector2(bodyComponent.body.getPosition().x, bodyComponent.body.getPosition().y);
        Vector2 rayEnd = new Vector2(rayStart.x, rayStart.y - 0.1f); /// Raio pequeno para baixo

        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                /// Se colidir com algo, considera que está no chão
                return 1;
            }
        };

        world.rayCast(callback, rayStart, rayEnd);

        return true;
    }
}
