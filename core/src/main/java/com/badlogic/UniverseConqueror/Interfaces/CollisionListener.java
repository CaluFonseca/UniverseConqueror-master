package com.badlogic.UniverseConqueror.Interfaces;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Interface `CollisionListener` que define os métodos necessários para lidar com colisões no jogo.
 * Implementando esta interface, uma classe pode responder aos eventos de colisão entre objetos físicos no mundo do jogo.
 */
public interface CollisionListener {

    /**
     * Método chamado quando ocorre o início de uma colisão entre dois objetos.
     *
     * @param fixtureA O primeiro fixture envolvido na colisão.
     * @param fixtureB O segundo fixture envolvido na colisão.
     * @param contact O contato entre os dois objetos, que contém informações sobre a colisão.
     */
    void beginContact(Fixture fixtureA, Fixture fixtureB, Contact contact);

    /**
     * Método chamado quando a colisão entre dois objetos termina.
     *
     * @param fixtureA O primeiro fixture envolvido na colisão.
     * @param fixtureB O segundo fixture envolvido na colisão.
     * @param contact O contato entre os dois objetos, que contém informações sobre a colisão.
     */
    void endContact(Fixture fixtureA, Fixture fixtureB, Contact contact);
}
