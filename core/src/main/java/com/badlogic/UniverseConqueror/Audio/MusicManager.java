package com.badlogic.UniverseConqueror.Audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;

public class MusicManager {
    private static MusicManager instance;

    private final AssetManager assetManager;
    private final ObjectMap<String, String> musicPaths = new ObjectMap<>();
    private Music currentMusic;
    private String currentKey;
    private float volume = 1.0f;
    private boolean isMuted = false;

    private MusicManager(AssetManager assetManager) {
        this.assetManager = assetManager;


        musicPaths.put("menu", AssetPaths.MUSIC_SPACE_INTRO);
        //musicPaths.put("gameplay", AssetPaths.MUSIC_GAMEPLAY);

    }

    public static void init(AssetManager assetManager) {
        if (instance == null) {
            instance = new MusicManager(assetManager);
        }
    }

    public static MusicManager getInstance() {
        if (instance == null) throw new IllegalStateException("MusicManager not initialized.");
        return instance;
    }

    public void loadAll() {
        for (String path : musicPaths.values()) {
            assetManager.load(path, Music.class);
        }
    }

    public void play(String key, boolean looping) {
        if (key == null || !musicPaths.containsKey(key)) return;

        String path = musicPaths.get(key);
        if (!assetManager.isLoaded(path)) return;

        stop(); // Para qualquer música anterior

        currentMusic = assetManager.get(path, Music.class);
        currentKey = key;

        currentMusic.setLooping(looping);
        currentMusic.setVolume(isMuted ? 0f : volume);
        currentMusic.play();
    }

    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentKey = null;
        }
    }

    public void pause() {
        if (currentMusic != null) currentMusic.pause();
    }

    public void resume() {
        if (currentMusic != null) currentMusic.play();
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (currentMusic != null && !isMuted) {
            currentMusic.setVolume(volume);
        }
    }

    public void mute() {
        isMuted = true;
        if (currentMusic != null) currentMusic.setVolume(0f);
    }

    public void unmute() {
        isMuted = false;
        if (currentMusic != null) currentMusic.setVolume(volume);
    }

    public boolean isMuted() {
        return isMuted;
    }

    public boolean isPlaying(String key) {
        return currentMusic != null && currentKey != null && currentKey.equals(key) && currentMusic.isPlaying();
    }
}
