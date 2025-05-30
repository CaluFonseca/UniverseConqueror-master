package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.UniverseConqueror.ECS.events.*;
import com.badlogic.UniverseConqueror.Interfaces.GameEvent;
import com.badlogic.UniverseConqueror.Interfaces.Observer;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletSystem extends EntitySystem {

    /// Mappers para acessar os componentes das balas
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<PhysicsComponent> em = ComponentMapper.getFor(PhysicsComponent.class);

    /// Câmera usada para verificar se as balas estão fora do ecrã
    private final OrthographicCamera camera;

    /// Engine ECS usada para adicionar/remover entidades
    private final PooledEngine engine;

    /// Fábrica responsável por criar e reutilizar projéteis (bullet pooling)
    private final BulletFactory bulletFactory;

    /// Lista de balas ativas atualmente no mundo
    private final Array<Entity> activeBullets = new Array<>();
    private final World world;

    /// Construtor: recebe câmera, AssetManager e engine
    public BulletSystem(World world, OrthographicCamera camera, AssetManager assetManager, PooledEngine engine) {
        this.world = world;
        this.camera = camera;
        this.engine = engine;
        this.bulletFactory = new BulletFactory(assetManager, engine);
    }

    /// Atualiza as balas a cada frame
    @Override
    public void update(float deltaTime) {
        for (int i = activeBullets.size - 1; i >= 0; i--) {
            Entity bullet = activeBullets.get(i);
            PositionComponent position = pm.get(bullet);
            VelocityComponent velocity = vm.get(bullet);
            PhysicsComponent physics = em.get(bullet);

            /// Se faltam componentes, remove a bala da lista
            if (position == null || velocity == null) {
                activeBullets.removeIndex(i);
                continue;
            }

            /// Atualiza posição da bala com base no corpo físico
            if (physics != null && physics.body != null) {
                position.position.set(physics.body.getPosition());
            }

            /// Remove a bala se estiver fora do campo de visão (com margem)
            if (isOutOfBounds(position)) {
                if (physics != null && physics.body != null) {
                    physics.body.setActive(false);
                }
                engine.removeEntity(bullet);
                activeBullets.removeIndex(i);
                bulletFactory.free(bullet); // devolve ao pool
            }
        }

        /// Debug: garante que nenhuma bala contenha EnemyComponent por engano
        for (Entity bullet : activeBullets) {
            if (bullet.getComponent(EnemyComponent.class) != null) {
                System.err.println("[ERRO FATAL] Bullet ainda tem EnemyComponent! ID: " + bullet.hashCode());
            }
        }
    }

    /// Verifica se a bala saiu do ecrã, com margem generosa
    private boolean isOutOfBounds(PositionComponent position) {
        float margin = 3000f;
        return position.position.x < camera.position.x - camera.viewportWidth / 2 - margin ||
            position.position.x > camera.position.x + camera.viewportWidth / 2 + margin ||
            position.position.y < camera.position.y - camera.viewportHeight / 2 - margin ||
            position.position.y > camera.position.y + camera.viewportHeight / 2 + margin;
    }

    /// Método público para instanciar uma bala no mundo
    public void spawnProjectile(float x, float y, Vector2 target, Body body, ProjectileComponent.ProjectileType type) {
        Entity bullet = bulletFactory.obtainProjectile(body.getWorld(), x, y, target, type);
        if (bullet.getComponent(ProjectileComponent.class) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] ERRO: entidade retornada pelo BulletFactory não tem ProjectileComponent! ID: " + bullet.hashCode());
        }
    }

    /// Método reservado para futuras liberações de recursos
    public void dispose() {
        // Nada a liberar por enquanto
    }

    /// Permite adicionar uma bala gerada externamente (ex: via BulletContactListener)
    public void spawnedFromFactory(Entity bullet) {
        if (bullet.getComponent(ProjectileComponent.class) != null) {
            activeBullets.add(bullet);
        } else {
            System.err.println("[BulletSystem] Tentativa de adicionar entidade sem ProjectileComponent à lista de projéteis: " + bullet.hashCode());
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        EventBus.get().addObserver(new Observer() {
            @Override
            public void onNotify(GameEvent event) {
                if (event instanceof ProjectileFiredEvent) {
                    ProjectileFiredEvent firedEvent = (ProjectileFiredEvent) event;
                    createProjectile(firedEvent.attacker, firedEvent.target, firedEvent.isFireball);
                }
            }
        });


    }

    private void createProjectile(Entity attacker, Vector2 mousePosition, boolean fireball) {
        PhysicsComponent physics = attacker.getComponent(PhysicsComponent.class);
        AnimationComponent animation = attacker.getComponent(AnimationComponent.class);
        AttackComponent attack = attacker.getComponent(AttackComponent.class);
        StateComponent state = attacker.getComponent(StateComponent.class);

        if (physics == null || animation == null || attack == null || state == null) return;

        float playerX = physics.body.getPosition().x;
        float playerY = physics.body.getPosition().y;

// Atualiza a direção do sprite baseado na posição alvo recebida (já em world coords)
        animation.facingRight = mousePosition.x >= playerX;

        // Cálculo da posição inicial do projétil
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

        // Verifica poder de ataque restante
        if ((fireball && attack.remainingAttackPower < 5) ||
            (!fireball && attack.remainingAttackPower < 1)) {

            SoundManager.getInstance().play("emptyGun");
            EventBus.get().notify(new NoAmmoEvent(attacker));
            return;
        }

        // Atualiza valores de ataque
        if (fireball) {
            attack.remainingAttackPower -= 5;
            state.set(StateComponent.State.SUPER_ATTACK);
            SoundManager.getInstance().play("fireball");
        } else {
            attack.remainingAttackPower -= 1;
            state.set(StateComponent.State.ATTACK);
            SoundManager.getInstance().play("bullet");
        }

        // Garante que não fique negativo
        attack.remainingAttackPower = Math.max(attack.remainingAttackPower, 0);

        // Cria e adiciona o projétil
        Entity bullet = bulletFactory.obtainProjectile(world, bulletStartPosition.x, bulletStartPosition.y, target, type);
        spawnedFromFactory(bullet);

        // Notifica que um projétil foi disparado
        EventBus.get().notify(new AttackStartedEvent(attacker, fireball));
    }

}
