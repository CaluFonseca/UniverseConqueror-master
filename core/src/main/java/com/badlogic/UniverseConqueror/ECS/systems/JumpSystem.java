package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.IdleEvent;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.ashley.core.*;

/// Sistema responsável por aplicar lógica de pulo no personagem (ou entidade com JumpComponent)
public class JumpSystem extends BaseIteratingSystem {

    private final World world; /// Mundo Box2D necessário para raycast e aplicar força de pulo

    /// Construtor: define a família de entidades que têm Jump, Body e State
    public JumpSystem(World world) {
        super(Family.all(JumpComponent.class, BodyComponent.class, StateComponent.class).get());
        this.world = world;
    }

    /// Processa cada entidade com os componentes necessários
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        JumpComponent jump = ComponentMappers.jump.get(entity);
        BodyComponent body = ComponentMappers.body.get(entity);
        StateComponent state = ComponentMappers.state.get(entity);

        boolean isOnGround = isPlayerOnGround(body); /// Verifica se está no chão via raycast

        /// Se o jogador iniciou pulo e está autorizado a pular, aplica o impulso
        if (jump.isJumping && jump.canJump && isOnGround) {
            body.body.applyLinearImpulse(new Vector2(0, jump.jumpForce), body.body.getWorldCenter(), true);
            jump.isJumping = false;     /// Marca que o pulo foi executado
            jump.canJump = false;       /// Bloqueia novo pulo até aterrissar
            state.set(StateComponent.State.JUMP); /// Atualiza o estado para JUMP
            jump.jumpDuration = 0.6f;   /// Define duração do pulo
        }

        /// Reduz a duração do pulo com o tempo
        if (jump.jumpDuration > 0) {
            jump.jumpDuration -= deltaTime;
        }

        /// Se estiver no chão, pode pular novamente
        if (isOnGround) {
            jump.canJump = true;
        }

        /// Quando o pulo termina e a velocidade horizontal é quase zero, volta ao estado IDLE
        if (jump.jumpDuration <= 0 &&
            body.body.getLinearVelocity().x <= 0 &&
            (state.get() == StateComponent.State.JUMP || state.get() == StateComponent.State.WALK)) {

            state.set(StateComponent.State.IDLE); /// Retorna ao estado parado
            EventBus.get().notify(new IdleEvent(entity)); /// Notifica que voltou ao estado IDLE
        }
    }

    /// Verifica se há chão embaixo da entidade usando um raycast curto para baixo
    private boolean isPlayerOnGround(BodyComponent bodyComponent) {
        final boolean[] grounded = {false}; /// Usado para armazenar resultado do raycast

        Vector2 start = bodyComponent.body.getPosition();            /// Posição atual do corpo
        Vector2 end = new Vector2(start.x, start.y - 0.1f);          /// Raio vai ligeiramente para baixo

        RayCastCallback callback = (fixture, point, normal, fraction) -> {
            grounded[0] = true; /// Se colidir com algo, está no chão
            return 0;           /// Termina o raycast ao encontrar o primeiro fixture
        };

        world.rayCast(callback, start, end); /// Executa raycast no mundo Box2D
        return grounded[0]; /// Retorna resultado do raycast
    }
}
