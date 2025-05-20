package com.badlogic.UniverseConqueror.State;

import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class SavedItemData {
    public String name;
    public Vector2 position;

    // Default constructor for JSON serialization
    public SavedItemData() {}

    public SavedItemData(String name, Vector2 position) {
        this.name = name;
        this.position =  position.cpy();
    }

    public Entity createEntity(PooledEngine engine, World world, AssetManager assetManager) {
        // Reutiliza o caminho da textura com base no nome
        String texturePath = switch (name) {
            case "Vida" -> AssetPaths.ITEM_VIDA;
            case "Ataque" -> AssetPaths.ITEM_ATAQUE;
            case "SuperAtaque" -> AssetPaths.ITEM_SUPER_ATAQUE;
            default -> throw new IllegalArgumentException("Item desconhecido: " + name);
        };

        ItemFactory factory = new ItemFactory(name, position.x, position.y, texturePath, assetManager);
        return factory.createEntity(engine, world);
    }
}
