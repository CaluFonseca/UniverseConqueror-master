package com.badlogic.UniverseConqueror;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Screens.MainMenuScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.badlogic.UniverseConqueror.Utils.AssetPaths.*;

public class GameLauncher extends Game {
    public SpriteBatch batch;
    private GameScreen gameScreen;
    public static AssetManager assetManager;

    @Override
    public void create() {
        assetManager = new AssetManager();
        // === UI ===
        assetManager.load(AssetPaths.UI_SKIN_JSON, Skin.class);
        assetManager.load(AssetPaths.UI_SKIN_ATLAS, TextureAtlas.class);

        // === Textures ===
        assetManager.load(AssetPaths.BULLET_TEXTURE, Texture.class);
        assetManager.load(AssetPaths.FIREBALL_TEXTURE, Texture.class);
        assetManager.load(AssetPaths.CROSSHAIR_TEXTURE, Texture.class);

        // === Icons ===
        assetManager.load(AssetPaths.CAMERA_ON_ICON, Texture.class);
        assetManager.load(AssetPaths.CAMERA_OFF_ICON, Texture.class);

        // === Joystick ===
        assetManager.load(AssetPaths.JOYSTICK_BASE, Texture.class);
        assetManager.load(AssetPaths.JOYSTICK_KNOB, Texture.class);

        // === Items ===
        assetManager.load(AssetPaths.ITEM_VIDA, Texture.class);
        assetManager.load(AssetPaths.ITEM_ATAQUE, Texture.class);
        assetManager.load(AssetPaths.ITEM_SUPER_ATAQUE, Texture.class);
      //  assetManager.load(AssetPaths.ITEM_SPACESHIP, Texture.class);

        // === Backgrounds ===
        assetManager.load(AssetPaths.BACKGROUND_PAUSE, Texture.class);
        assetManager.load(AssetPaths.BACKGROUND_MAIN, Texture.class);

        // === Sounds ===
        assetManager.load(AssetPaths.SOUND_GAME_OVER, Sound.class);
        assetManager.load(AssetPaths.SOUND_HOVER, Sound.class);
        assetManager.load(AssetPaths.SOUND_CLICK, Sound.class);
        assetManager.load(AssetPaths.SOUND_HURT, Sound.class);
        assetManager.load(AssetPaths.SOUND_ITEM_PICKUP, Sound.class);
        assetManager.load(AssetPaths.SOUND_FLIGHT, Sound.class);
        assetManager.load(AssetPaths.SOUND_LASER, Sound.class);

        // === Music ===
        assetManager.load(AssetPaths.MUSIC_SPACE_INTRO, Music.class);

        // === Particles ===
        assetManager.load(AssetPaths.PARTICLE_EXPLOSION, ParticleEffect.class);
        assetManager.load(AssetPaths.FIREBALL_PARTICLE_IMAGE, Texture.class);

        // === Animations State ===
        queueAnimationTextures(assetManager);

        // === Sound & Music Manager ===
        SoundManager.init(assetManager);
        MusicManager.init(assetManager);
        SoundManager.getInstance().loadAll();
        MusicManager.getInstance().loadAll();

        assetManager.finishLoading();
        batch = new SpriteBatch();
        gameScreen = new GameScreen(this,assetManager);
        setScreen(new MainMenuScreen(this,assetManager));
    }
    public static void queueAnimationTextures(AssetManager assetManager) {
        loadFrames(assetManager, ANIM_IDLE, 2);
        loadFrames(assetManager, ANIM_WALK, 7);
        loadFrames(assetManager, ANIM_FAST_MOVE, 2);
        loadFrames(assetManager, ANIM_JUMP, 2);
        loadFrames(assetManager, ANIM_FALL, 1);
        loadFrames(assetManager, ANIM_DEATH, 3);
        loadFrames(assetManager, ANIM_HURT, 2);
        loadFrames(assetManager, ANIM_ATTACK, 6);
        loadFrames(assetManager, ANIM_SUPER_ATTACK, 3);
        loadFrames(assetManager, ANIM_DEFENSE, 4);
        loadFrames(assetManager, ANIM_WALK_INJURED, 5);
        loadFrames(assetManager, ANIM_IDLE_INJURED, 2);
        loadFrames(assetManager, ANIM_DEFENSE_INJURED, 4);
    }

    private static void loadFrames(AssetManager assetManager, String basePath, int count) {
        for (int i = 0; i < count; i++) {
            String path = basePath + String.format("%04d.png", i);
            assetManager.load(path, Texture.class);
        }
    }
    public void startGame() {
        setScreen(new GameScreen(this,assetManager));
    }
    public GameScreen getGameScreen() {
        return gameScreen;
    }

}
