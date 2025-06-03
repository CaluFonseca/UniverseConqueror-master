package com.badlogic.UniverseConqueror.ECS.components;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component {

    // Indica se o personagem está no ar
    public boolean isJumping = false;

    // Força aplicada no pulo
    public float jumpForce = 500f;

    // Indica se o personagem pode pular
    public boolean canJump = true;

    // Duração total atual do pulo
    public float jumpDuration = 0f;

    // Tempo atual desde que o pulo começou
    public float currentJumpTime = 0f;

    // Tempo máximo permitido para o pulo durar
    public float maxJumpTime = 0.5f;

    // Posição vertical do chão (para controle de término do pulo)
    public float groundY = 0f;
}
