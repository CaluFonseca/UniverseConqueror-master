package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.GameLauncher;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Utils.Constants;
import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
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
    private final ComponentMapper<SoundComponent > smSound = ComponentMapper.getFor(SoundComponent .class);
    private final World world;
    private final Joystick joystick;
    private OrthographicCamera camera;
    // Bullet System reference
    private PooledEngine engine;
    private BulletSystem bulletSystem;

    // Constructor
    public PlayerInputSystem(World world, Joystick joystick, BulletSystem bulletSystem, OrthographicCamera camera, PooledEngine engine) {
        super(Family.all(PlayerComponent.class, AttackComponent.class, VelocityComponent.class, PhysicsComponent.class, StateComponent.class, AnimationComponent.class).get());
        this.world = world;
        this.joystick = joystick;
        this.bulletSystem = bulletSystem;
        this.camera = camera;
        this.engine = engine;
    }
    private Entity player;

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

        // Verifica se o Shift está pressionado
        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
      //  float currentSpeed = Constants.SPEED;
        float currentSpeed = Constants.SPEED;
        if (isShiftPressed) {
            currentSpeed = Constants.SPEED_FAST; // velocidade aumentada
        }
        // Verifica se há input de teclado
        boolean keyPressed = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= currentSpeed * deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += currentSpeed* deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += currentSpeed* deltaTime;
            keyPressed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= currentSpeed* deltaTime;
            keyPressed = true;
        }

        // Se nenhuma tecla pressionada, usa joystick
        if (!keyPressed && joystick != null && joystick.isMoving()) {
            Vector2 dir = joystick.getDirection(); // vetor normalizado
            dx = dir.x ;
            dy = dir.y ;
        }

        isMoving = dx != 0 || dy != 0;

        // Atualiza a velocidade
        velocity.velocity.set(dx * Constants.PHYSICS_MULTIPLIER, dy * Constants.PHYSICS_MULTIPLIER);

        // Verifica se o estado é HURT ou DEATH
        if (state.get() == StateComponent.State.HURT || state.get() == StateComponent.State.DEATH) {
            return;
        }

        // Handle jump and other states
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

        // Ataque com o botão esquerdo (criação da bala)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            fireBullet(entity, StateComponent.State.SUPER_ATTACK == state.get());
        }
        // Define a animação de movimento
        if (isMoving && isShiftPressed) {
            state.set(StateComponent.State.FAST_MOVE);
        } else if (isMoving) {
            state.set(StateComponent.State.WALK);
            if (sound != null && state.currentState == StateComponent.State.WALK) {
                if (!sound.play) {
                    sound.soundKey = "walk";
                    sound.play = true;
                }
            }
        }

        // Atualiza direção do sprite
        if (dx > 0) {
            animation.facingRight = true;
        } else if (dx < 0) {
            animation.facingRight = false;
        }
    }

    private void fireBullet(Entity entity, boolean fireball)
    {
        StateComponent state = sm.get(entity);
        PhysicsComponent physics = phm.get(entity);
        AttackComponent attack = acm.get(entity);
        AnimationComponent animation = am.get(entity);
        SoundComponent sound = smSound.get(entity);

        if (  attack.remainingAttackPower>0) {
            Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 mousePosition3D = new Vector3(mousePosition.x, mousePosition.y, 0);
           //System.out.println("Mouse Position in World antes da onversao: " + mousePosition3D);
            mousePosition3D = camera.unproject(mousePosition3D);
          //  System.out.println("Mouse Position in World: " + mousePosition3D);
            TextureRegion currentFrame = animation.currentFrame;
            float frameWidth = currentFrame.getRegionWidth() ;
            float frameHeight = currentFrame.getRegionHeight() ;

            float playerX = physics.body.getPosition().x;
            float playerY = physics.body.getPosition().y;

            float originX = (playerX + frameWidth / 2f);
            float originY = (playerY + frameHeight / 2f)-100f;

            float offsetX = animation.facingRight ? -40 : -200;
            float offsetY = 0;

            Vector2 bulletStartPosition = new Vector2(originX + offsetX, originY + offsetY);
            Vector2 direction = new Vector2(mousePosition3D.x, mousePosition3D.y);  // Normaliza a direção para a posição do clique
            // Usa o BulletFactory para criar a bala
            BulletFactory bulletFactory = new BulletFactory(GameLauncher.assetManager);
            try {
                if(!fireball) {
                    state.set(StateComponent.State.ATTACK);
                    if (!sound.play) {
                        sound.soundKey = "attack";
                        sound.play = true;
                    }
                    bulletFactory.createProjectile(engine, world, bulletStartPosition.x, bulletStartPosition.y, direction, ProjectileComponent.ProjectileType.BULLET);
                    attack.remainingAttackPower -= 1;
                    if (attack.remainingAttackPower < 0) {
                        attack.remainingAttackPower = 0;
                    }
                }
                else{
                    if (!sound.play) {
                        sound.soundKey = "superattack";
                        sound.play = true;
                    }
                    bulletFactory.createProjectile(engine, world, bulletStartPosition.x, bulletStartPosition.y, direction, ProjectileComponent.ProjectileType.FIREBALL);
                    attack.remainingAttackPower -= 5;
                    if (attack.remainingAttackPower < 0) {
                        attack.remainingAttackPower = 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao tentar criar a bala: " + e.getMessage());
                e.printStackTrace();
            }

            if (mousePosition3D.x > bulletStartPosition.x) {
                animation.facingRight = true;
            } else {
                animation.facingRight = false;
            }
        }
        else {
            if (sound != null && !sound.play) {
                sound.soundKey = "empty_gun"; // chave lógica usada no SoundSystem
                sound.play = true;
            }
            System.out.println("Fim do nothingbalas");
        }
    }
}
