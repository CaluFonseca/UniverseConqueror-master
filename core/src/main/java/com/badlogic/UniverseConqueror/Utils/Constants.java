/// Classe que armazena constantes globais utilizadas em várias partes do jogo.

package com.badlogic.UniverseConqueror.Utils;

public class Constants {

    /// Pixels por metro - usado para conversão entre unidades visuais e físicas no Box2D.
    public static final float PPM = 128f;

    /// Velocidade padrão de movimento do jogador ou inimigos (em unidades por segundo).
    public static final float SPEED = 100f;

    /// Velocidade de corrida ou movimento especial (ex: dash, sprint).
    public static final float SPRINT_SPEED = 10000f;

    /// Fator multiplicador para ajustar forças ou velocidades aplicadas na física (Box2D).
    public static final float PHYSICS_MULTIPLIER = 32f;

    /// Força do pulo aplicada ao corpo do jogador.
    public static final float JUMP_FORCE = 500f;

    /// Caminho do arquivo de salvamento do jogo.
    public static final String SAVE_PATH = "savegame.json";

    /// Velocidade usada em modo de movimento rápido, equivalente ao sprint.
    /// Duplicada de SPRINT_SPEED, mas pode ser usada para distinguir casos lógicos diferentes.
    public static final float SPEED_FAST = 10000f;
}
