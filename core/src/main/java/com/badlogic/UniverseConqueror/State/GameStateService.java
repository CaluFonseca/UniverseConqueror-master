package com.badlogic.UniverseConqueror.State;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.entity.ItemFactory;
import com.badlogic.UniverseConqueror.ECS.entity.PlayerFactory;
import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.State.SavedItemData;
import com.badlogic.UniverseConqueror.Strategy.ChasePlayerStrategy;
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
import com.badlogic.UniverseConqueror.ECS.entity.SpaceshipFactory;
import com.badlogic.UniverseConqueror.ECS.entity.EnemyFactory;


public class GameStateService {

    /// Referência ao motor de entidades do Ashley ECS (PooledEngine)
    private  PooledEngine engine;
    /// Mundo físico do Box2D para física e colisões
    private  World world;
    /// Gerenciador de ativos para carregar recursos (imagens, sons, etc)
    private  AssetManager assetManager;
    /// Sistema para remoção segura de corpos Box2D marcados para exclusão
    private  BodyRemovalSystem bodyRemovalSystem;
    /// Sistema que controla o poder de ataque do jogador
    private  AttackSystem attackSystem;
    /// Sistema responsável pela coleta de itens no jogo
    private  ItemCollectionSystem itemCollectionSystem;
    /// Temporizador para controlar o tempo de jogo
    private  Timer playingTimer;
    /// Câmera ortográfica usada na renderização
    private  OrthographicCamera camera;
    /// Sistema responsável pela entrada do jogador (teclado, joystick)
    private  PlayerInputSystem playerInputSystem;
    /// Posição da nave espacial (objetivo do jogo)
    public Vector2 spaceshipPosition;

    /// Entidade que representa o jogador
    private Entity player;
    /// Flag que indica se o estado foi restaurado a partir de um save
    private boolean restoredState = false;
    private UfoSpawnerSystem ufoSpawnerSystem;


    /// Construtor da classe com todas as dependências injetadas
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

    /// Define a entidade jogador que será gerenciada
    public void setPlayer(Entity player) {
        this.player = player;
    }

    /// Retorna a entidade jogador atual
    public Entity getPlayer() {
        return player;
    }

    /// Retorna se o estado do jogo foi restaurado de um save
    public boolean wasRestored() {
        return restoredState;
    }

    /// Salva o estado atual do jogo, coletando dados importantes das entidades
    public void saveGameState() {
        GameState state = new GameState();

        /// Obtém componentes importantes do jogador
        PositionComponent pos = player.getComponent(PositionComponent.class);
        HealthComponent health = player.getComponent(HealthComponent.class);
        AttackComponent attack = player.getComponent(AttackComponent.class);

        /// Copia dados do jogador para o estado
        if (pos != null) state.playerPosition = pos.position.cpy();
        if (health != null) state.playerHealth = health.currentHealth;
        if (attack != null) state.playerAttack = attackSystem.getRemainingAttackPower();
        state.gameTime = playingTimer.getTime();

        /// Salva os itens não coletados atualmente no mundo
        ImmutableArray<Entity> items = engine.getEntitiesFor(Family.all(ItemComponent.class, TransformComponent.class).get());
        for (Entity e : items) {
            ItemComponent ic = e.getComponent(ItemComponent.class);
            TransformComponent tc = e.getComponent(TransformComponent.class);
            if (ic != null && tc != null && !ic.isCollected) {
                state.remainingItems.add(new SavedItemData(ic.name, new Vector2(tc.position.x, tc.position.y)));
            }
        }

        /// Salva a posição da nave espacial, se existir no mundo
        ImmutableArray<Entity> spaceships = engine.getEntitiesFor(Family.all(EndLevelComponent.class, PositionComponent.class).get());
        if (spaceships.size() > 0) {
            PositionComponent spPos = spaceships.first().getComponent(PositionComponent.class);
            if (spPos != null) {
                state.spaceshipPosition = spPos.position.cpy();
            }
        }

        /// Remove todas as balas do mundo para evitar problemas na restauração
        ImmutableArray<Entity> bullets = engine.getEntitiesFor(Family.all(ProjectileComponent.class).get());
        for (Entity bullet : bullets) {
            BodyComponent bodyComponent = bullet.getComponent(BodyComponent.class);
            if (bodyComponent != null && bodyComponent.body != null) {
                bodyRemovalSystem.markForRemoval(bodyComponent.body);
            }
            engine.removeEntity(bullet);
        }

        /// Salva os inimigos ativos, com suas posições e pontos de patrulha
        ImmutableArray<Entity> enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, PositionComponent.class).get());
        for (Entity e : enemies) {
            PositionComponent posEnemy = e.getComponent(PositionComponent.class);
            EnemyComponent enemy = e.getComponent(EnemyComponent.class);

            /// Converte o tipo do inimigo para string em minúsculas
            String type = enemy.type != null ? enemy.type.name().toLowerCase() : "chase";
            Vector2 patrolStart = enemy.patrolStart != null ? enemy.patrolStart.cpy() : posEnemy.position.cpy();
            Vector2 patrolEnd = enemy.patrolEnd != null ? enemy.patrolEnd.cpy() : posEnemy.position.cpy();
            Vector2 target = null;
            if ("chase".equals(type)) {
                PositionComponent playerPos = player.getComponent(PositionComponent.class);
                if (playerPos != null) {
                    target = playerPos.position.cpy();
                } else {
                    target = posEnemy.position.cpy(); // fallback
                }
            }

            /// Salva os dados do inimigo
            state.enemies.add(new SavedEnemyData(posEnemy.position.cpy(), patrolStart, patrolEnd, type, target));
        }

        /// Atualiza o sistema de remoção de corpos e o engine para sincronizar o estado
        bodyRemovalSystem.update(0f);
        engine.update(0f);

        /// Salva a contagem de itens coletados
        state.collectedItemCount = itemCollectionSystem.getCollectedCount();

        /// Finalmente salva o estado no arquivo
        GameStateManager.save(state);
    }

    public boolean loadGameStateFromJson() {
        if (!GameStateManager.hasSave()) return false;

        GameState state = GameStateManager.load();
        if (state == null) return false;

        restoreState(state);
        restoredState = true;
        return true;
    }
    /// Método para restaurar o estado salvo — ainda não implementado
    public Entity restoreState(GameState state) {
//        // Limpa todas as entidades atuais para evitar duplicatas
//        ImmutableArray<Entity> allEntities = engine.getEntities();
//        for (Entity e : allEntities) {
//            engine.removeEntity(e);
//        }

        // --- 1. Restaurar o jogador ---
        player = PlayerFactory.createPlayer(engine, state.playerPosition, world, assetManager);


        if (!engine.getEntities().contains(player, true)) {
            engine.addEntity(player);
        }
        setPlayer(player); // atualiza referência interna do player no GameStateService

        // Atualiza componentes do jogador (vida, ataque)
        HealthComponent health = player.getComponent(HealthComponent.class);
        if (health != null) {
            health.currentHealth = state.playerHealth;
        }
        attackSystem.setRemainingAttackPower(state.playerAttack);

        // Atualiza sistema de input para usar o jogador restaurado
        playerInputSystem.setPlayer(player);

        // --- 2. Restaurar o timer ---
        playingTimer.setTime(state.gameTime);

        // --- 3. Restaurar itens restantes ---
        for (SavedItemData itemData : state.remainingItems) {
            ItemFactory factory = ItemFactory.createItem(itemData.name, itemData.position, assetManager);
            Entity item = factory.createEntity(engine, world);
            engine.addEntity(item);

        }
        SpriteBatch batchItem = new SpriteBatch();
        engine.addSystem(new RenderItemSystem(batchItem, camera));
        // --- 4. Restaurar a nave (end level) ---
        if (state.spaceshipPosition != null) {
            SpaceshipFactory factory = new SpaceshipFactory(assetManager);
            Entity spaceship = factory.createSpaceship(state.spaceshipPosition, engine, world);

            if (!engine.getEntities().contains(spaceship, true)) {
                engine.addEntity(spaceship);
                spaceshipPosition = state.spaceshipPosition.cpy();
            }

        }

        // --- 5. Restaurar inimigos ---
//        for (SavedEnemyData enemyData : state.enemies) {
//            Entity enemy = EnemyFactory.createEnemyFromData(engine, world, assetManager, player, camera, enemyData);
//            engine.addEntity(enemy);
//        }
        SpriteBatch batchUfo = new SpriteBatch();
        engine.addSystem(new UfoRenderSystem(batchUfo, camera));
        // --- 6. Restaurar contagem de itens coletados ---
        itemCollectionSystem.setCollectedCount(state.collectedItemCount);

        // Marca que o estado foi restaurado com sucesso
        restoredState = true;
        if (ufoSpawnerSystem != null) {
            ufoSpawnerSystem.resetTimer();
        }
        return player;
    }
    public void setUfoSpawnerSystem(UfoSpawnerSystem ufoSpawnerSystem) {
        this.ufoSpawnerSystem = ufoSpawnerSystem;
    }
}
