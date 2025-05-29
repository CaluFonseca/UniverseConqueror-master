package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class SpaceshipFactory {
    private final AssetManager assetManager;

    public SpaceshipFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /// Cria a entidade da nave espacial, com textura, corpo e componentes necessários
    public Entity createSpaceship(Vector2 position, Engine engine, World world) {
        Entity spaceship = new Entity();

        /// Carrega a textura da nave
        Texture texture = assetManager.get(AssetPaths.ITEM_SPACESHIP, Texture.class);

        /// Adiciona componentes básicos
        PositionComponent positionComponent = new PositionComponent(position);
        spaceship.add(positionComponent);

        TransformComponent transformComponent = new TransformComponent();
        transformComponent.position.set(position.x, position.y, 0);
        spaceship.add(transformComponent);

        TextureComponent textureComponent = new TextureComponent();
        textureComponent.texture = texture;
        spaceship.add(textureComponent);

        spaceship.add(new BoundsComponent(texture.getWidth(), texture.getHeight()));
        spaceship.add(new TargetComponent());
        spaceship.add(new EndLevelComponent());

        /// Cria e adiciona o corpo físico (Box2D)
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.body = createBody(world, position, texture, spaceship);
        spaceship.add(bodyComponent);

        /// Adiciona ao engine
        engine.addEntity(spaceship);
        return spaceship;
    }

    /// Cria o corpo físico da nave (Box2D)
    private Body createBody(World world, Vector2 position, Texture texture, Entity entity) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position.x, position.y);

        Body body = world.createBody(bodyDef);

        /// Define a forma de colisão da nave
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20f, 20f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("spaceship");
        body.setUserData(entity);

        shape.dispose();

        return body;
    }
}
