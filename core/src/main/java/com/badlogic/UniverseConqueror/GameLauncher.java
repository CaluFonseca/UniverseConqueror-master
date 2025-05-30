/// Classe principal que inicia o jogo. Responsável por carregar recursos, configurar gerenciadores,
/// e definir o ecrã inicial. Estende `Game` da LibGDX.

package com.badlogic.UniverseConqueror;

import com.badlogic.UniverseConqueror.Audio.MusicManager;
import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.State.GameStateManager;
import com.badlogic.UniverseConqueror.Utils.AssetPaths;
import com.badlogic.UniverseConqueror.Screens.GameScreen;
import com.badlogic.UniverseConqueror.Screens.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.badlogic.UniverseConqueror.Utils.AssetPaths.*;

public class GameLauncher extends Game {

    /// SpriteBatch compartilhado para renderizar elementos gráficos.
    public SpriteBatch batch;

    /// Referência para o ecrã principal do jogo.
    private GameScreen gameScreen;

    /// AssetManager estático para acesso global aos recursos.
    public static AssetManager assetManager;

    /// Flag para indicar se o jogo atual é novo (sem carregamento de estado).
    public boolean isNewGame = false;

    /// Método principal de criação do jogo. Carrega recursos e define o ecrã inicial.
    @Override
    public void create() {
        assetManager = new AssetManager();

        // === UI ===
        assetManager.load(UI_SKIN_JSON, Skin.class);
        assetManager.load(UI_SKIN_ATLAS, TextureAtlas.class);

        // === Textures ===
        assetManager.load(BULLET_TEXTURE, Texture.class);
        assetManager.load(FIREBALL_TEXTURE, Texture.class);
        assetManager.load(CROSSHAIR_TEXTURE, Texture.class);

        // === Icons ===
        assetManager.load(CAMERA_ON_ICON, Texture.class);
        assetManager.load(CAMERA_OFF_ICON, Texture.class);

        // === Joystick ===
        assetManager.load(JOYSTICK_BASE, Texture.class);
        assetManager.load(JOYSTICK_KNOB, Texture.class);

        // === Items ===
        assetManager.load(ITEM_VIDA, Texture.class);
        assetManager.load(ITEM_ATAQUE, Texture.class);
        assetManager.load(ITEM_SUPER_ATAQUE, Texture.class);
        assetManager.load(ITEM_SPACESHIP, Texture.class);

        // === Backgrounds ===
        assetManager.load(BACKGROUND_PAUSE, Texture.class);
        assetManager.load(BACKGROUND_MAIN, Texture.class);

        // === Sons gerais ===
        assetManager.load(SOUND_GAME_OVER, Sound.class);
        assetManager.load(SOUND_HOVER, Sound.class);
        assetManager.load(SOUND_CLICK, Sound.class);
        assetManager.load(SOUND_HURT, Sound.class);
        assetManager.load(SOUND_ITEM_PICKUP, Sound.class);
        assetManager.load(SOUND_FLIGHT, Sound.class);
        assetManager.load(SOUND_LASER, Sound.class);
        assetManager.load(SOUND_EMPTY_GUN, Sound.class);
        assetManager.load(SOUND_NEXT_LEVEL, Sound.class);
        assetManager.load(SOUND_WAYPOINT, Sound.class);

        // === Sons de inimigos ===
        assetManager.load(SOUND_DEATH_ALIEN, Sound.class);
        assetManager.load(SOUND_PATROL_ALIEN, Sound.class);
        assetManager.load(SOUND_CHASE_ALIEN, Sound.class);
        assetManager.load(SOUND_DEATH_UFO, Sound.class);
        assetManager.load(SOUND_HURT_UFO, Sound.class);
        assetManager.load(SOUND_CHASE_UFO, Sound.class);

        // === Música de fundo ===
        assetManager.load(MUSIC_SPACE_INTRO, Music.class);

        // === Partículas ===
        assetManager.load(PARTICLE_EXPLOSION, ParticleEffect.class);
        assetManager.load(FIREBALL_PARTICLE_IMAGE, Texture.class);

        // === Animações do jogador ===
        queueAnimationTextures(assetManager);

        // === Inicialização dos gerenciadores de som ===
        SoundManager.init(assetManager);
        MusicManager.init(assetManager);
        SoundManager.getInstance().loadAll();
        MusicManager.getInstance().loadAll();

        loadEnemyAnimations();
        loadUfoAnimations();


        // Finaliza o carregamento de todos os recursos
        assetManager.finishLoading();

        // Inicializa o SpriteBatch compartilhado
        batch = new SpriteBatch();

        // Instancia o ecrã do jogo (mas não a ativa ainda)
        gameScreen = new GameScreen(this, assetManager);

        // Define o menu principal como ecrã inicial
        setScreen(new MainMenuScreen(this, assetManager));
    }
    private void loadEnemyAnimations() {
        loadFormattedFrames(AssetPaths.ENEMY_ATTACK, 17);
        loadFormattedFrames(AssetPaths.ENEMY_DEATH, 9);
        loadFormattedFrames(AssetPaths.ENEMY_WALK, 34);
        loadFormattedFrames(AssetPaths.ENEMY_IDLE, 6);
        loadFormattedFrames(AssetPaths.ENEMY_HURT, 3);
    }

    private void loadUfoAnimations() {
        loadFormattedFrames(AssetPaths.UFO_HURT, 4);
        loadFormattedFrames(AssetPaths.UFO_DEATH, 6);
        loadFormattedFrames(AssetPaths.UFO_FLY, 4);
    }

    private void loadFormattedFrames(String pattern, int count) {
        for (int i = 1; i <= count; i++) {
            assetManager.load(String.format(pattern, i), Texture.class);
        }
    }


    /// Método auxiliar para carregar sequências de animações.
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

    /// Carrega uma sequência de imagens (frames) numeradas para uma animação.
    private static void loadFrames(AssetManager assetManager, String basePath, int count) {
        for (int i = 0; i < count; i++) {
            String path = basePath + String.format("%04d.png", i);
            assetManager.load(path, Texture.class);
        }
    }

    /// Inicia um novo jogo, apagando o estado anterior e reinicializando o ecrã.
    public void startGame() {
        GameStateManager.delete();
        setScreen(new GameScreen(this, assetManager));
    }

    /// Retorna a instância da GameScreen atual.
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    /// Verifica se o jogo atual é uma nova sessão.
    public boolean isNewGame() {
        return isNewGame;
    }

    /// Define o estado de novo jogo.
    public void setNewGame(boolean isNewGame) {
        this.isNewGame = isNewGame;
    }
}
