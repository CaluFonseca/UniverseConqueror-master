package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
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
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<PhysicsComponent> phm = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<AttackComponent> acm = ComponentMapper.getFor(AttackComponent.class);
    private final ComponentMapper<JumpComponent> jumpMapper = ComponentMapper.getFor(JumpComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<SoundComponent> smSound = ComponentMapper.getFor(SoundComponent.class);

    private final World world;
    private final Joystick joystick;
    private final OrthographicCamera camera;
    private final PooledEngine engine;
    private final BulletSystem bulletSystem;
    private final BulletFactory bulletFactory;

    private Entity player;

    public PlayerInputSystem(World world, Joystick joystick, BulletSystem bulletSystem, OrthographicCamera camera, PooledEngine engine, BulletFactory bulletFactory) {
        super(Family.all(PlayerComponent.class, AttackComponent.class, VelocityComponent.class, PhysicsComponent.class, StateComponent.class, AnimationComponent.class).get());
        this.world = world;
        this.joystick = joystick;
        this.bulletSystem = bulletSystem;
        this.camera = camera;
        this.engine = engine;
        this.bulletFactory = bulletFactory;
    }

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
        SoundComponent sound = smSound.get(entity);

        float dx = 0f, dy = 0f;
        boolean isMoving = false;

        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        float currentSpeed = isShiftPressed ? Constants.SPEED_FAST : Constants.SPEED;

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

        if (!keyPressed && joystick != null && joystick.isMoving()) {
            Vector2 dir = joystick.getDirection();
            dx = dir.x;
            dy = dir.y;
        }

        isMoving = dx != 0 || dy != 0;
        velocity.velocity.set(dx * Constants.PHYSICS_MULTIPLIER, dy * Constants.PHYSICS_MULTIPLIER);

        if (state.get() == StateComponent.State.HURT || state.get() == StateComponent.State.DEATH) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (jump.canJump) {
                jump.isJumping = true;
                state.set(StateComponent.State.JUMP);
                physics.body.applyForceToCenter(new Vector2(0, Constants.JUMP_FORCE), true);
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            state.set(StateComponent.State.SUPER_ATTACK);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            state.set(StateComponent.State.DEFENSE);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            fireBullet(entity, state.get() == StateComponent.State.SUPER_ATTACK);
        }

        if (isMoving && isShiftPressed) {
            state.set(StateComponent.State.FAST_MOVE);
        } else if (isMoving) {
            state.set(StateComponent.State.WALK);

            if (sound != null && state.currentState == StateComponent.State.WALK && !sound.play) {
                sound.soundKey = "walk";
                sound.play = true;
            }
        }

        if (dx > 0) animation.facingRight = true;
        else if (dx < 0) animation.facingRight = false;
    }

    private void fireBullet(Entity entity, boolean fireball) {
        StateComponent state = sm.get(entity);
        PhysicsComponent physics = phm.get(entity);
        AttackComponent attack = acm.get(entity);
        AnimationComponent animation = am.get(entity);
        SoundComponent sound = smSound.get(entity);

        if (attack.remainingAttackPower > 0) {
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 mousePosition3D = camera.unproject(new Vector3(mousePosition.x, mousePosition.y, 0));

            TextureRegion currentFrame = animation.currentFrame;
            float frameWidth = currentFrame.getRegionWidth();
            float frameHeight = currentFrame.getRegionHeight();

            float playerX = physics.body.getPosition().x;
            float playerY = physics.body.getPosition().y;
            float originX = playerX + frameWidth / 2f;
            float originY = playerY + frameHeight / 2f - 100f;

            float offsetX = animation.facingRight ? -40 : -200;
            Vector2 bulletStartPosition = new Vector2(originX + offsetX, originY);
            Vector2 target = new Vector2(mousePosition3D.x, mousePosition3D.y);

            ProjectileComponent.ProjectileType type = fireball
                ? ProjectileComponent.ProjectileType.FIREBALL
                : ProjectileComponent.ProjectileType.BULLET;

           // Entity bullet = bulletFactory.obtainProjectile(world, bulletStartPosition.x, bulletStartPosition.y, target, type);
           // engine.addEntity(bullet);


            if (fireball) {
                // FIREBALL LOGIC
                if (attack.remainingAttackPower >= 5) {
                    attack.remainingAttackPower -= 5;
                    state.set(StateComponent.State.SUPER_ATTACK);

                    Entity bullet = bulletFactory.obtainProjectile(world, bulletStartPosition.x, bulletStartPosition.y, target, ProjectileComponent.ProjectileType.FIREBALL);
                   // engine.addEntity(bullet);
                    bulletSystem.spawnedFromFactory(bullet);

                    if (sound != null && !sound.play) {
                        sound.soundKey = "superattack";
                        sound.play = true;
                    }
                } else {
                    if (sound != null && !sound.play) {
                        sound.soundKey = "empty_gun";
                        sound.play = true;
                    }
                }
            } else {
                // BULLET LOGIC
                if (attack.remainingAttackPower >= 1) {
                    attack.remainingAttackPower -= 1;
                    state.set(StateComponent.State.ATTACK);

                    Entity bullet = bulletFactory.obtainProjectile(world, bulletStartPosition.x, bulletStartPosition.y, target, ProjectileComponent.ProjectileType.BULLET);
                    //engine.addEntity(bullet);
                    bulletSystem.spawnedFromFactory(bullet);

                    if (sound != null && !sound.play) {
                        sound.soundKey = "attack";
                        sound.play = true;
                    }
                } else {
                    if (sound != null && !sound.play) {
                        sound.soundKey = "empty_gun";
                        sound.play = true;
                    }
                }
            }

            attack.remainingAttackPower = Math.max(attack.remainingAttackPower, 0);
            animation.facingRight = mousePosition3D.x > bulletStartPosition.x;

        } else {
            if (sound != null && !sound.play) {
                sound.soundKey = "empty_gun";
                sound.play = true;
            }

        }
    }
}
