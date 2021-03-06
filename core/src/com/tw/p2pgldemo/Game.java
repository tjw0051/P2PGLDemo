package com.tw.p2pgldemo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.Screens.MainMenu;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;
	public AssetManager assetManager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = AssetManager.GetInstance();
		setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
		super.render();
	}
	@Override
	public void dispose() {
		batch.dispose();

	}
}
