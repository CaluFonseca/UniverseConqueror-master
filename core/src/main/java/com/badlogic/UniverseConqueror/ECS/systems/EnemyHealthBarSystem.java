package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

// Sistema responsável por desenhar barras de vida sobre os inimigos
public class EnemyHealthBarSystem extends BaseIteratingSystem {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;


    // Construtor, define a família de entidades que tem Enemy, Health e Position
    public EnemyHealthBarSystem(OrthographicCamera camera) {
        super(Family.all(EnemyComponent.class, HealthComponent.class, PositionComponent.class).get());
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    // Chamado a cada frame para atualizar e desenhar as barras
    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        super.update(deltaTime);
        shapeRenderer.end();
    }

    // Processa cada inimigo com barra de vida
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = ComponentMappers.health.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);

        if (health == null || position == null) return;
        if (!health.wasDamagedThisFrame && health.currentHealth == health.maxHealth) return; // Se não teve dano e vida cheia, não desenha

        float barWidth = 50f;
        float barHeight = 10f;
        float healthPercent = (float) health.currentHealth / health.maxHealth;

        Vector2 pos = position.position;
        float barX = pos.x - barWidth / 2f;
        float barY = pos.y + 90f;

        // Desenha fundo da barra em vermelho
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Desenha parte da vida atual em verde
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * healthPercent, barHeight);
    }

    // Liberta recursos ao remover o sistema
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        shapeRenderer.dispose();
    }
}
