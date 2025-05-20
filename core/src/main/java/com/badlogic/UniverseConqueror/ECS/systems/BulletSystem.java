package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class BulletSystem extends EntitySystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<PhysicsComponent> em = ComponentMapper.getFor(PhysicsComponent.class);


    private Texture bulletTexture;
    private OrthographicCamera camera;

    public BulletSystem(OrthographicCamera camera, AssetManager assetManager) {
        bulletTexture =assetManager.get(AssetPaths.BULLET_TEXTURE, Texture.class);;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        // Atualiza a posição das balas com base na sua velocidade
        for (Entity entity : getEngine().getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class, TransformComponent.class).get())) {
            PositionComponent position = pm.get(entity);
            VelocityComponent velocity = vm.get(entity);
            PhysicsComponent physics = em.get(entity);
            position.position.mulAdd(velocity.velocity, deltaTime);

            if (isOutOfBounds(position)) {
                if (physics.body != null) {
                    physics.body.getWorld().destroyBody(physics.body);  // Destroy the body from the Box2D world
                }
                getEngine().removeEntity(entity);
           }
            else {

           }
        }
    }

    // Method to check if a bullet is out of bounds
    private boolean isOutOfBounds(PositionComponent position) {
        float margin = 100f;
        return position.position.x < camera.position.x - camera.viewportWidth / 2 - margin ||
                position.position.x > camera.position.x + camera.viewportWidth / 2 + margin ||
                position.position.y < camera.position.y - camera.viewportHeight / 2 - margin ||
                position.position.y > camera.position.y + camera.viewportHeight / 2 + margin;
    }

    // Método para descarregar recursos
    public void dispose() {
        if (bulletTexture != null) {
            bulletTexture.dispose();
        }
    }
}
