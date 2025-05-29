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

    /// Referência ao motor de entidades do Ashley ECS (PooledEngine)
    private final PooledEngine engine;
    /// Mundo físico do Box2D para física e colisões
    private final World world;
    /// Gerenciador de ativos para carregar recursos (imagens, sons, etc)
    private final AssetManager assetManager;
    /// Sistema para remoção segura de corpos Box2D marcados para exclusão
    private final BodyRemovalSystem bodyRemovalSystem;
    /// Sistema que controla o poder de ataque do jogador
    private final AttackSystem attackSystem;
    /// Sistema responsável pela coleta de itens no jogo
    private final ItemCollectionSystem itemCollectionSystem;
    /// Temporizador para controlar o tempo de jogo
    private final Timer playingTimer;
    /// Câmera ortográfica usada na renderização
    private final OrthographicCamera camera;
    /// Sistema responsável pela entrada do jogador (teclado, joystick)
    private final PlayerInputSystem playerInputSystem;
    /// Posição da nave espacial (objetivo do jogo)
    public Vector2 spaceshipPosition;

    /// Entidade que representa o jogador
    private Entity player;
    /// Flag que indica se o estado foi restaurado a partir de um save
    private boolean restoredState = false;

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
            Vector2 patrolStart = enemy.patrolStart != null ? enemy.patrolStart.cpy() : null;
            Vector2 patrolEnd = enemy.patrolEnd != null ? enemy.patrolEnd.cpy() : null;

            /// Salva os dados do inimigo
            state.enemies.add(new SavedEnemyData(posEnemy.position.cpy(), patrolStart, patrolEnd, type));
        }

        /// Atualiza o sistema de remoção de corpos e o engine para sincronizar o estado
        bodyRemovalSystem.update(0f);
        engine.update(0f);

        /// Salva a contagem de itens coletados
        state.collectedItemCount = itemCollectionSystem.getCollectedCount();

        /// Finalmente salva o estado no arquivo
        GameStateManager.save(state);
    }

    /// Método para restaurar o estado salvo — ainda não implementado
    public void restoreState(GameState state) {

    }
}
