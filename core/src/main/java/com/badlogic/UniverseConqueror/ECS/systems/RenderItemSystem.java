package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.TextureComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.UfoComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderItemSystem extends EntitySystem {
    private SpriteBatch batch;  /// Batch para desenhar sprites na tela
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);  /// Mapper para TextureComponent
    private ComponentMapper<TransformComponent> trm = ComponentMapper.getFor(TransformComponent.class); /// Mapper para TransformComponent
    private ComponentMapper<ItemComponent> im = ComponentMapper.getFor(ItemComponent.class); /// Mapper para ItemComponent
    private OrthographicCamera camera; /// Câmera para ajustar a projeção

    /// Define família de entidades que tem textura, transformação e são itens, excluindo UFOs
    private Family renderableItems = Family.all(TextureComponent.class, TransformComponent.class, ItemComponent.class)
        .exclude(UfoComponent.class).get();

    /// Construtor recebe o SpriteBatch e a câmera
    public RenderItemSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined); /// Usa a matriz da câmera para projeção correta
        batch.begin();
        /// Percorre todas as entidades que correspondem à família renderableItems
        for (Entity entity : getEngine().getEntitiesFor(renderableItems)) {
            TextureComponent texture = tm.get(entity);
            TransformComponent transform = trm.get(entity);

            /// Se a entidade tem textura, desenha na posição definida pelo transform
            if (texture.texture != null) {
                batch.draw(texture.texture, transform.position.x, transform.position.y);
            }
        }
        batch.end();
    }
}
