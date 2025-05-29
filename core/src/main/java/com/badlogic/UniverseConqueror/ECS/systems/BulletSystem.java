package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.BulletFactory;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletSystem extends EntitySystem {

    /// Mappers para acessar os componentes das balas
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<PhysicsComponent> em = ComponentMapper.getFor(PhysicsComponent.class);

    /// Câmera usada para verificar se as balas estão fora da tela
    private final OrthographicCamera camera;

    /// Engine ECS usada para adicionar/remover entidades
    private final PooledEngine engine;

    /// Fábrica responsável por criar e reutilizar projéteis (bullet pooling)
    private final BulletFactory bulletFactory;

    /// Lista de balas ativas atualmente no mundo
    private final Array<Entity> activeBullets = new Array<>();

    /// Construtor: recebe câmera, AssetManager e engine
    public BulletSystem(OrthographicCamera camera, AssetManager assetManager, PooledEngine engine) {
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

    /// Verifica se a bala saiu da tela, com margem generosa
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
}
