package com.badlogic.UniverseConqueror.Audio;

import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private final AssetManager assetManager;

    private final HashMap<String, String> soundPaths = new HashMap<>();
    private final HashMap<String, Float> soundCooldowns = new HashMap<>();

    private final Map<Entity, Long> entityLoopIds = new HashMap<>();
    private final Map<Entity, String> entitySoundKeys = new HashMap<>();

    private final float COOLDOWN_TIME = 0.3f;

    private SoundManager(AssetManager assetManager) {
        this.assetManager = assetManager;

        /// Caminhos de som mapeados por chave
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
        soundPaths.put("deathAlien", AssetPaths.SOUND_DEATH_ALIEN);
        soundPaths.put("patrolAlien", AssetPaths.SOUND_PATROL_ALIEN);
        soundPaths.put("chaseAlien", AssetPaths.SOUND_CHASE_ALIEN);
        soundPaths.put("deathUfo", AssetPaths.SOUND_DEATH_UFO);
        soundPaths.put("hurtUfo", AssetPaths.SOUND_HURT_UFO);
        soundPaths.put("chaseUfo", AssetPaths.SOUND_CHASE_UFO);
    }

    /// Inicializa o singleton do SoundManager.
    public static void init(AssetManager assetManager) {
        if (instance == null) instance = new SoundManager(assetManager);
    }

    /// Retorna a instância singleton.
    public static SoundManager getInstance() {
        if (instance == null) throw new IllegalStateException("SoundManager not initialized");
        return instance;
    }

    /// Carrega todos os sons registrados.
    public void loadAll() {
        for (String path : soundPaths.values()) {
            assetManager.load(path, Sound.class);
        }
    }

    /// Toca um som uma única vez, com cooldown para evitar spam.
    public void play(String key) {
        if (key == null) return;

        String path = soundPaths.get(key);
        if (path == null || !assetManager.isLoaded(path)) return;

        float currentTime = com.badlogic.gdx.utils.TimeUtils.nanoTime() / 1_000_000_000f;
        float lastTime = soundCooldowns.getOrDefault(key, 0f);

        if (currentTime - lastTime < COOLDOWN_TIME) return;

        Sound sound = assetManager.get(path, Sound.class);
        sound.play();
        soundCooldowns.put(key, currentTime);
    }

    /// Toca um som em loop contínuo (sem controle por entidade).
    public void loop(String key) {
        String path = soundPaths.get(key);
        if (path != null && assetManager.isLoaded(path)) {
            Sound sound = assetManager.get(path, Sound.class);
            sound.loop();
        }
    }

    /// Para a reprodução de um som específico.
    public void stop(String key) {
        String path = soundPaths.get(key);
        if (path != null && assetManager.isLoaded(path)) {
            Sound sound = assetManager.get(path, Sound.class);
            sound.stop();
        }
    }

    /// Para todos os sons que estão sendo reproduzidos.
    public void stop() {
        for (String key : soundPaths.keySet()) {
            String path = soundPaths.get(key);
            if (path != null && assetManager.isLoaded(path)) {
                Sound sound = assetManager.get(path, Sound.class);
                sound.stop();
            }
        }
    }

    /// Toca um som em loop apenas se a entidade ainda não estiver tocando um som.
    public void loopUnique(Entity entity, String key) {
        if (entity == null || key == null) return;
        if (key.equals(entitySoundKeys.get(entity))) return;

        stopLoopForEntity(entity);

        String path = soundPaths.get(key);
        if (path != null && assetManager.isLoaded(path)) {
            Sound sound = assetManager.get(path, Sound.class);
            long soundId = sound.loop();
            entityLoopIds.put(entity, soundId);
            entitySoundKeys.put(entity, key);
        }
    }

    /// Verifica se o som está em loop por alguma entidade.
    public boolean isLooping(String key) {
        return entitySoundKeys.containsValue(key);
    }

    /// Retorna a chave do som atual da entidade, se houver.
    public String getCurrentLoopKey(Entity entity) {
        return entitySoundKeys.get(entity);
    }

    /// Para o som em loop que está sendo tocado pela entidade.
    public void stopLoopForEntity(Entity entity) {
        if (entity == null) return;

        String key = entitySoundKeys.remove(entity);
        Long soundId = entityLoopIds.remove(entity);

        if (key != null && soundId != null) {
            String path = soundPaths.get(key);
            if (path != null && assetManager.isLoaded(path)) {
                Sound sound = assetManager.get(path, Sound.class);
                sound.stop(soundId);
            }
        }
    }
}
