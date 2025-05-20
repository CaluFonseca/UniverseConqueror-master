package com.badlogic.UniverseConqueror.State;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.State.SavedItemData;
import com.badlogic.UniverseConqueror.Utils.Timer;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.ashley.core.*;

public class GameStateService {

    private final PooledEngine engine;
    private final World world;
    private final AssetManager assetManager;
    private final BodyRemovalSystem bodyRemovalSystem;
    private final AttackSystem attackSystem;
    private final ItemCollectionSystem itemCollectionSystem;
    private final Timer playingTimer;
    private final OrthographicCamera camera;
    private final PlayerInputSystem playerInputSystem;

    private Entity player;
    private boolean restoredState = false;

    public GameStateService(PooledEngine engine, World world, AssetManager assetManager,
                            BodyRemovalSystem bodyRemovalSystem,
                            AttackSystem attackSystem,
                            ItemCollectionSystem itemCollectionSystem,
                            Timer playingTimer,
                            OrthographicCamera camera,
                            PlayerInputSystem playerInputSystem) {
        this.engine = engine;
        this.world = world;
        this.assetManager = assetManager;
        this.bodyRemovalSystem = bodyRemovalSystem;
        this.attackSystem = attackSystem;
        this.itemCollectionSystem = itemCollectionSystem;
        this.playingTimer = playingTimer;
        this.camera = camera;
        this.playerInputSystem = playerInputSystem;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Entity getPlayer() {
        return player;
    }

    public boolean wasRestored() {
        return restoredState;
    }

    public void saveGameState() {
        GameState state = new GameState();

        PositionComponent pos = player.getComponent(PositionComponent.class);
        HealthComponent health = player.getComponent(HealthComponent.class);
        AttackComponent attack = player.getComponent(AttackComponent.class);

        if (pos != null) state.playerPosition = pos.position.cpy();
        if (health != null) state.playerHealth = health.currentHealth;
        if (attack != null) state.playerAttack = attackSystem.getRemainingAttackPower();
        state.gameTime = playingTimer.getTime();

        ImmutableArray<Entity> items = engine.getEntitiesFor(Family.all(ItemComponent.class, TransformComponent.class).get());
        for (Entity e : items) {
            ItemComponent ic = e.getComponent(ItemComponent.class);
            TransformComponent tc = e.getComponent(TransformComponent.class);
            if (ic != null && tc != null && !ic.isCollected) {
                state.remainingItems.add(new SavedItemData(ic.name, new Vector2(tc.position.x, tc.position.y)));
            }
        }

        ImmutableArray<Entity> bullets = engine.getEntitiesFor(Family.all(ProjectileComponent.class).get());
        for (Entity bullet : bullets) {
            BodyComponent bodyComponent = bullet.getComponent(BodyComponent.class);
            if (bodyComponent != null && bodyComponent.body != null) {
                bodyRemovalSystem.markForRemoval(bodyComponent.body);
            }
            engine.removeEntity(bullet);
        }
        bodyRemovalSystem.update(0f);
        engine.update(0f);

        state.collectedItemCount = itemCollectionSystem.getCollectedCount();

        GameStateManager.save(state);
    }

    public void restoreState(GameState state) {

    }
}
