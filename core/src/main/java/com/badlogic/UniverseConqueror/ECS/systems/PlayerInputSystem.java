package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.Utils.Constants;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerInputSystem extends BaseIteratingSystem {

    // Dependências externas
    private final World world;
    private Joystick joystick;
    private final OrthographicCamera camera;
    private final PooledEngine engine;
    private final BulletSystem bulletSystem;
    private final BulletFactory bulletFactory;

    private Entity player;

    // Construtor
    public PlayerInputSystem(World world, Joystick joystick, BulletSystem bulletSystem,
                             OrthographicCamera camera, PooledEngine engine, BulletFactory bulletFactory) {
        super(Family.all(PlayerComponent.class, AttackComponent.class, VelocityComponent.class,
            PhysicsComponent.class, StateComponent.class, AnimationComponent.class).get());
        this.world = world;
        this.joystick = joystick;
        this.camera = camera;
        this.engine = engine;
        this.bulletSystem = bulletSystem;
        this.bulletFactory = bulletFactory;
    }

    // Define a entidade principal do jogador
    public void setPlayer(Entity player) {
        this.player = player;
    }

    public void setJoystick(Joystick joystick) {
        this.joystick = joystick;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        // Obtém os componentes relevantes via BaseIteratingSystem
        StateComponent state = ComponentMappers.state.get(entity);
        PhysicsComponent physics = ComponentMappers.physics.get(entity);
        VelocityComponent velocity = ComponentMappers.velocity.get(entity);
        AnimationComponent animation = ComponentMappers.animation.get(entity);
        HealthComponent health = ComponentMappers.health.get(entity);
        JumpComponent jump = ComponentMappers.jump.get(entity);
        AttackComponent attack = ComponentMappers.attack.get(entity);

        float dx = 0f, dy = 0f;
        boolean isMoving = false;

        // Define velocidade com base na vida e se Shift está pressionado
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float currentSpeed = (health != null && health.currentHealth >= 25f && shift) ? Constants.SPEED_FAST : Constants.SPEED;

        // Movimento via teclado
        boolean keyPressed = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= currentSpeed * deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += currentSpeed * deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += currentSpeed * deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= currentSpeed * deltaTime;
            keyPressed = true;
        }

        // joystick
        if (!keyPressed && joystick != null && joystick.isMoving()) {
            Vector2 dir = joystick.getDirection();
            dx = dir.x;
            dy = dir.y;
        }

        // Verifica se está seguindo caminho automaticamente (via F ou H)
        boolean isFollowingPath = Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.H);

        if (!isFollowingPath) {
            isMoving = dx != 0 || dy != 0;
            if (dx > 0) animation.facingRight = true;
            else if (dx < 0) animation.facingRight = false;
            velocity.velocity.set(dx * Constants.PHYSICS_MULTIPLIER, dy * Constants.PHYSICS_MULTIPLIER);
        } else {
            isMoving = false;
        }

        // Se estiver em estado inválido, ignora inputs
        if (state.get() == StateComponent.State.HURT || state.get() == StateComponent.State.DEATH)
            return;

        // Defesa com TAB
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            state.set(StateComponent.State.DEFENSE);
            EventBus.get().notify(new DefenseEvent(entity));
        }

        // Saltar (SPACE) e Super Ataque (E)
        if (health != null && health.currentHealth >= 25f) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jump.canJump) {
                jump.isJumping = true;
                physics.body.applyForceToCenter(new Vector2(0, Constants.JUMP_FORCE), true);
                EventBus.get().notify(new JumpEvent(entity));
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                state.set(StateComponent.State.SUPER_ATTACK);
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && (joystick == null || !joystick.isMoving())) {
                fireBullet(entity, state.get() == StateComponent.State.SUPER_ATTACK);
            }

            if (isMoving && shift) {
                state.set(StateComponent.State.FAST_MOVE);
                EventBus.get().notify(new FastMoveEvent(entity));
            } else if (isMoving) {
                state.set(StateComponent.State.WALK);
                EventBus.get().notify(new WalkEvent(entity));
            }
        } else {
            if (isMoving) {
                state.set(StateComponent.State.WALK);
                EventBus.get().notify(new WalkEvent(entity));
            }
        }
    }

    // Dispara um projétil normal ou fireball
    private void fireBullet(Entity entity, boolean fireball) {
        AttackComponent attack = ComponentMappers.attack.get(entity);
        if (attack.remainingAttackPower > 0) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 worldPos = camera.unproject(mouse);
            Vector2 direction = new Vector2(worldPos.x, worldPos.y);
            EventBus.get().notify(new ProjectileFiredEvent(entity, direction, fireball));
        }
    }
}
