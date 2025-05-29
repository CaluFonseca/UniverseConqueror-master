package com.badlogic.UniverseConqueror.ECS.systems;

// Importações dos componentes necessários
import com.badlogic.UniverseConqueror.ECS.components.BodyComponent;
import com.badlogic.UniverseConqueror.ECS.components.BoundsComponent;
import com.badlogic.UniverseConqueror.ECS.components.ItemComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.events.EventBus;
import com.badlogic.UniverseConqueror.ECS.events.ItemCollectedEvent;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

// Sistema responsável por detectar e processar a coleta de itens
public class ItemCollectionSystem extends IteratingSystem {

    private int collectedCount = 0; // Contador de itens coletados

    // Mappers para acesso rápido aos componentes
    private final ComponentMapper<ItemComponent> itemMapper = ComponentMapper.getFor(ItemComponent.class);
    private final ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

    private final BodyRemovalSystem removalSystem; // Sistema que remove corpos do mundo Box2D

    // Construtor: sistema depende do BodyRemovalSystem para remover o corpo do item
    public ItemCollectionSystem(BodyRemovalSystem removalSystem) {
        super(Family.all(ItemComponent.class, BoundsComponent.class).get()); // Aplica-se a entidades com Item e Bounds
        this.removalSystem = removalSystem;
    }

    // Executado para cada entidade do tipo item ainda não coletado
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent item = itemMapper.get(entity);
        if (!item.isCollected) {
            Entity playerEntity = getPlayerEntity(); // Procura o jogador
            if (playerEntity != null) {
                collectItem(entity, playerEntity); // Realiza coleta
            }
        }
    }

    // Recupera a entidade do jogador (assumindo apenas um)
    private Entity getPlayerEntity() {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        return entities.size() > 0 ? entities.first() : null;
    }

    // Realiza a coleta do item
    public void collectItem(Entity itemEntity, Entity playerEntity) {
        ItemComponent item = itemMapper.get(itemEntity);
        if (item.isCollected) return;

        item.isCollected = true;  // Marca como coletado
        collectedCount++;         // Incrementa contador

        // Notifica outros sistemas da coleta (UI, som, etc.)
        EventBus.get().notify(new ItemCollectedEvent(playerEntity, itemEntity, collectedCount));

        // Remove corpo físico do item
        BodyComponent bodyComponent = bodyMapper.get(itemEntity);
        if (bodyComponent != null && bodyComponent.body != null) {
            removalSystem.markForRemoval(bodyComponent.body); // Marca para remoção segura no próximo frame
        }

        // Remove a entidade do item do mundo ECS
        getEngine().removeEntity(itemEntity);
    }

    // Retorna total de itens coletados
    public int getCollectedCount() {
        return collectedCount;
    }

    // Define o número de itens coletados (útil para restauração de estado salvo)
    public void setCollectedCount(int count) {
        this.collectedCount = count;
    }
}
