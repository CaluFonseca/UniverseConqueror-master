/// Classe utilitária que define caminhos constantes para os recursos do jogo.
/// Inclui texturas, sons, partículas, animações e arquivos de UI.

package com.badlogic.UniverseConqueror.Utils;

public class AssetPaths {

    /// Recursos de UI (skin e atlas para elementos visuais)
    public static final String UI_SKIN_JSON = "ui/uiskin.json";
    public static final String UI_SKIN_ATLAS = "ui/uiskin.atlas";

    /// Texturas para projéteis e mira
    public static final String BULLET_TEXTURE = "bullet.png";
    public static final String FIREBALL_TEXTURE = "fireball.png";
    public static final String CROSSHAIR_TEXTURE = "crosshair.png";

    /// Ícones de controle de câmera
    public static final String CAMERA_ON_ICON = "camera_on.png";
    public static final String CAMERA_OFF_ICON = "camera_off.png";

    /// Texturas para o joystick virtual
    public static final String JOYSTICK_BASE = "joystick_base.png";
    public static final String JOYSTICK_KNOB = "joystick_knob.png";

    /// Fundos para diferentes ecrãs
    public static final String BACKGROUND_PAUSE = "background_pause.jpg";
    public static final String BACKGROUND_MAIN = "background.jpg";

    /// Itens colecionáveis
    public static final String ITEM_VIDA = "item.png";
    public static final String ITEM_ATAQUE = "bullet_item.png";
    public static final String ITEM_SUPER_ATAQUE = "fireball_logo.png";
    public static final String ITEM_SPACESHIP = "finalSpaceship.png";

    /// Efeitos de partículas
    public static final String PARTICLE_EXPLOSION = "effects/fire_trail.p";
    public static final String FIREBALL_PARTICLE_IMAGE = "effects/fire_particle.png";

    /// Efeitos sonoros gerais
    public static final String SOUND_GAME_OVER = "audio/gameOver.mp3";
    public static final String SOUND_HOVER = "audio/alert0.mp3";
    public static final String SOUND_CLICK = "audio/keyboardclick.mp3";
    public static final String SOUND_HURT = "audio/hurt.mp3";
    public static final String SOUND_ITEM_PICKUP = "audio/item_pickup.mp3";
    public static final String SOUND_FLIGHT = "audio/flight.mp3";
    public static final String SOUND_LASER = "audio/laser_shoot.mp3";
    public static final String SOUND_FIREBALL = "audio/fireball.mp3";
    public static final String SOUND_JUMP = "audio/jump.mp3";
    public static final String SOUND_WALK = "audio/walk.mp3";
    public static final String SOUND_DEATH = "audio/death.mp3";
    public static final String SOUND_WALK_INJURED = "audio/walk.mp3";  // Duplicado de SOUND_WALK
    public static final String SOUND_EMPTY_GUN = "audio/empty_gun.mp3";
    public static final String SOUND_NEXT_LEVEL = "audio/nextLevel.mp3";
    public static final String SOUND_WAYPOINT = "audio/waypoint.mp3";

    /// Sons específicos para inimigos do tipo alien
    public static final String SOUND_DEATH_ALIEN = "audio/deathAlien.mp3";
    public static final String SOUND_CHASE_ALIEN = "audio/chaseAlien.mp3";
    public static final String SOUND_PATROL_ALIEN = "audio/patrolAlien.mp3";

    /// Sons específicos para inimigos do tipo UFO
    public static final String SOUND_DEATH_UFO = "audio/deathUfo.mp3";
    public static final String SOUND_CHASE_UFO = "audio/chaseUfo.mp3";
    public static final String SOUND_HURT_UFO = "audio/hurtUfo.mp3";

    /// Música de fundo
    public static final String MUSIC_SPACE_INTRO = "audio/space_intro_sound.mp3";

    /// Caminhos para animações dos estados do jogador
    public static final String ANIM_IDLE = "armysoldier/Idle";
    public static final String ANIM_WALK = "armysoldier/Walk";
    public static final String ANIM_CLIMB = "armysoldier/Climb";
    public static final String ANIM_FAST_MOVE = "armysoldier/fastMove";
    public static final String ANIM_JUMP = "armysoldier/Jump";
    public static final String ANIM_FALL = "armysoldier/Fall";
    public static final String ANIM_DEATH = "armysoldier/Death";
    public static final String ANIM_HURT = "armysoldier/Hurt";
    public static final String ANIM_ATTACK = "armysoldier/Attack";
    public static final String ANIM_SUPER_ATTACK = "armysoldier/SuperAttack";
    public static final String ANIM_DEFENSE = "armysoldier/Defense";

    /// Variantes de animações para o estado ferido
    public static final String ANIM_WALK_INJURED = "armysoldier/WalkInjured";
    public static final String ANIM_IDLE_INJURED = "armysoldier/IdleInjured";
    public static final String ANIM_DEFENSE_INJURED = "armysoldier/DefenseInjured";

    // Enemy animations
    public static final String ENEMY_ATTACK = "enemy/attack/frame-%02d.png";
    public static final String ENEMY_DEATH = "enemy/death/frame-%02d.png";
    public static final String ENEMY_WALK = "enemy/walk/frame-%02d.png";
    public static final String ENEMY_IDLE = "enemy/idle/frame-%02d.png";
    public static final String ENEMY_HURT = "enemy/hurt/frame-%02d.png";

    // UFO animations
    public static final String UFO_HURT = "ufo/hurt/frame-%02d.png";
    public static final String UFO_DEATH = "ufo/death/frame-%02d.png";
    public static final String UFO_FLY = "ufo/fly/frame-%02d.png";
}
