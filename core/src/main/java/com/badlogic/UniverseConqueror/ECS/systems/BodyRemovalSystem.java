package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class BodyRemovalSystem extends EntitySystem {
    /// Referência ao mundo Box2D onde os corpos físicos existem
    private final World world;

    /// Lista de corpos marcados para remoção
    private final Array<Body> bodiesToDestroy = new Array<>();

    /// Construtor recebe o mundo Box2D como parâmetro
    public BodyRemovalSystem(World world) {
        this.world = world;
    }

    /// Marca um corpo para remoção no próximo update
    public void markForRemoval(Body body) {
        bodiesToDestroy.add(body);
    }

    /// Remove todos os corpos marcados durante o update
    @Override
    public void update(float deltaTime) {
        for (Body body : bodiesToDestroy) {
            world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }
}
