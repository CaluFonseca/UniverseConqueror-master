package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.utils.ComponentMappers;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.ItemCollectedEvent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

// Sistema responsável por detectar e processar a coleta de itens
public class ItemCollectionSystem extends BaseIteratingSystem {

    private int collectedCount = 0;
    private final BodyRemovalSystem removalSystem;

    public ItemCollectionSystem(BodyRemovalSystem removalSystem) {
        super(Family.all(ItemComponent.class, BoundsComponent.class).get());
        this.removalSystem = removalSystem;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent item = ComponentMappers.item.get(entity);
        if (!item.isCollected) {
            Entity playerEntity = getPlayerEntity();
            if (playerEntity != null) {
                collectItem(entity, playerEntity);
            }
        }
    }

    private Entity getPlayerEntity() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        return entities.size() > 0 ? entities.first() : null;
    }

    public void collectItem(Entity itemEntity, Entity playerEntity) {
        ItemComponent item = ComponentMappers.item.get(itemEntity);
        if (item.isCollected) return;

        item.isCollected = true;
        collectedCount++;

        EventBus.get().notify(new ItemCollectedEvent(playerEntity, itemEntity, collectedCount));

        BodyComponent bodyComponent = ComponentMappers.body.get(itemEntity);
        if (bodyComponent != null && bodyComponent.body != null) {
            removalSystem.markForRemoval(bodyComponent.body);
        }

        getEngine().removeEntity(itemEntity);
    }

    public int getCollectedCount() {
        return collectedCount;
    }

    public void setCollectedCount(int count) {
        this.collectedCount = count;
    }
}
