package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import static com.badlogic.UniverseConqueror.Utils.Constants.SAVE_PATH;

// Classe responsável por guardar, carregar e apagar o estado do jogo em arquivo JSON
public class GameStateManager {

    // guarda o estado do jogo no arquivo definido em SAVE_PATH
    public static void save(GameState state) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(SAVE_PATH);
        file.writeString(json.prettyPrint(state), false);
    }

    // Carrega o estado do jogo a partir do arquivo JSON. Retorna null se não existir arquivo salvo.
    public static GameState load() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        if (!file.exists()) return null;

        Json json = new Json();
        return json.fromJson(GameState.class, file);
    }

    // Apaga o arquivo JSON
    public static void delete() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        if (file.exists()) file.delete();
    }
    public static boolean hasSave() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        return file.exists();
    }
}
