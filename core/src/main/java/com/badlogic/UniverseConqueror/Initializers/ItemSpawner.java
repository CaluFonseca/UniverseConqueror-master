package com.badlogic.UniverseConqueror.Initializers;

import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.ECS.systems.RenderItemSystem;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ItemSpawner {

    /// Engine ECS usada para gerenciar entidades
    private final PooledEngine engine;
    /// Mundo físico do Box2D onde os itens serão colocados
    private final World world;
    /// Gerenciador de recursos para carregar texturas dos itens
    private final AssetManager assetManager;
    /// Câmera usada para renderizar os itens
    private final OrthographicCamera camera;
    /// Estrutura do grafo do mapa, usada para encontrar posições válidas
    private final MapGraphBuilder mapGraphBuilder;

    /// Construtor que inicializa o spawner com todas as dependências necessárias
    public ItemSpawner(PooledEngine engine, World world, AssetManager assetManager,
                       OrthographicCamera camera, MapGraphBuilder mapGraphBuilder) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.camera = camera;
        this.mapGraphBuilder = mapGraphBuilder;
    }

    /// Inicializa os itens no jogo e adiciona o sistema de renderização deles
    public void initializeItems() {
        // Cria 10 de cada tipo de item e os adiciona à engine
        for (int i = 0; i < 10; i++) {
            engine.addEntity(createItem("Vida", AssetPaths.ITEM_VIDA).createEntity(engine, world));
            engine.addEntity(createItem("Ataque", AssetPaths.ITEM_ATAQUE).createEntity(engine, world));
            engine.addEntity(createItem("SuperAtaque", AssetPaths.ITEM_SUPER_ATAQUE).createEntity(engine, world));
        }

        // Cria o sistema responsável por desenhar os itens no ecrã
        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
    }

    /// Cria uma fábrica de item em uma posição aleatória do mapa
    private ItemFactory createItem(String type, String assetPath) {
        // Encontra um nó caminhável aleatório
        Node node = mapGraphBuilder.getRandomWalkableNode();
        // Converte o nó para coordenadas no mundo real
        Vector2 worldPos = mapGraphBuilder.toWorldPosition(node);
        // Cria e retorna a fábrica do item com tipo, posição e textura
        return new ItemFactory(type, worldPos.x, worldPos.y, assetPath, assetManager);
    }
}
