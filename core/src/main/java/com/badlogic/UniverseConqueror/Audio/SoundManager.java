package com.badlogic.UniverseConqueror.Audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;

import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private final AssetManager assetManager;

    private final HashMap<String, String> soundPaths = new HashMap<>();

    private SoundManager(AssetManager assetManager) {
        this.assetManager = assetManager;

        // Registra os sons aqui
        soundPaths.put("jump", AssetPaths.SOUND_JUMP);
        soundPaths.put("hurt", AssetPaths.SOUND_HURT);
        soundPaths.put("item", AssetPaths.SOUND_ITEM_PICKUP);
        soundPaths.put("attack", AssetPaths.SOUND_LASER);
        soundPaths.put("superattack", AssetPaths.SOUND_FIREBALL);
        soundPaths.put("fastmove", AssetPaths.SOUND_FLIGHT);
        soundPaths.put("walk", AssetPaths.SOUND_WALK);
        soundPaths.put("walk_injured", AssetPaths.SOUND_WALK);
        soundPaths.put("death", AssetPaths.SOUND_DEATH);
        soundPaths.put("empty_gun", AssetPaths.SOUND_EMPTY_GUN);
        soundPaths.put("keyboardClick", AssetPaths.SOUND_CLICK);
        soundPaths.put("hoverButton", AssetPaths.SOUND_HOVER);
        soundPaths.put("gameOver", AssetPaths.SOUND_GAME_OVER);
        soundPaths.put("nextLevel", AssetPaths.SOUND_NEXT_LEVEL);
        soundPaths.put("wayPoint", AssetPaths.SOUND_WAYPOINT);
    }

    public static void init(AssetManager assetManager) {
        if (instance == null) instance = new SoundManager(assetManager);
    }

    public static SoundManager getInstance() {
        if (instance == null) throw new IllegalStateException("SoundManager not initialized");
        return instance;
    }

    public void loadAll() {
        for (String path : soundPaths.values()) {
            assetManager.load(path, Sound.class);
        }
    }

    private final HashMap<String, Float> soundCooldowns = new HashMap<>();
    private final float COOLDOWN_TIME = 0.3f; // 300ms de intervalo, ajustável

    public void play(String key) {
        if (key == null) return;

        String path = soundPaths.get(key);
        if (path == null || !assetManager.isLoaded(path)) return;

        float currentTime = com.badlogic.gdx.utils.TimeUtils.nanoTime() / 1_000_000_000f;
        float lastTime = soundCooldowns.getOrDefault(key, 0f);

        if (currentTime - lastTime < COOLDOWN_TIME) return; // ainda em cooldown

        Sound sound = assetManager.get(path, Sound.class);
        sound.play();

        soundCooldowns.put(key, currentTime);
    }


    public void loop(String key) {
        String path = soundPaths.get(key);
        if (path != null && assetManager.isLoaded(path)) {
            Sound sound = assetManager.get(path, Sound.class);
            sound.loop(); // ← repete automaticamente
        }
    }

    public void stop(String key) {
        String path = soundPaths.get(key);
        if (path != null && assetManager.isLoaded(path)) {
            Sound sound = assetManager.get(path, Sound.class);
            sound.stop();
        }
    }
}
