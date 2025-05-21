package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.BoundsComponent;
import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.UniverseConqueror.ECS.components.EndLevelComponent;

public class ItemCollectionSystem extends IteratingSystem {
   // private final Rectangle playerBounds;
    private final Label itemsLabel;
    private int collectedCount = 0;

    private final ComponentMapper<ItemComponent> itemMapper = ComponentMapper.getFor(ItemComponent.class);
    private final ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
    private final ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

    private AttackSystem attackSystem;  // Adiciona a variável para armazenar o AttackSystem
    private HealthSystem healthSystem;
    private BodyRemovalSystem removalSystem;

    public ItemCollectionSystem(Label itemsLabel, AttackSystem attackSystem, HealthSystem healthSystem,BodyRemovalSystem removalSystem) {
        super(Family.all(ItemComponent.class, BoundsComponent.class).get());

        this.itemsLabel = itemsLabel;
        this.attackSystem = attackSystem;
        this.healthSystem = healthSystem;
        this.removalSystem= removalSystem;
        updateLabel();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent item = itemMapper.get(entity);
        BoundsComponent bounds = boundsMapper.get(entity);

        // Verifica se o item não foi coletado e se há colisão com o jogador
        if (!item.isCollected ) {
            Entity playerEntity = getPlayerEntity();
            collectItem(entity, playerEntity);
        }
    }

    private Entity getPlayerEntity() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());

        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    // Método para coletar o item e aplicar o efeito no jogador
    public void collectItem(Entity itemEntity, Entity playerEntity) {
        ItemComponent item = itemMapper.get(itemEntity);

        if (!item.isCollected) {
            item.isCollected = true;
            collectedCount++;
            updateLabel();
            SoundManager.getInstance().play("item");

            if ("SuperAtaque".equals(item.name)) {
                attackSystem.increaseAttackPower(5);
            }
            if ("Ataque".equals(item.name)) {
                attackSystem.increaseAttackPower(1);
            }
           if ("Vida".equals(item.name)) {
            healthSystem.heal(playerEntity,20);
          }
            BodyComponent bodyComponent = bodyMapper.get(itemEntity);
            if (bodyComponent != null && bodyComponent.body != null) {
                removalSystem.markForRemoval(bodyComponent.body);
            }
            getEngine().removeEntity(itemEntity);
        }
    }

    // Método para atualizar o rótulo de itens coletados
    private void updateLabel() {
        itemsLabel.setText("Items: " + collectedCount);
    }

    // Método para obter a quantidade de itens coletados
    public int getCollectedCount() {
        return collectedCount;
    }

    public void setCollectedCount(int count) {
        this.collectedCount = count;
        updateLabel();
    }
}
