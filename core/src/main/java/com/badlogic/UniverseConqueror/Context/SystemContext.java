package com.badlogic.UniverseConqueror.Context;

import com.badlogic.UniverseConqueror.ECS.systems.*;
import com.badlogic.UniverseConqueror.Initializers.SystemInitializer;

public class SystemContext {

    private AttackSystem attackSystem;
    private HealthSystem healthSystem;
    private PlayerInputSystem playerInputSystem;
    private ItemCollectionSystem itemCollectionSystem;
    private BodyRemovalSystem bodyRemovalSystem;
    private AnimationSystem animationSystem;
    private CameraInputSystem cameraInputSystem;

    // Getters
    public AttackSystem getAttackSystem() { return attackSystem; }
    public HealthSystem getHealthSystem() { return healthSystem; }
    public PlayerInputSystem getPlayerInputSystem() { return playerInputSystem; }
    public ItemCollectionSystem getItemCollectionSystem() { return itemCollectionSystem; }
    public BodyRemovalSystem getBodyRemovalSystem() { return bodyRemovalSystem; }
    public AnimationSystem getAnimationSystem() { return animationSystem; }
    public CameraInputSystem getCameraInputSystem() { return cameraInputSystem; }

    // Setters
    public void setAttackSystem(AttackSystem attackSystem) { this.attackSystem = attackSystem; }
    public void setHealthSystem(HealthSystem healthSystem) { this.healthSystem = healthSystem; }
    public void setPlayerInputSystem(PlayerInputSystem playerInputSystem) { this.playerInputSystem = playerInputSystem; }
    public void setItemCollectionSystem(ItemCollectionSystem itemCollectionSystem) { this.itemCollectionSystem = itemCollectionSystem; }
    public void setBodyRemovalSystem(BodyRemovalSystem bodyRemovalSystem) { this.bodyRemovalSystem = bodyRemovalSystem; }
    public void setAnimationSystem(AnimationSystem animationSystem) { this.animationSystem = animationSystem; }
    public void setCameraInputSystem(CameraInputSystem cameraInputSystem) { this.cameraInputSystem = cameraInputSystem; }

    // Static factory method
    public static SystemContext createFrom(SystemInitializer initializer) {
        SystemContext context = new SystemContext();
        context.setAttackSystem(initializer.attackSystem);
        context.setHealthSystem(initializer.healthSystem);
        context.setPlayerInputSystem(initializer.playerInputSystem);
        context.setItemCollectionSystem(initializer.itemCollectionSystem);
        context.setBodyRemovalSystem(initializer.bodyRemovalSystem);
        context.setAnimationSystem(initializer.animationSystem);
        context.setCameraInputSystem(initializer.cameraInputSystem);
        return context;
    }
}
