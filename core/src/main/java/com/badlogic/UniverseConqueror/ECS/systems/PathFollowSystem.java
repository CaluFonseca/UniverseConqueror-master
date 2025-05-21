package com.badlogic.UniverseConqueror.ECS.systems;

import com.badlogic.UniverseConqueror.Audio.SoundManager;
import com.badlogic.UniverseConqueror.ECS.components.PathComponent;
import com.badlogic.UniverseConqueror.ECS.components.PhysicsComponent;
import com.badlogic.UniverseConqueror.ECS.components.PositionComponent;
import com.badlogic.UniverseConqueror.ECS.components.VelocityComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class PathFollowSystem extends IteratingSystem {
    private ComponentMapper<PathComponent> pathMapper = ComponentMapper.getFor(PathComponent.class);
    private ComponentMapper<PositionComponent> posMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> velMapper = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PhysicsComponent> phyMapper = ComponentMapper.getFor(PhysicsComponent.class);    public PathFollowSystem() {
        super(Family.all(PathComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

//    @Override
//    protected void processEntity(Entity entity, float deltaTime) {
//        PathComponent path = pathMapper.get(entity);
//        PositionComponent position = posMapper.get(entity);
//        VelocityComponent velocity = velMapper.get(entity);
//
//        if (path.waypoints.isEmpty()) {
//            velocity.velocity.set(0, 0);
//            return;
//        }
//
//        Vector2 target = path.waypoints.peek();
//        Vector2 pos = position.position;
//        Vector2 direction = new Vector2(target).sub(pos);
//
//        if (direction.len() < 2f) {
//            path.waypoints.poll();
//        } else {
//            velocity.velocity.set(direction.nor().scl(100f)); // velocidade
//        }
//    }
@Override
protected void processEntity(Entity entity, float deltaTime) {
    PathComponent path = pathMapper.get(entity);
    PhysicsComponent physics = phyMapper.get(entity);
    VelocityComponent velocity = velMapper.get(entity);

    if (path.waypoints.isEmpty()) {
        velocity.velocity.setZero();
        return;
    }

    Vector2 target = path.waypoints.peek();
    Vector2 currentPos = physics.body.getPosition();

    Vector2 direction = new Vector2(target).sub(currentPos);
    float distance = direction.len();

  //  System.out.println(" Target: " + target + " | Pos: " + currentPos + " | Distance: " + distance);

    if (distance < 50f) {
        SoundManager.getInstance().play("wayPoint");
        path.waypoints.poll();
        velocity.velocity.setZero();
    } else {
        velocity.velocity.set(direction.nor().scl(100f));
    }
}


}
