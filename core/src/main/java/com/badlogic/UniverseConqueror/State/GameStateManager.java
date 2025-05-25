package com.badlogic.UniverseConqueror.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import static com.badlogic.UniverseConqueror.Utils.Constants.SAVE_PATH;

public class GameStateManager {


    public static void save(GameState state) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(SAVE_PATH);
        file.writeString(json.prettyPrint(state), false);
      //  System.out.println("Jogo salvo em: " + file.file().getAbsolutePath());
    }

    public static GameState load() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        if (!file.exists()) return null;

        Json json = new Json();
        return json.fromJson(GameState.class, file);
    }
    public static void delete() {
        FileHandle file = Gdx.files.local(SAVE_PATH);
        if (file.exists()) file.delete();
    }

}
