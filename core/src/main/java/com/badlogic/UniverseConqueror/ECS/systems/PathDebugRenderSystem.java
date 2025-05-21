package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.PathComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class PathDebugRenderSystem extends EntitySystem {
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<PathComponent> pathMapper = ComponentMapper.getFor(PathComponent.class);

    private final Family pathFamily = Family.all(PathComponent.class, PositionComponent.class).get();

    private Engine engine;

    public PathDebugRenderSystem(OrthographicCamera camera) {
        super(-1000);
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;

    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(pathFamily);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : entities) {
            PathComponent path = pathMapper.get(entity);
            Queue<Vector2> waypointsQueue = path.waypoints;

            // Converter para lista para usar índice
            List<Vector2> waypoints = new ArrayList<>(waypointsQueue);
            int total = waypoints.size();

            for (int i = 0; i < total; i++) {
                Vector2 waypoint = waypoints.get(i);

                float alpha = 1f - ((float) i / total);
                Color color;
                if (path.type == PathComponent.PathType.SPACESHIP) {
                    color = new Color(1f, 1f, 0f, alpha); // Amarelo
                } else {
                    color = new Color(1f, 0f, 0f, alpha); // Vermelho
                }
                shapeRenderer.setColor(color);
                shapeRenderer.circle(waypoint.x, waypoint.y, 10f);
            }
        }

        shapeRenderer.end();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        shapeRenderer.dispose();
    }
}
