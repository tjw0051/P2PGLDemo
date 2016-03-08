package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.tw.p2pgldemo.Game;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class MainMenu implements Screen{

    Game game;

    public MainMenu(Game game) {
        this.game = game;
    }
    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isTouched())
            game.setScreen(new GameScreen(game));
            dispose();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
