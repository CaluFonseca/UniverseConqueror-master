package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.EnumSet;

public class PathFollowSystem extends IteratingSystem {

    public PathFollowSystem() {
        super(Family.all(PathComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PathComponent path = ComponentMappers.path.get(entity);
        PhysicsComponent physics = ComponentMappers.physics.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);
        AnimationComponent animation = ComponentMappers.animation.get(entity);

        if (path.waypoints.isEmpty()) {
            velocity.velocity.setZero(); // para movimento
            return;
        }

        // Obtém o próximo waypoint para onde a entidade deve ir
        Vector2 target = path.waypoints.peek();

        // Obtém a posição atual da entidade pelo corpo físico Box2D
        Vector2 currentPos = physics.body.getPosition();

        // Calcula vetor direção e distância entre posição atual e alvo
        Vector2 direction = new Vector2(target).sub(currentPos);
        float distance = direction.len();

        // Se estiver perto o suficiente do waypoint
        if (distance < 50f) {
            SoundManager.getInstance().play("wayPoint");
            path.waypoints.poll();
            velocity.velocity.setZero();
        } else {
            velocity.velocity.set(direction.nor().scl(100f));
            StateComponent state = entity.getComponent(StateComponent.class);
            if (state != null) {
                boolean isFollowingPath = Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.H);

                if (isFollowingPath) {
                    StateComponent.State current = state.get();
                    // Apenas muda para WALK se não estiver nos estados que bloqueiam o movimento
                    if (current != StateComponent.State.DEATH && current != StateComponent.State.HURT) {
                        state.set(StateComponent.State.WALK);
                    }
                }
            }
        }

        if (animation != null) {
            boolean isFollowingPath = Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.H);
            if (isFollowingPath) {
                float vx = velocity.velocity.x;
                if (vx > 0.01f) {
                    animation.facingRight = true;
                } else if (vx < -0.01f) {
                    animation.facingRight = false;
                }
            }
        }
    }
}
