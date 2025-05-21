package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;



public class SpaceshipFactory {

    public static Entity createSpaceship(Vector2 position, Engine engine) {
        Entity spaceship = new Entity();

        spaceship.add(new PositionComponent(position));
        spaceship.add(new BoundsComponent(64, 64)); // ajusta se tua sprite for diferente
        spaceship.add(new TargetComponent());
        spaceship.add(new EndLevelComponent());

        // Se quiser adicionar uma textura futuramente:
        TextureComponent tc = new TextureComponent();
       // tc.region = new TextureRegion(assetManager.get(ITEM_SPACESHIP, Texture.class));
        spaceship.add(tc);

        engine.addEntity(spaceship);
        return spaceship;
    }
}
