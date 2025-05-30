package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class UfoRenderSystem extends IteratingSystem {

    private final SpriteBatch batch; // SpriteBatch para desenhar os sprites
    private final OrthographicCamera camera; // Câmera ortográfica para projeção

    // ComponentMappers para acessar componentes facilmente
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<UfoComponent> um = ComponentMapper.getFor(UfoComponent.class);

    // Construtor que recebe SpriteBatch e Câmera
    public UfoRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(UfoComponent.class, TransformComponent.class, AnimationComponent.class).get());
        this.batch = batch;
        this.camera = camera;
    }

    // Atualiza e renderiza todas as entidades processadas pelo sistema
    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined); // Define matriz de projeção da câmera
        batch.begin(); // Começa a desenhar
        super.update(deltaTime); // Processa todas as entidades (chama processEntity)
        batch.end(); // Termina de desenhar
    }

    // Processa cada entidade: atualiza animação e desenha o frame correto no ecrã
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent anim = am.get(entity);
        TransformComponent transform = tm.get(entity);
        StateComponent state = entity.getComponent(StateComponent.class);

        // Verifica se os componentes necessários existem
        if (anim != null && anim.animations != null && state != null) {
            Animation<TextureRegion> currentAnimation = anim.animations.get(state.get());

            if (currentAnimation != null) {
                anim.stateTime += deltaTime; // Atualiza o tempo da animação acumulando deltaTime

                TextureRegion frame = currentAnimation.getKeyFrame(anim.stateTime, true); // Pega o frame atual da animação (loopando)
                anim.currentFrame = frame;

                float x = transform.position.x;
                float y = transform.position.y;

                // Desenha o frame centralizado na posição da entidade
                batch.draw(frame, x - frame.getRegionWidth() / 2f, y - frame.getRegionHeight() / 2f);
            }
        }
    }
}
