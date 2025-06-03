package com.badlogic.UniverseConqueror.State;

import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class SavedItemData {
    /// Nome do item salvo (ex: "Vida", "Ataque", "SuperAtaque")
    public String name;

    /// Posição do item no mundo (coordenadas x,y)
    public Vector2 position;

    /// Construtor padrão necessário para serialização JSON
    public SavedItemData() {}

    /// Construtor completo que cria uma cópia da posição para evitar efeitos colaterais
    public SavedItemData(String name, Vector2 position) {
        this.name = name;
        this.position = position.cpy();
    }

//    /// Cria a entidade do item correspondente a partir dos dados salvos
//    public Entity createEntity(PooledEngine engine, World world, AssetManager assetManager) {
//        // Determina o caminho da textura baseado no nome do item
//        String texturePath = switch (name) {
//            case "Vida" -> AssetPaths.ITEM_VIDA;
//            case "Ataque" -> AssetPaths.ITEM_ATAQUE;
//            case "SuperAtaque" -> AssetPaths.ITEM_SUPER_ATAQUE;
//            default -> throw new IllegalArgumentException("Item desconhecido: " + name);
//        };
//
//        // Cria a entidade usando o ItemFactory e retorna
//        ItemFactory factory = new ItemFactory(name, position.x, position.y, texturePath, assetManager);
//        return factory.createEntity(engine, world);
//    }
}
