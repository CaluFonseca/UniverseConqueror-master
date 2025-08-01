// Classe que armazena constantes globais utilizadas em várias partes do jogo.

package com.badlogic.UniverseConqueror.Utils;

public class Constants {

    // Pixels por metro - usado para conversão entre unidades visuais e físicas no Box2D.
    public static final float PPM = 128f;

    // Velocidade padrão de movimento do jogador
    public static final float SPEED = 120f;

    // Velocidade padrão de movimento dos UFO em CHASE.
    public static final float SPEED_UFO_ENEMY = 60f;

    // Velocidade padrão de movimento dos ENEMY em PATROL.
    public static final float SPEED_ENEMY = 60f;

    // Fator multiplicador para ajustar forças ou velocidades aplicadas na física (Box2D).
    public static final float PHYSICS_MULTIPLIER = 32f;

    // Força do pulo aplicada ao corpo do jogador.
    public static final float JUMP_FORCE = 500f;

    // Caminho do arquivo de save do jogo.
    public static final String SAVE_PATH = "savegame.json";

    // Velocidade usada em modo de movimento rápido, equivalente ao sprint.
    public static final float SPEED_FAST = 10000f;

    public static final float MIN_FLIP_THRESHOLD = 2f;


}
