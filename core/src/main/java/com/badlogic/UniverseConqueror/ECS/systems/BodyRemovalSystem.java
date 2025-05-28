package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BodyRemovalSystem extends EntitySystem {
    private final World world;
    private final Array<Body> bodiesToDestroy = new Array<>();

    public BodyRemovalSystem(World world) {
        this.world = world;
    }

    public void markForRemoval(Body body) {

        bodiesToDestroy.add(body);
       // System.out.println("[BodyRemovalSystem] Corpo marcado para destruição: " + body);
    }

    @Override
    public void update(float deltaTime) {
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }
}

