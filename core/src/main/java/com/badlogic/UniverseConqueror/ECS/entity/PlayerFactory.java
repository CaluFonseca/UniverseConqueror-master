package com.badlogic.UniverseConqueror.ECS.entity;

import com.badlogic.UniverseConqueror.ECS.components.*;
import com.badlogic.UniverseConqueror.ECS.systems.AnimationLoader;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerFactory {

    //private final AssetManager assetManager;


    public static Entity createPlayer(PooledEngine engine,
                                      Vector2 position,
                                      ObjectMap<String, Sound> sounds,
                                      World world,
                                      AssetManager assetManager) {

        Entity entity = engine.createEntity();

        // Transformação e posição
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.position.set(position.x, position.y, 0);
        entity.add(transform);

        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.position.set(position.x, position.y);
        entity.add(positionComponent);

        // Física
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        BodyComponent bodyComponent = createBody(position, world);
        physicsComponent.body = bodyComponent.body;
        entity.add(physicsComponent);
        entity.add(bodyComponent);

        // Velocity e estado
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        entity.add(velocity);
        entity.add(state);

        // Carrega as animações com o AnimationLoader
        AnimationLoader animationLoader = new AnimationLoader(assetManager);
        ObjectMap<StateComponent.State, Animation<TextureRegion>> animations = animationLoader.loadAnimations();

        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        animationComponent.setAnimations(animations);
        entity.add(animationComponent);

        // Sons
        SoundComponent sound = engine.createComponent(SoundComponent.class);
        sound.sounds.putAll(sounds);
        entity.add(sound);

        // Outros componentes
        entity.add(engine.createComponent(AttackComponent.class));
        entity.add(engine.createComponent(JumpComponent.class));
        entity.add(engine.createComponent(PlayerComponent.class));
        entity.add(engine.createComponent(CameraComponent.class));
        entity.add(engine.createComponent(HealthComponent.class));

        // Associa o corpo à entidade
        bodyComponent.body.setUserData(entity);

        // Adiciona à engine
        engine.addEntity(entity);
        return entity;
    }


    // Função para criar o corpo físico usando Box2D
    public static BodyComponent createBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);  // Define a posição do corpo no mundo (centro do jogador)
        bodyDef.type = BodyDef.BodyType.DynamicBody;  // Corpo dinâmico

        // Criação do corpo no mundo
        Body body = world.createBody(bodyDef);

        // Criando a forma de colisão (garantindo que ela esteja centralizada)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(40f, 40f);  // Forma de colisão centralizada (box de 64x64)

        // Definição do fixture para o corpo
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;  // Definir a densidade do jogador
        fixtureDef.friction = 0.5f;  // Fricção para controlar deslizamento
        fixtureDef.restitution = 0.3f;  // Baixa restituição para evitar quicar

        // Aplica o fixture ao corpo
        body.createFixture(fixtureDef);

        // Ajuste do centro de massa (garantindo que está no centro do corpo)
//        MassData massData = new MassData();
//        massData.center.set(0, 0);  // Definir o centro de massa no centro do corpo
//        massData.mass = 1.0f;       // Defina a massa do jogador (ajuste conforme necessário)
//        massData.I = 0.1f;          // Definindo a inércia (resistência à rotação)
//        body.setMassData(massData); // Aplicando os dados de massa ao corpo


        // Descartar o shape após o uso para liberar memória
        shape.dispose();

        // Criando o BodyComponent e retornando
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.body = body;
        return bodyComponent;
    }


}
