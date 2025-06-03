package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.CameraComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraSystem extends EntitySystem {

    // Lista de entidades com componentes de câmera e transformação
    private ImmutableArray<Entity> entities;

    // A câmera que será atualizada
    private OrthographicCamera camera;

    // ComponentMappers para acessar Transform e CameraComponent rapidamente
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<CameraComponent> cm = ComponentMapper.getFor(CameraComponent.class);

    // Dimensões do mapa
    private float mapWidth;
    private float mapHeight;

    // Construtor que recebe a câmera e os limites do mapa
    public CameraSystem(OrthographicCamera camera, float mapWidth, float mapHeight) {
        this.camera = camera;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    // Ao adicionar à engine, pega todas as entidades com Transform e CameraComponent
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, CameraComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // Se não houver entidades com câmera, não faz nada
        if (entities.size() == 0) return;

        // Pega a primeira (e geralmente única) entidade com câmera
        Entity target = entities.first();
        TransformComponent transform = tm.get(target);
        CameraComponent camComp = cm.get(target);

        // Se estiver em modo "seguir jogador", move a câmera suavemente
        if (camComp.followPlayer) {
            Vector3 targetPos = new Vector3(transform.position.x, transform.position.y, 0);

            // Interpola suavemente a posição da câmera até a do jogador
            camera.position.lerp(targetPos, 0.1f);

            // Cálculo de dimensões da câmera com zoom (para limites futuros)
            float camWidth = camera.viewportWidth * camera.zoom;
            float camHeight = camera.viewportHeight * camera.zoom;

            // Limita a câmera às bordas do mapa (comentado atualmente)
            //camera.position.x = MathUtils.clamp(camera.position.x, camWidth / 2f, mapWidth - camWidth / 2f);
            //camera.position.y = MathUtils.clamp(camera.position.y, camHeight / 2f, mapHeight - camHeight / 2f);
        }

        // Atualiza a câmera no final do frame
        camera.update();
    }

    // Getter para expor a câmera atual (útil para renderização)
    public OrthographicCamera getCamera() {
        return camera;
    }

}
