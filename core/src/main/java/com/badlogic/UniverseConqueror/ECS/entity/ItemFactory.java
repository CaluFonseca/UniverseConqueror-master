package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.TextureComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class ItemFactory {
    private Texture itemTexture;
    private String name;
    private float x, y;
    private String texturePath;
    private Entity entity;
    private final AssetManager assetManager;

    /// Construtor da fábrica de itens.
    public ItemFactory(String name, float x, float y, String texturePath, AssetManager assetManager) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.texturePath = texturePath;
        this.assetManager = assetManager;
        itemTexture = assetManager.get(texturePath, Texture.class);
    }

    /// Cria uma entidade de item e adiciona os componentes necessários.
    public Entity createEntity(PooledEngine engine, World world) {
        entity = engine.createEntity();

        PositionComponent position = engine.createComponent(PositionComponent.class);
        position.position.set(x, y);
        entity.add(position);

        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        transformComponent.position.set(x, y, 0); /// Define a posição tridimensional do item.
        entity.add(transformComponent);

        /// Cria e adiciona o componente de textura para representação visual.
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        textureComponent.texture = itemTexture;
        entity.add(textureComponent);

        /// Cria e adiciona o componente de corpo para colisão (Box2D).
        BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
        bodyComponent.body = createBody(world);
        entity.add(bodyComponent);

        /// Adiciona o componente lógico de item com o nome associado.
        ItemComponent itemComponent = engine.createComponent(ItemComponent.class);
        itemComponent.name = name;
        entity.add(itemComponent);

        return entity;
    }

    /// Cria o corpo físico (Box2D) para o item.
    private Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            itemTexture.getWidth() / 2f,
            itemTexture.getHeight() / 2f,
            new Vector2(itemTexture.getWidth() / 2f, itemTexture.getHeight() / 2f),
            0
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("item");
        body.setUserData(entity);

        shape.dispose();

        return body;
    }
}
