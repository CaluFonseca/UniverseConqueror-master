package com.badlogic.UniverseConqueror.Context;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HUDContext {
    private Label healthLabel, attackPowerLabel, itemsLabel, timerLabel, enemiesKilledLabel;
    private TextureRegionDrawable healthBackground, attackPowerBackground, itemsBackground;
    private TextureRegion cameraOnTexture, cameraOffTexture;
    private Image cameraIconImage;
    private Skin skin;

    // Getters
    public Label getHealthLabel() {
        return healthLabel;
    }

    public Label getAttackPowerLabel() {
        return attackPowerLabel;
    }

    public Label getItemsLabel() {
        return itemsLabel;
    }

    public Label getTimerLabel() {
        return timerLabel;
    }

    public Label getEnemiesKilledLabel() {
        return enemiesKilledLabel;
    }

    public TextureRegionDrawable getHealthBackground() {
        return healthBackground;
    }

    public TextureRegionDrawable getAttackPowerBackground() {
        return attackPowerBackground;
    }

    public TextureRegionDrawable getItemsBackground() {
        return itemsBackground;
    }

    public TextureRegion getCameraOnTexture() {
        return cameraOnTexture;
    }

    public TextureRegion getCameraOffTexture() {
        return cameraOffTexture;
    }

    public Image getCameraIconImage() {
        return cameraIconImage;
    }

    public Skin getSkin() {
        return skin;
    }

    // Setters
    public void setHealthLabel(Label healthLabel) {
        this.healthLabel = healthLabel;
    }

    public void setAttackPowerLabel(Label attackPowerLabel) {
        this.attackPowerLabel = attackPowerLabel;
    }

    public void setItemsLabel(Label itemsLabel) {
        this.itemsLabel = itemsLabel;
    }

    public void setTimerLabel(Label timerLabel) {
        this.timerLabel = timerLabel;
    }

    public void setEnemiesKilledLabel(Label enemiesKilledLabel) {
        this.enemiesKilledLabel = enemiesKilledLabel;
    }

    public void setHealthBackground(TextureRegionDrawable healthBackground) {
        this.healthBackground = healthBackground;
    }

    public void setAttackPowerBackground(TextureRegionDrawable attackPowerBackground) {
        this.attackPowerBackground = attackPowerBackground;
    }

    public void setItemsBackground(TextureRegionDrawable itemsBackground) {
        this.itemsBackground = itemsBackground;
    }

    public void setCameraOnTexture(TextureRegion cameraOnTexture) {
        this.cameraOnTexture = cameraOnTexture;
    }

    public void setCameraOffTexture(TextureRegion cameraOffTexture) {
        this.cameraOffTexture = cameraOffTexture;
    }

    public void setCameraIconImage(Image cameraIconImage) {
        this.cameraIconImage = cameraIconImage;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public void updateCameraIcon(boolean isFollowing) {

        TextureRegion icon = isFollowing ? cameraOnTexture : cameraOffTexture;
        cameraIconImage.setDrawable(new TextureRegionDrawable(icon));
    }
}
