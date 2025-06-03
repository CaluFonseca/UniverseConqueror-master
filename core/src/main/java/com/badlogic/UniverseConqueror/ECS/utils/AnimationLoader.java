package com.badlogic.UniverseConqueror.ECS.utils;

import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import static com.badlogic.UniverseConqueror.Utils.AssetPaths.*;

// Classe utilitária responsável por carregar animações associadas aos estados do personagem
public class AnimationLoader {
    private final AssetManager assetManager;

    // Construtor recebe o AssetManager para carregar as texturas
    public AnimationLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    // Carrega todas as animações e retorna um mapa associando estados às animações
    public ObjectMap<StateComponent.State, Animation<TextureRegion>> loadAnimations() {
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();

        // Cada linha representa uma animação associada a um estado do personagem
        load(animations, StateComponent.State.IDLE, ANIM_IDLE, 2, 0.2f, true);
        load(animations, StateComponent.State.WALK, ANIM_WALK, 7, 0.1f, true);
        load(animations, StateComponent.State.FAST_MOVE, ANIM_FAST_MOVE, 2, 0.1f, false);
        load(animations, StateComponent.State.JUMP, ANIM_JUMP, 2, 0.3f, false);
        load(animations, StateComponent.State.FALL, ANIM_FALL, 1, 0.15f, true);
        load(animations, StateComponent.State.DEATH, ANIM_DEATH, 3, 0.15f, false);
        load(animations, StateComponent.State.HURT, ANIM_HURT, 2, 0.1f, false);
        load(animations, StateComponent.State.ATTACK, ANIM_ATTACK, 6, 0.1f, false);
        load(animations, StateComponent.State.SUPER_ATTACK, ANIM_SUPER_ATTACK, 3, 0.1f, false);
        load(animations, StateComponent.State.DEFENSE, ANIM_DEFENSE, 4, 0.1f, false);
        load(animations, StateComponent.State.WALK_INJURED, ANIM_WALK_INJURED, 5, 0.1f, true);
        load(animations, StateComponent.State.IDLE_INJURED, ANIM_IDLE_INJURED, 2, 0.2f, true);
        load(animations, StateComponent.State.DEFENSE_INJURED, ANIM_DEFENSE_INJURED, 4, 0.1f, false);

        return animations;
    }

    // Função genérica para carregar uma sequência de texturas e construir uma animação
    private void load(ObjectMap<StateComponent.State, Animation<TextureRegion>> map,
                      StateComponent.State state, String basePath,
                      int frameCount, float frameDuration, boolean loop) {
        Array<TextureRegion> frames = new Array<>();

        // Carrega cada frame da animação
        for (int i = 0; i < frameCount; i++) {
            String path = basePath + String.format("%04d.png", i);
            Texture texture = assetManager.get(path, Texture.class);
            frames.add(new TextureRegion(texture));
        }

        // Cria a animação com os frames carregados
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);

        // Associa a animação ao estado no mapa
        map.put(state, animation);
    }
}
