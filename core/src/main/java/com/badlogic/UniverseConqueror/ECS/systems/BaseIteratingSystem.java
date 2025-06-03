package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * BaseSystem com suporte a mapeadores de componentes.
 * Pode ser extendido por qualquer sistema que queira
 * acesso fácil a mappers comuns.
 */
public abstract class BaseIteratingSystem extends IteratingSystem {
    public BaseIteratingSystem(com.badlogic.ashley.core.Family family) {
        super(family);
    }

    @Override
    protected abstract void processEntity(Entity entity, float deltaTime);
}
