package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class EnemyHealthBarSystem extends IteratingSystem {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public EnemyHealthBarSystem(OrthographicCamera camera) {
        super(Family.all(EnemyComponent.class, HealthComponent.class, PositionComponent.class).get());
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        super.update(deltaTime);
        shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = hm.get(entity);
        PositionComponent position = pm.get(entity);

        if (health == null || position == null) return;
        if (!health.wasDamagedThisFrame  && health.currentHealth == health.maxHealth) return;

        float barWidth = 50f;
        float barHeight = 10f;
        float healthPercent = (float) health.currentHealth / health.maxHealth;

        Vector2 pos = position.position;
        float barX = pos.x - barWidth / 2f;
        float barY = pos.y + 90f;

        // Fundo (vermelho)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Frente (verde)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * healthPercent, barHeight);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        shapeRenderer.dispose();
    }
}
