package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;
import com.badlogic.UniverseConqueror.Pathfinding.MapGraphBuilder;
import com.badlogic.UniverseConqueror.Pathfinding.Node;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class UfoSpawnerSystem extends EntitySystem {

    private final PooledEngine engine;              // Motor ECS para adicionar/remover entidades
    private final World world;                      // Mundo Box2D para física
    private final AssetManager assetManager;        // Gerenciador de assets para carregar texturas e sons
    private final MapGraphBuilder mapGraphBuilder; // Grafo do mapa para localizar nós válidos para spawn
    private final Entity player;                     // Referência ao jogador para comportamentos dependentes
    private final OrthographicCamera camera;        // Câmera para possíveis cálculos visuais

    private float spawnTimer = 0f;                   // Temporizador para controlar intervalo de spawn
    private final float spawnInterval = 5f;          // Intervalo fixo de 5 segundos entre spawns

    public UfoSpawnerSystem(PooledEngine engine, World world, AssetManager assetManager,
                            MapGraphBuilder mapGraphBuilder, Entity player, OrthographicCamera camera) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.mapGraphBuilder = mapGraphBuilder;
        this.player = player;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;                   // Incrementa o temporizador pelo delta do frame

        if (spawnTimer >= spawnInterval) {        // Se passou o intervalo, tenta spawnar um UFO
            spawnTimer = 0f;                       // Reseta o temporizador

            Node node = getRandomWalkableNode();  // Obtém um nó aleatório válido para spawn no mapa
            if (node != null) {
                Vector2 worldPos = mapGraphBuilder.toWorldPosition(node); // Converte para coordenadas de mundo

                if (worldPos != null) {
                    // Cria o UFO via EnemyFactory. Pode vir reciclado de pool
                    Entity ufoEnemy = EnemyFactory.createUfoEnemy(engine, world, worldPos, assetManager, player, camera);

                    AnimationComponent anim = ufoEnemy.getComponent(AnimationComponent.class);
                    StateComponent state = ufoEnemy.getComponent(StateComponent.class);

                    if (state != null && anim != null) {
                        // Se o UFO reciclado estava em DEATH, reseta para CHASE
                        if (state.get() == StateComponent.State.DEATH) {
                            // Pode colocar logs para debug aqui se quiser
                        }

                        state.set(StateComponent.State.CHASE);  // Define estado ativo
                        anim.stateTime = 0f;                    // Reseta o tempo da animação

                        if (anim.animations.containsKey(StateComponent.State.CHASE)) {
                            anim.currentFrame = anim.animations.get(StateComponent.State.CHASE).getKeyFrame(0f);
                        } else {
                            // Log de erro opcional se faltar animação
                        }
                    }

                    if (ufoEnemy != null) {
                        engine.addEntity(ufoEnemy);           // Adiciona UFO ao motor ECS para processamento
                    } else {
                        // Log de erro opcional se entidade incompleta
                    }
                }
            }
        }
    }

    // Obtém um nó aleatório válido para spawn (chamando o builder do mapa)
    private Node getRandomWalkableNode() {
        return mapGraphBuilder.getRandomWalkableNode();
    }
}
