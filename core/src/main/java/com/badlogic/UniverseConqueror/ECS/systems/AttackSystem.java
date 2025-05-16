package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.ECS.components.AttackComponent;
import com.badlogic.UniverseConqueror.ECS.components.PlayerComponent;
import com.badlogic.UniverseConqueror.ECS.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class AttackSystem extends IteratingSystem {

    private final ComponentMapper<AttackComponent> am = ComponentMapper.getFor(AttackComponent.class);
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private Engine engine;

    public AttackSystem() {
        super(Family.all(AttackComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttackComponent attack = am.get(entity);
        StateComponent state = sm.get(entity);

        // Atualiza timers
        attack.timeSinceLastAttack += deltaTime;

        if (attack.isAttacking) {
            attack.attackTimer += deltaTime;

            // Termina ataque quando duração expira
            if (attack.attackTimer >= attack.attackDuration) {
                attack.isAttacking = false;
                attack.attackTimer = 0f;

                // Volta para idle após o ataque
                if (state.currentState == StateComponent.State.ATTACK) {
                    state.set(StateComponent.State.IDLE);
                }
            }

        } else {
            // Se pode atacar e está pronto (lógica de controle pode chamar isso externamente)
            if (attack.canAttack()) {
                // Exemplo: iniciar ataque manualmente fora do sistema, ou via input
            }
        }
    }

    // Método para aumentar o remainingAttackPower do jogador
    public void increaseAttackPower(int amount) {
        // Acessa a entidade do jogador e aumenta o remainingAttackPower
        Entity playerEntity = getPlayerEntity();
        AttackComponent attack = playerEntity.getComponent(AttackComponent.class);

        if (attack != null) {
            attack.remainingAttackPower += amount;  // Adiciona ao remainingAttackPower
            System.out.println("Aumentando Remaining Attack Power: " + attack.remainingAttackPower);
        }
    }

    // Método para obter o remainingAttackPower do jogador
    public int getRemainingAttackPower() {
        // Busca a entidade do jogador
        Entity playerEntity = getPlayerEntity();
        AttackComponent attack = playerEntity.getComponent(AttackComponent.class);

        // Se o jogador tiver um AttackComponent, retorna o remainingAttackPower
        if (attack != null) {
            return attack.remainingAttackPower;
        }

        // Retorna 0 caso não encontre o AttackComponent
        return 0;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    // Método para obter a entidade do jogador
    private Entity getPlayerEntity() {
        try {
            // Buscando a entidade do jogador através do componente PlayerComponent
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());

            // Se houver entidades com PlayerComponent, retornamos a primeira
            if (entities != null && entities.size() > 0) {
                return entities.get(0);  // Retorna a primeira entidade do jogador
            } else {
                //System.out.println("Nenhuma entidade com PlayerComponent encontrada.");
            }
        } catch (Exception e) {
            // Captura qualquer exceção e imprime no console para debug
            System.err.println("Erro ao tentar buscar a entidade do jogador: " + e.getMessage());
            e.printStackTrace();
        }

        return null;  // Retorna null se não encontrar a entidade do jogador ou ocorrer um erro
    }
}

