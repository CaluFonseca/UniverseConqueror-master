package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AnimationComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.UniverseConqueror.ECS.components.TransformComponent;
import com.badlogic.UniverseConqueror.ECS.components.UfoComponent;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UfoRenderSystem extends BaseAnimatedRenderSystem  {

    public UfoRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(
            Family.all(UfoComponent.class, TransformComponent.class, AnimationComponent.class, StateComponent.class).get(),
            batch,
            camera
        );
    }
}
