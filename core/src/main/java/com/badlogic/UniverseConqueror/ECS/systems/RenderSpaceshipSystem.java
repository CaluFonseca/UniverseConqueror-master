package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.TextureComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.EndLevelComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSpaceshipSystem extends EntitySystem {
    private final SpriteBatch batch;  /// Batch para desenhar sprites na tela
    private final OrthographicCamera camera; /// Câmera para projeção correta

    private final ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class); /// Mapper para TextureComponent
    private final ComponentMapper<TransformComponent> trm = ComponentMapper.getFor(TransformComponent.class); /// Mapper para TransformComponent

    /// Família de entidades com textura, transformação e que possuem EndLevelComponent
    private final Family spaceshipFamily = Family.all(TextureComponent.class, TransformComponent.class, EndLevelComponent.class).get();

    /// Construtor recebe SpriteBatch e câmera
    public RenderSpaceshipSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined); /// Define a projeção com base na câmera
        batch.begin();

        /// Para cada entidade da família spaceshipFamily, desenha a textura centralizada
        for (Entity entity : getEngine().getEntitiesFor(spaceshipFamily)) {
            TextureComponent texture = tm.get(entity);
            TransformComponent transform = trm.get(entity);

            if (texture.texture != null) {
                batch.draw(
                    texture.texture,
                    transform.position.x - texture.texture.getWidth() / 2f,   /// Centraliza horizontalmente
                    transform.position.y - texture.texture.getHeight() / 2f   /// Centraliza verticalmente
                );
            }
        }

        batch.end();
    }
}
