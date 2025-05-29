package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Utils.Joystick;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class JoystickInputSystem extends IteratingSystem {

    private final Joystick joystick;       /// Referência para o joystick virtual usado no jogo
    private final float MAX_SPEED = 100f;  /// Velocidade máxima que uma entidade pode alcançar via joystick

    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public JoystickInputSystem(Joystick joystick) {
        super(Family.all(VelocityComponent.class).get()); /// Sistema aplicado a entidades com VelocityComponent
        this.joystick = joystick;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = vm.get(entity); /// Obtém o componente de velocidade da entidade

        /// Obtém a direção do joystick (normalizada entre -1 e 1)
        float dx = joystick.getDirection().x;
        float dy = joystick.getDirection().y;

        /// Aplica a direção e escala pela velocidade máxima
        velocity.velocity.set(dx * MAX_SPEED, dy * MAX_SPEED);
    }
}
