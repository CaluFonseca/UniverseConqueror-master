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
        // SoundComponent sound = smSound.get(entity);
        KnockbackComponent knockback = entity.getComponent(KnockbackComponent.class);

        /// Se estiver sofrendo knockback, ignora input para movimento
        if (knockback != null) {
            return;
        }

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

        /// Checa se teclas F ou H estão pressionadas para desativar movimento
        boolean isFPressed = Gdx.input.isKeyPressed(Input.Keys.F);
        boolean isHPressed = Gdx.input.isKeyPressed(Input.Keys.H);

        if (isFPressed || isHPressed) {
            /// Desativa movimento — mantém velocity atual para não mudar estado
            isMoving = false;
        } else {
            /// Atualiza velocidade e movimento normalmente
            isMoving = dx != 0 || dy != 0;
            velocity.velocity.set(dx * Constants.PHYSICS_MULTIPLIER, dy * Constants.PHYSICS_MULTIPLIER);
        }

        /// Se estado for HURT ou DEATH, ignora input
        if (state.get() == StateComponent.State.HURT || state.get() == StateComponent.State.DEATH) return;

        /// Defesa ativada com TAB
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            state.set(StateComponent.State.DEFENSE);
            EventBus.get().notify(new DefenseEvent(entity));
        }

        /// Lógica para salto, ataque e movimentos especiais só se vida >= 25
        if (health != null && health.currentHealth >= 25f) {
            /// Salto com espaço
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (jump.canJump) {
                    jump.isJumping = true;
                    physics.body.applyForceToCenter(new Vector2(0, Constants.JUMP_FORCE), true);
                    EventBus.get().notify(new JumpEvent(entity));
                    return;
                }
            }

            /// Ativa super ataque com tecla E
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                state.set(StateComponent.State.SUPER_ATTACK);
            }

            /// Atira com botão esquerdo do mouse
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                fireBullet(entity, state.get() == StateComponent.State.SUPER_ATTACK);
            }

            /// Atualiza estado de movimento para rápido ou normal
            if (isMoving && isShiftPressed) {
                state.set(StateComponent.State.FAST_MOVE);
                EventBus.get().notify(new FastMoveEvent(entity));
            } else if (isMoving) {
                state.set(StateComponent.State.WALK);
                EventBus.get().notify(new WalkEvent(entity));
            }
        } else {
            /// Caso vida baixa, pode andar só normal
            if (isMoving) {
                state.set(StateComponent.State.WALK);
                EventBus.get().notify(new WalkEvent(entity));
            }
        }

        // Comentado: ajuste da direção do sprite baseado no movimento horizontal
//        if (dx > 0) animation.facingRight = true;
//        else if (dx < 0) animation.facingRight = false;
    }

    /// Lógica para disparar projéteis, bullet ou fireball
    private void fireBullet(Entity entity, boolean fireball) {
        StateComponent state = sm.get(entity);
        PhysicsComponent physics = phm.get(entity);
        AttackComponent attack = acm.get(entity);
        AnimationComponent animation = am.get(entity);
        //SoundComponent sound = smSound.get(entity);

        /// Só atira se tiver ataque disponível
        if (attack.remainingAttackPower > 0) {
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 mousePosition3D = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));

            TextureRegion currentFrame = animation.currentFrame;
            float frameWidth = currentFrame.getRegionWidth();
            float frameHeight = currentFrame.getRegionHeight();

            float playerX = physics.body.getPosition().x;
            float playerY = physics.body.getPosition().y;
            float originX = playerX + frameWidth / 2f;
            float originY = playerY + frameHeight / 2f - 65f;

            /// Define a direção do personagem olhando para o mouse
            animation.facingRight = mousePosition3D.x >= playerX;

            /// Calcula posição inicial do projétil de acordo com a direção do personagem
            float offsetX = 40f;
            float bulletX = animation.facingRight
                ? playerX + 20f
                : playerX - 120f;

            float bulletYOffset = fireball ? -30f : -10f;
            float bulletY = (playerY + frameHeight * 0.1f) + bulletYOffset;

            Vector2 bulletStartPosition = new Vector2(bulletX, bulletY);

            Vector2 target = new Vector2(mousePosition3D.x, mousePosition3D.y);

            ProjectileComponent.ProjectileType type = fireball
                ? ProjectileComponent.ProjectileType.FIREBALL
                : ProjectileComponent.ProjectileType.BULLET;

            /// Dispara fireball se tiver poder suficiente
            if (fireball) {
                if (attack.remainingAttackPower >= 5) {
                    attack.remainingAttackPower -= 5;
                    state.set(StateComponent.State.SUPER_ATTACK);

                    Entity bullet = bulletFactory.obtainProjectile(
                        world, bulletStartPosition.x, bulletStartPosition.y,
                        target, ProjectileComponent.ProjectileType.FIREBALL
                    );
                    bulletSystem.spawnedFromFactory(bullet);

                    SoundManager.getInstance().play("fireball"); // som de sucesso
                    EventBus.get().notify(new AttackStartedEvent(entity, true));
                } else {
                    SoundManager.getInstance().play("emptyGun"); // som de erro
                    EventBus.get().notify(new NoAmmoEvent(entity));
                }
            } else {
                /// Dispara bullet comum se tiver poder suficiente
                if (attack.remainingAttackPower >= 1) {
                    attack.remainingAttackPower -= 1;
                    state.set(StateComponent.State.ATTACK);

                    Entity bullet = bulletFactory.obtainProjectile(
                        world, bulletStartPosition.x, bulletStartPosition.y,
                        target, ProjectileComponent.ProjectileType.BULLET
                    );
                    bulletSystem.spawnedFromFactory(bullet);

                    SoundManager.getInstance().play("bullet"); // som de sucesso
                    EventBus.get().notify(new AttackStartedEvent(entity, false));
                } else {
                    SoundManager.getInstance().play("emptyGun"); // som de erro
                    EventBus.get().notify(new NoAmmoEvent(entity));
                }
            }

            attack.remainingAttackPower = Math.max(attack.remainingAttackPower, 0);

            /// Ajusta a direção do sprite conforme a posição do projétil
            animation.facingRight = mousePosition3D.x > bulletStartPosition.x;

        } else {
            /// Caso não tenha munição, notifica
            EventBus.get().notify(new NoAmmoEvent(entity));
        }
    }
}
