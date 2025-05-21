package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
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

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final Family playerFamily = Family.all(PlayerComponent.class, PositionComponent.class).get();
    private final Family itemFamily = Family.all(ItemComponent.class, PositionComponent.class).get();
    private final Family spaceshipFamily = Family.all(TargetComponent.class, PositionComponent.class).get();

    private Engine engine;

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {

            pathComponent.type = PathComponent.PathType.SPACESHIP;
            ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);
            ImmutableArray<Entity> spaceships = engine.getEntitiesFor(spaceshipFamily);

            if (players.size() == 0 || spaceships.size() == 0) return;

            Entity player = players.first();
            Entity spaceship = spaceships.first(); // Assumimos só uma spaceship

            Vector2 playerPos = pm.get(player).position;
            Vector2 spaceshipPos = pm.get(spaceship).position;

            Node startNode = mapGraphBuilder.getNodeAtWorldPosition(playerPos.x, playerPos.y);
            Node endNode = mapGraphBuilder.getNodeAtWorldPosition(spaceshipPos.x, spaceshipPos.y);

            if (startNode == null || endNode == null) return;

            List<Node> path = pathfinder.findPath(startNode, endNode);

            if (!path.isEmpty()) {
               // PathComponent pathComponent = new PathComponent();
                for (Node node : path) {
                    pathComponent.waypoints.add(mapGraphBuilder.toWorldPosition(node));
                }

                player.remove(PathComponent.class);
                player.add(pathComponent);

                // Optional: log
                System.out.println("Path to spaceship created with " + path.size() + " steps.");
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            pathComponent.type = PathComponent.PathType.ITEM;
            ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);
            ImmutableArray<Entity> items = engine.getEntitiesFor(itemFamily);

            if (players.size() == 0 || items.size() == 0) return;

            Entity player = players.first();
            Entity targetItem = findClosestItem(player, items);
            if (targetItem == null) return;

            Vector2 playerPos = pm.get(player).position;
            Vector2 targetPos = pm.get(targetItem).position;

            Node startNode = mapGraphBuilder.getNodeAtWorldPosition(playerPos.x, playerPos.y);
              Node endNode = mapGraphBuilder.getNodeAtWorldPosition(targetPos.x, targetPos.y);


//            if (startNode == null || endNode == null) {
//                System.out.println("[PathRequestSystem] startNode ou endNode é null.");
//                System.out.println("↪️ Posição do jogador: " + playerPos);
//                System.out.println("↪️ Posição do alvo: " + targetPos);
//                return;
//            }

            if (startNode == null || endNode == null) {
                //System.out.println("[PathRequestSystem] startNode ou endNode é null. Pos player: " + playerPos + ", alvo: " + targetPos);
                return;
            }
            List<Node> path = pathfinder.findPath(startNode, endNode);

            if (!path.isEmpty()) {
               // PathComponent pathComponent = new PathComponent();

                for (Node node : path) {
                    pathComponent.waypoints.add(mapGraphBuilder.toWorldPosition(node));
                }

                if (player.getComponent(PathComponent.class) != null) {
                    player.remove(PathComponent.class);
                }
                player.add(pathComponent);
              //  System.out.println("Caminho gerado com " + path.size() + " passos.");
            }
        }
    }

    private Entity findClosestItem(Entity player, ImmutableArray<Entity> items) {
        Vector2 playerPos = pm.get(player).position;
        Entity closest = null;
        float minDistance = Float.MAX_VALUE;

        for (Entity item : items) {
            Vector2 itemPos = pm.get(item).position;
            float dist = playerPos.dst2(itemPos); // Evita sqrt

            if (dist < minDistance) {
                minDistance = dist;
                closest = item;
            }
        }

        return closest;
    }
}
