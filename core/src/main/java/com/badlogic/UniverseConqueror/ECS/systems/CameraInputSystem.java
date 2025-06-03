package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.CameraComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CameraInputSystem extends EntitySystem {

    // Câmera usada para navegação e zoom
    private OrthographicCamera camera;

    // Referência à engine ECS
    private Engine engine;

    // Coordenadas de início do arrasto com o botão direito
    private float dragStartX, dragStartY;

    // Flag para saber se está arrastando a câmera
    private boolean dragging = false;

    // Adaptador de entrada para lidar com eventos de input
    private InputAdapter inputAdapter;

    // Callback opcional disparado ao alternar o modo de seguir o jogador
    private Runnable onCameraToggle;

    // Mapper do componente de câmera
    private final ComponentMapper<CameraComponent> cm = ComponentMapper.getFor(CameraComponent.class);
    private boolean followingPlayer = true;

    // Construtor recebe a câmera e configura o input
    public CameraInputSystem(OrthographicCamera camera) {
        this.camera = camera;
        setupInputProcessor();
    }

    // Quando o sistema é adicionado à engine, guarda referência
    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    // Permite registrar uma função para ser chamada quando a câmera alternar follow mode
    public void setOnCameraToggle(Runnable onCameraToggle) {
        this.onCameraToggle = onCameraToggle;
    }

    // Configura os comportamentos de input
    private void setupInputProcessor() {
        inputAdapter = new InputAdapter() {

            // Zoom com a roda do rato
            @Override
            public boolean scrolled(float amountX, float amountY) {
                float targetZoom = MathUtils.clamp(camera.zoom + amountY * 0.1f, 0.5f, 3f);
                camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.2f);
                return true;
            }

            // Alternar entre seguir ou não o jogador com a tecla C
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.C) {
                    toggleCameraFollow();
                    return true;
                }
                return false;
            }

            // Começo de arrasto com o botão direito
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT && !isFollowingPlayer()) {
                    Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
                    dragStartX = world.x;
                    dragStartY = world.y;
                    dragging = true;
                    return true;
                }
                return false;
            }

            // Durante o arrasto com botão direito
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (dragging && !isFollowingPlayer()) {
                    Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
                    float dx = dragStartX - world.x;
                    float dy = dragStartY - world.y;
                    camera.position.add(dx, dy, 0);
                    dragStartX = world.x;
                    dragStartY = world.y;
                    return true;
                }
                return false;
            }

            // Para o arrasto
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    dragging = false;
                    return true;
                }
                return false;
            }
        };
    }

    // Alterna o modo de seguir ou não o jogador
    public void toggleCameraFollow() {
        ImmutableArray<Entity> cameraEntities = engine.getEntitiesFor(Family.all(CameraComponent.class).get());
        for (Entity entity : cameraEntities) {
            CameraComponent cam = cm.get(entity);
            cam.followPlayer = !cam.followPlayer;
        }

        if (onCameraToggle != null) {
            onCameraToggle.run();
        }
    }

    // Verifica se a câmera está em modo "seguir jogador"
    public boolean isFollowingPlayer() {
        ImmutableArray<Entity> cameraEntities = engine.getEntitiesFor(Family.all(CameraComponent.class).get());
        for (Entity entity : cameraEntities) {
            CameraComponent cam = cm.get(entity);
            return cam.followPlayer;
        }
        return true;
    }

    // Define diretamente se a câmera deve ou não seguir o jogador
    public void setCameraFollow(boolean follow) {
        ImmutableArray<Entity> cameraEntities = engine.getEntitiesFor(Family.all(CameraComponent.class).get());
        for (Entity entity : cameraEntities) {
            CameraComponent cam = cm.get(entity);
            cam.followPlayer = follow;
        }
    }

    // Retorna o adaptador de entrada para registrar
    public InputAdapter getInputAdapter() {
        return inputAdapter;
    }

    public void setFollowingPlayer(boolean follow) {
        this.followingPlayer = follow;
    }

    public boolean toggleFollow() {
        followingPlayer = !followingPlayer;
        return followingPlayer;
    }
}
