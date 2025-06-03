package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletSystem extends EntitySystem {

    private final OrthographicCamera camera;
    private final PooledEngine engine;
    private final BulletFactory bulletFactory;
    private final Array<Entity> activeBullets = new Array<>();
    private final World world;

    public BulletSystem(World world, OrthographicCamera camera, AssetManager assetManager, PooledEngine engine) {
        this.world = world;
        this.camera = camera;
        this.engine = engine;
        this.bulletFactory = new BulletFactory(assetManager, engine);
    }

    @Override
    public void update(float deltaTime) {
        for (int i = activeBullets.size - 1; i >= 0; i--) {
            Entity bullet = activeBullets.get(i);
            PositionComponent position = ComponentMappers.position.get(bullet);
            VelocityComponent velocity = ComponentMappers.velocity.get(bullet);
            PhysicsComponent physics = ComponentMappers.physics.get(bullet);

            if (position == null || velocity == null) {
                activeBullets.removeIndex(i);
                continue;
            }

            if (physics != null && physics.body != null) {
                position.position.set(physics.body.getPosition());
            }

            if (isOutOfBounds(position)) {
                if (physics != null && physics.body != null) {
                    physics.body.setActive(false);
                }
                engine.removeEntity(bullet);
                activeBullets.removeIndex(i);
                bulletFactory.free(bullet);
            }
        }

        // Verificação defensiva de integridade
        for (Entity bullet : activeBullets) {
            if (ComponentMappers.enemy.get(bullet) != null) {
                System.err.println("[ERRO FATAL] Bullet ainda tem EnemyComponent! ID: " + bullet.hashCode());
            }
        }
    }

    private boolean isOutOfBounds(PositionComponent position) {
        float margin = 3000f;
        return position.position.x < camera.position.x - camera.viewportWidth / 2 - margin ||
            position.position.x > camera.position.x + camera.viewportWidth / 2 + margin ||
            position.position.y < camera.position.y - camera.viewportHeight / 2 - margin ||
            position.position.y > camera.position.y + camera.viewportHeight / 2 + margin;
    }

    public void spawnProjectile(float x, float y, Vector2 target, Body body, ProjectileComponent.ProjectileType type) {
        Entity bullet = bulletFactory.obtainProjectile(body.getWorld(), x, y, target, type);
        if (ComponentMappers.projectile.get(bullet) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] ERRO: entidade retornada não tem ProjectileComponent! ID: " + bullet.hashCode());
        }
    }

    public void dispose() {
        // Reservado para futuras liberações
    }

    public void spawnedFromFactory(Entity bullet) {
        if (ComponentMappers.projectile.get(bullet) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] Entidade sem ProjectileComponent adicionada! ID: " + bullet.hashCode());
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        EventBus.get().addObserver(event -> {
            if (event instanceof ProjectileFiredEvent firedEvent) {
                createProjectile(firedEvent.attacker, firedEvent.target, firedEvent.isFireball);
            }
        });
    }

    private void createProjectile(Entity attacker, Vector2 mousePosition, boolean fireball) {
        PhysicsComponent physics = ComponentMappers.physics.get(attacker);
        AnimationComponent animation = ComponentMappers.animation.get(attacker);
        AttackComponent attack = ComponentMappers.attack.get(attacker);
        StateComponent state = ComponentMappers.state.get(attacker);

        if (physics == null || animation == null || attack == null || state == null) return;

        float playerX = physics.body.getPosition().x;
        float playerY = physics.body.getPosition().y;

        animation.facingRight = mousePosition.x >= playerX;

        float frameWidth = animation.currentFrame.getRegionWidth();
        float frameHeight = animation.currentFrame.getRegionHeight();
        float offsetX = animation.facingRight ? 20f : -120f;
        float offsetY = fireball ? -30f : -10f;
        float bulletX = playerX + offsetX;
        float bulletY = playerY + offsetY;

        Vector2 bulletStartPosition = new Vector2(bulletX, bulletY);
        Vector2 target = new Vector2(mousePosition);

        ProjectileComponent.ProjectileType type = fireball
            ? ProjectileComponent.ProjectileType.FIREBALL
            : ProjectileComponent.ProjectileType.BULLET;

        if ((fireball && attack.remainingAttackPower < 5) || (!fireball && attack.remainingAttackPower < 1)) {
            SoundManager.getInstance().play("emptyGun");
            EventBus.get().notify(new NoAmmoEvent(attacker));
            return;
        }

        if (fireball) {
            attack.remainingAttackPower -= 5;
            state.set(StateComponent.State.SUPER_ATTACK);
            SoundManager.getInstance().play("fireball");
        } else {
            attack.remainingAttackPower -= 1;
            state.set(StateComponent.State.ATTACK);
            SoundManager.getInstance().play("bullet");
        }

        attack.remainingAttackPower = Math.max(attack.remainingAttackPower, 0);

        Entity bullet = bulletFactory.obtainProjectile(world, bulletStartPosition.x, bulletStartPosition.y, target, type);
        spawnedFromFactory(bullet);

        EventBus.get().notify(new AttackStartedEvent(attacker, fireball));
    }
}
