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
    }

    // Inicializa o MusicManager com o AssetManager.
    public static void init(AssetManager assetManager) {
        if (instance == null) {
            instance = new MusicManager(assetManager);
        }
    }

    // Obtém a instância singleton do MusicManager.
    public static MusicManager getInstance() {
        if (instance == null)
            throw new IllegalStateException("MusicManager not initialized.");
        return instance;
    }

    // Carrega todas as músicas registradas no mapa.
    public void loadAll() {
        for (String path : musicPaths.values()) {
            assetManager.load(path, Music.class);
        }
    }

    // Reproduz a música correspondente.
    public void play(String key, boolean looping) {
        if (key == null || !musicPaths.containsKey(key)) return;

        String path = musicPaths.get(key);
        if (!assetManager.isLoaded(path)) return;

        stop();

        currentMusic = assetManager.get(path, Music.class);
        currentKey = key;

        currentMusic.setLooping(looping);
        currentMusic.setVolume(isMuted ? 0f : volume);
        currentMusic.play();
    }

    // Para a música atual e limpa os dados associados.
    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentKey = null;
        }
    }

    // Pausa a música atual.
    public void pause() {
        if (currentMusic != null) currentMusic.pause();
    }

    // Retoma a música atual pausada.
    public void resume() {
        if (currentMusic != null) currentMusic.play();
    }

    // Define o volume da música, se não estiver em modo mudo.
    public void setVolume(float volume) {
        this.volume = volume;
        if (currentMusic != null && !isMuted) {
            currentMusic.setVolume(volume);
        }
    }

    // Ativa o modo mudo.
    public void mute() {
        isMuted = true;
        if (currentMusic != null) currentMusic.setVolume(0f);
    }

    // Desativa o modo mudo e restaura o volume anterior.
    public void unmute() {
        isMuted = false;
        if (currentMusic != null) currentMusic.setVolume(volume);
    }

    // Retorna true se o modo mudo estiver ativado.
    public boolean isMuted() {
        return isMuted;
    }

    // Verifica se uma música com a chave especificada está sendo tocada.
    public boolean isPlaying(String key) {
        return currentMusic != null && currentKey != null &&
            currentKey.equals(key) && currentMusic.isPlaying();
    }
}
