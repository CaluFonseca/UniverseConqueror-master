package com.badlogic.UniverseConqueror.State;


import com.badlogic.gdx.math.Vector2;

public class SavedItemData {
    public String name;
    public Vector2 position;
    public SavedItemData() {}

    // Construtor completo que cria uma cópia da posição para evitar efeitos colaterais
    public SavedItemData(String name, Vector2 position) {
        this.name = name;
        this.position = position.cpy();
    }
}
