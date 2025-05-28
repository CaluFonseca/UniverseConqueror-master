package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EnemyAnimationLoader {
    private final AssetManager assetManager;

    public EnemyAnimationLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public ObjectMap<StateComponent.State, Animation<TextureRegion>> loadAnimations(String enemyType) {

        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = new ObjectMap<>();

        switch (enemyType.toLowerCase()) {
            case "ufo":
                load(animations, StateComponent.State.CHASE, "ufo/fly/frame-", 4, 0.1f, true);
                load(animations, StateComponent.State.DEATH, "ufo/death/frame-", 4, 0.15f, false);
                load(animations, StateComponent.State.HURT, "ufo/hurt/frame-", 2, 0.1f, false);
                break;

            case "default": // seu inimigo genérico
            default:
                load(animations, StateComponent.State.IDLE, "enemy/idle/frame-", 6, 0.2f, true);
                load(animations, StateComponent.State.PATROL, "enemy/walk/frame-", 6, 0.1f, true);
                load(animations, StateComponent.State.CHASE, "enemy/attack/frame-", 17, 0.1f, true);
                load(animations, StateComponent.State.DEATH, "enemy/death/frame-", 9, 0.1f, false);
                load(animations, StateComponent.State.HURT, "enemy/hurt/frame-", 3, 0.1f, false);
                break;
        }

        return animations;
    }



    private void load(ObjectMap<StateComponent.State, Animation<TextureRegion>> map,
                      StateComponent.State state, String basePath,
                      int frameCount, float frameDuration, boolean loop) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            String filePath = basePath + String.format("%02d.png", i);
            //System.out.println("Loading enemy animation: " + filePath);
            Texture texture = assetManager.get(filePath, Texture.class);
            TextureRegion region = new TextureRegion(texture);
            frames.add(new TextureRegion(region)); // clone para evitar side-effects
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        map.put(state, animation);
    }
}
