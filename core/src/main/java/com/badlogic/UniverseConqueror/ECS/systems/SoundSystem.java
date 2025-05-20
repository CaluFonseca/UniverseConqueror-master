package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.SoundComponent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;

public class SoundSystem extends IteratingSystem {
    private final ComponentMapper<SoundComponent> sm = ComponentMapper.getFor(SoundComponent.class);

    public SoundSystem() {
        super(Family.all(SoundComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SoundComponent sound = sm.get(entity);
        if (sound.play) {
            SoundManager.getInstance().play(sound.soundKey);
            sound.play = false;
        }
    }
}
