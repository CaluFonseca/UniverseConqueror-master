package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.components.SoundComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

// Sistema que reproduz sons únicos a partir do componente SoundComponent.
// Quando `play` é true, o som correspondente à `soundKey` é tocado uma vez.
public class SoundSystem extends BaseIteratingSystem {

    public SoundSystem() {
        super(Family.all(SoundComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SoundComponent sound = ComponentMappers.sound.get(entity);

        if (sound.play) {
            SoundManager.getInstance().play(sound.soundKey);
            sound.play = false;
        }
    }
}
