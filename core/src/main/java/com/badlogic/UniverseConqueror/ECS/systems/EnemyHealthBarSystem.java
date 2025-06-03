package com.badlogic.UniverseConqueror.ECS.systems;

// Importa os componentes usados para acessar dados das entidades
import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

// Sistema responsável por desenhar barras de vida sobre os inimigos
public class EnemyHealthBarSystem extends BaseIteratingSystem {

    private final ShapeRenderer shapeRenderer; // Utilizado para desenhar formas básicas (barras)
    private final OrthographicCamera camera;   // Câmera usada para posicionar corretamente a barra no mundo


    // Construtor, define a família de entidades que têm Enemy, Health e Position
    public EnemyHealthBarSystem(OrthographicCamera camera) {
        super(Family.all(EnemyComponent.class, HealthComponent.class, PositionComponent.class).get());
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    // Chamado a cada frame para atualizar e desenhar as barras
    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined); // Atualiza matriz da câmera
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Inicia desenho de formas preenchidas
        super.update(deltaTime); // Itera sobre todas as entidades válidas
        shapeRenderer.end(); // Finaliza o desenho
    }

    // Processa cada inimigo com barra de vida
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = ComponentMappers.health.get(entity);
        PositionComponent position = ComponentMappers.position.get(entity);

        if (health == null || position == null) return; // Segurança
        if (!health.wasDamagedThisFrame && health.currentHealth == health.maxHealth) return; // Se não teve dano e vida cheia, não desenha

        float barWidth = 50f; // Largura da barra
        float barHeight = 10f; // Altura da barra
        float healthPercent = (float) health.currentHealth / health.maxHealth; // Porcentagem da vida

        Vector2 pos = position.position;
        float barX = pos.x - barWidth / 2f; // Centraliza no X
        float barY = pos.y + 90f; // Posição acima do inimigo

        // Desenha fundo da barra (vida perdida) em vermelho
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Desenha parte da vida atual em verde
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, barWidth * healthPercent, barHeight);
    }

    // Libera recursos ao remover o sistema
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        shapeRenderer.dispose(); // Libera GPU
    }
}
