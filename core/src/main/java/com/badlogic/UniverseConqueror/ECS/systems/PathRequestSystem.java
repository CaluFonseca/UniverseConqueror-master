package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.Pathfinding.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class PathRequestSystem extends EntitySystem {

    private final MapGraphBuilder mapGraphBuilder;
    private final AStarPathfinder pathfinder;


    private final Family playerFamily = Family.all(PlayerComponent.class, PositionComponent.class).get();
    private final Family itemFamily = Family.all(ItemComponent.class, PositionComponent.class).get();
    private final Family spaceshipFamily = Family.all(TargetComponent.class, PositionComponent.class).get();

    private Engine engine;

    // Construtor recebe as referências do construtor do grafo e do pathfinder
    public PathRequestSystem(MapGraphBuilder mapGraphBuilder, AStarPathfinder pathfinder) {
        this.mapGraphBuilder = mapGraphBuilder;
        this.pathfinder = pathfinder;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {

        PathComponent pathComponent = new PathComponent();

        // Se tecla F foi pressionada, caminho até a spaceship
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {

            pathComponent.type = PathComponent.PathType.SPACESHIP;

            // Obtém o player e a spaceship
            ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);
            ImmutableArray<Entity> spaceships = engine.getEntitiesFor(spaceshipFamily);

            if (players.size() == 0 || spaceships.size() == 0) return;

            Entity player = players.first();
            Entity spaceship = spaceships.first();

            Vector2 playerPos = ComponentMappers.position.get(player).position;
            Vector2 spaceshipPos = ComponentMappers.position.get(spaceship).position;

            // Obtém os nodes do grafo para as posições do player e da spaceship
            Node startNode = mapGraphBuilder.getNodeAtWorldPosition(playerPos.x, playerPos.y);
            Node endNode = mapGraphBuilder.getNodeAtWorldPosition(spaceshipPos.x, spaceshipPos.y);

            if (startNode == null || endNode == null) return;

            // Executa A* para encontrar o caminho
            List<Node> path = pathfinder.findPath(startNode, endNode);

            if (!path.isEmpty()) {
                // Converte nodes para posições do mundo e adiciona à fila do caminho
                for (Node node : path) {
                    pathComponent.waypoints.add(mapGraphBuilder.toWorldPosition(node));
                }

                player.remove(PathComponent.class); // Remove caminho antigo se houver
                player.add(pathComponent); // Adiciona novo caminho ao player

            }
        }

        // Se tecla H foi pressionada,  caminho até o item mais próximo
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            pathComponent.type = PathComponent.PathType.ITEM;

            ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);
            ImmutableArray<Entity> items = engine.getEntitiesFor(itemFamily);

            if (players.size() == 0 || items.size() == 0) return;

            Entity player = players.first();
            Entity targetItem = findClosestItem(player, items);
            if (targetItem == null) return;

            Vector2 playerPos = ComponentMappers.position.get(player).position;
            Vector2 targetPos = ComponentMappers.position.get(targetItem).position;


            Node startNode = mapGraphBuilder.getNodeAtWorldPosition(playerPos.x, playerPos.y);
            Node endNode = mapGraphBuilder.getNodeAtWorldPosition(targetPos.x, targetPos.y);

            if (startNode == null || endNode == null) {
                return;
            }

            List<Node> path = pathfinder.findPath(startNode, endNode);

            if (!path.isEmpty()) {
                for (Node node : path) {
                    pathComponent.waypoints.add(mapGraphBuilder.toWorldPosition(node));
                }

                if (player.getComponent(PathComponent.class) != null) {
                    player.remove(PathComponent.class);
                }
                player.add(pathComponent);

            }
        }
    }

    // Método para encontrar o item mais próximo do jogador
    private Entity findClosestItem(Entity player, ImmutableArray<Entity> items) {
        Vector2 playerPos = ComponentMappers.position.get(player).position;
        Entity closest = null;
        float minDistance = Float.MAX_VALUE;

        for (Entity item : items) {
            Vector2 itemPos = ComponentMappers.position.get(item).position;
            float dist = playerPos.dst2(itemPos);

            if (dist < minDistance) {
                minDistance = dist;
                closest = item;
            }
        }

        return closest;
    }
}
