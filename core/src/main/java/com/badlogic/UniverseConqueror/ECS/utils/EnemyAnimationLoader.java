package com.badlogic.UniverseConqueror.ECS.utils;

import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EnemyAnimationLoader {
    private final AssetManager assetManager;

    // Construtor que recebe o AssetManager
    public EnemyAnimationLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    // Carrega as animações de um inimigo com base no tipo
    public ObjectMap<StateComponent.State, Animation<TextureRegion>> loadAnimations(String enemyType) {
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();

        // Define as animações com base no tipo de inimigo
        switch (enemyType.toLowerCase()) {
            case "ufo":
                // UFO voando (estado CHASE)
                load(animations, StateComponent.State.CHASE, "ufo/fly/frame-", 4, 0.1f, true);
                // Animação de morte
                load(animations, StateComponent.State.DEATH, "ufo/death/frame-", 4, 0.15f, false);
                // Animação de dano
                load(animations, StateComponent.State.HURT, "ufo/hurt/frame-", 2, 0.1f, false);
                break;

            case "default": // inimigo genérico com mais estados
            default:
                // Estado parado
                load(animations, StateComponent.State.IDLE, "enemy/idle/frame-", 6, 0.2f, true);
                // Estado de patrulha
                load(animations, StateComponent.State.PATROL, "enemy/walk/frame-", 6, 0.1f, true);
                // Perseguindo/já atacando
                load(animations, StateComponent.State.CHASE, "enemy/attack/frame-", 17, 0.1f, true);
                // Morte
                load(animations, StateComponent.State.DEATH, "enemy/death/frame-", 9, 0.1f, false);
                // Dano recebido
                load(animations, StateComponent.State.HURT, "enemy/hurt/frame-", 3, 0.1f, false);
                break;
        }
        return animations;
    }

    // Método utilitário para carregar animações com base em prefixo e quantidade de frames
    private void load(ObjectMap<StateComponent.State, Animation<TextureRegion>> map,
                      StateComponent.State state, String basePath,
                      int frameCount, float frameDuration, boolean loop) {

        Array<TextureRegion> frames = new Array<>();

        // Carrega cada frame baseado no nome do arquivo
        for (int i = 1; i <= frameCount; i++) {
            String filePath = basePath + String.format("%02d.png", i);
            Texture texture = assetManager.get(filePath, Texture.class);
            TextureRegion region = new TextureRegion(texture);
            frames.add(new TextureRegion(region));
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        map.put(state, animation);
    }
}
