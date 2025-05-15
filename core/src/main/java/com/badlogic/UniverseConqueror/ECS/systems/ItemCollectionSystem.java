package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.BoundsComponent;
import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ItemCollectionSystem extends IteratingSystem {
    private final Rectangle playerBounds;
    private final Label itemsLabel;
    private int collectedCount = 0;

    private final ComponentMapper<ItemComponent> itemMapper = ComponentMapper.getFor(ItemComponent.class);
    private final ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);

    private AttackSystem attackSystem;  // Adiciona a variável para armazenar o AttackSystem
    private HealthSystem healthSystem;

    // Modifica o construtor para armazenar o AttackSystem
    public ItemCollectionSystem(Rectangle playerBounds, Label itemsLabel, AttackSystem attackSystem, HealthSystem healthSystem) {
        super(Family.all(ItemComponent.class, BoundsComponent.class).get());
        this.playerBounds = playerBounds;
        this.itemsLabel = itemsLabel;
        this.attackSystem = attackSystem;  // Armazena a instância do AttackSystem
        this.healthSystem = healthSystem;
        updateLabel(); // Atualiza o rótulo com a contagem inicial de itens coletados
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent item = itemMapper.get(entity);  // Obtém o componente de item
        BoundsComponent bounds = boundsMapper.get(entity);  // Obtém o componente de colisão (Bounds)

        System.out.println("Item Bounds: " + bounds.bounds);

        // Verifica se o item não foi coletado e se há colisão com o jogador
        if (!item.isCollected && playerBounds.overlaps(bounds.bounds)) {
            Entity playerEntity = getPlayerEntity();
            collectItem(entity, playerEntity);  // Chama o método centralizado de coleta
        }
    }

    private Entity getPlayerEntity() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());

        if (entities.size() > 0) {
            return entities.get(0);  // Assuming there is only one player, return the first entity
        }

        return null;  // Return null if no player entity is found
    }

    // Método para coletar o item e aplicar o efeito no jogador
    public void collectItem(Entity itemEntity, Entity playerEntity) {
        ItemComponent item = itemMapper.get(itemEntity);  // Obtém o ItemComponent

        // Se o item não foi coletado ainda
        if (!item.isCollected) {
            item.isCollected = true;  // Marca o item como coletado
            collectedCount++;  // Incrementa o contador de itens coletados
            updateLabel();  // Atualiza o rótulo na UI

            // Se o item for "SuperAtaque", aumenta o poder de ataque do jogador
            if ("SuperAtaque".equals(item.name)) {
                attackSystem.increaseAttackPower(5);  // Aumenta o remainingAttackPower em 5
            }
            if ("Ataque".equals(item.name)) {
                attackSystem.increaseAttackPower(1);  // Aumenta o remainingAttackPower em 5
            }
           if ("Vida".equals(item.name)) {
            healthSystem.heal(playerEntity,20);
          }

            getEngine().removeEntity(itemEntity);  // Remove a entidade do ECS
        }
    }

    // Método para atualizar o rótulo de itens coletados
    private void updateLabel() {
        itemsLabel.setText("Items: " + collectedCount);  // Atualiza a contagem de itens coletados na UI
    }

    // Método para obter a quantidade de itens coletados
    public int getCollectedCount() {
        return collectedCount;
    }
}
