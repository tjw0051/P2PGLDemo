package com.tw.p2pgldemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Loads assets and exposes them to the game.
 */
public class AssetManager {

    public Texture grass;

    public AssetManager() {}

    public void LoadWorldAssets() {
        grass = new Texture(Gdx.files.internal("Textures/grass.png"));
    }

    public void dispose() {

    }
}
