package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.Constants;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerInputSystem extends IteratingSystem {
    /// Mappers para acessar os componentes das entidades
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<AttackComponent> acm = ComponentMapper.getFor(AttackComponent.class);
    private final ComponentMapper<JumpComponent> jumpMapper = ComponentMapper.getFor(JumpComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    //private final ComponentMapper<SoundComponent> smSound = ComponentMapper.getFor(SoundComponent.class);

    /// Referências para mundo físico, joystick, câmera, engine, sistema e fábrica de projéteis
    private final World world;
    private final Joystick joystick;
    private final OrthographicCamera camera;
    private final PooledEngine engine;
    private final BulletSystem bulletSystem;
    private final BulletFactory bulletFactory;

    private Entity player; /// Referência à entidade jogador

    /// Construtor inicializa o sistema com as dependências necessárias
    public PlayerInputSystem(World world, Joystick joystick, BulletSystem bulletSystem, OrthographicCamera camera, PooledEngine engine, BulletFactory bulletFactory) {
        super(Family.all(PlayerComponent.class, AttackComponent.class, VelocityComponent.class, PhysicsComponent.class, StateComponent.class, AnimationComponent.class).get());
        this.world = world;
        this.joystick = joystick;
        this.bulletSystem = bulletSystem;
        this.camera = camera;
        this.engine = engine;
        this.bulletFactory = bulletFactory;
    }

    /// Define qual entidade é o jogador para este sistema
    public void setPlayer(Entity player) {
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        StateComponent state = sm.get(entity);
        PhysicsComponent physics = phm.get(entity);
        VelocityComponent velocity = vm.get(entity);
        AnimationComponent animation = am.get(entity);
        HealthComponent health = hm.get(entity);
        JumpComponent jump = jumpMapper.get(entity);


        /// Inicializa variáveis para movimento
        float dx = 0f, dy = 0f;
        boolean isMoving = false;

        /// Detecta se tecla Shift está pressionada para movimento rápido
        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float currentSpeed;
        if (health != null && health.currentHealth >= 25f && isShiftPressed) {
            currentSpeed = Constants.SPEED_FAST; /// Velocidade rápida se vida > 25 e shift pressionado
        } else {
            currentSpeed = Constants.SPEED; /// Velocidade normal
        }

        boolean keyPressed = false;
        /// Checa teclas WASD ou setas para movimento horizontal e vertical
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

        /// Caso nenhuma tecla do teclado, usa joystick se disponível e estiver em movimento
        if (!keyPressed && joystick != null && joystick.isMoving()) {
            Vector2 dir = joystick.getDirection();
            dx = dir.x;
            dy = dir.y;
        }

        // ===TIPO DE MOVIMENTO ===
        boolean isFollowingPath = Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.H);

        if (!isFollowingPath) {
            isMoving = dx != 0 || dy != 0;
            if (dx > 0) animation.facingRight = true;
            else if (dx < 0) animation.facingRight = false;
            velocity.velocity.set(dx * Constants.PHYSICS_MULTIPLIER, dy * Constants.PHYSICS_MULTIPLIER);
        } else {
            isMoving = false;

        }

        // === BLOQUEIO POR ESTADO ===
        if (state.get() == StateComponent.State.HURT || state.get() == StateComponent.State.DEATH) return;

        // === DEFESA ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            state.set(StateComponent.State.DEFENSE);
            EventBus.get().notify(new DefenseEvent(entity));
        }

        // === MOVIMENTOS AVANÇADOS ===
        if (health != null && health.currentHealth >= 25f) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (jump.canJump) {
                    jump.isJumping = true;
                    physics.body.applyForceToCenter(new Vector2(0, Constants.JUMP_FORCE), true);
                    EventBus.get().notify(new JumpEvent(entity));
                    return;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                state.set(StateComponent.State.SUPER_ATTACK);
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                fireBullet(entity, state.get() == StateComponent.State.SUPER_ATTACK);
            }

            if (isMoving && isShiftPressed) {
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

    private void fireBullet(Entity entity, boolean fireball) {
        AttackComponent attack = acm.get(entity);
        if (attack.remainingAttackPower > 0) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 world = camera.unproject(new Vector3(mouse.x, mouse.y, 0));
            EventBus.get().notify(new ProjectileFiredEvent(entity, new Vector2(world.x, world.y), fireball));
        }
    }

}
