package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.tw.p2pgldemo.Entities.UI.*;

/**
 * Created by t_j_w on 27/03/2016.
 */
public class Menu {
    //Texture menuButton, exitButton;
    private Rectangle menuButtonRect, exitButtonRect;
    private MenuState menuState;
    private BitmapFont font;
    private Button menuButton, exitButton;
    private int score;

    private enum MenuState {
        OPEN,
        CLOSED
    }

    public Menu() {
        score = 0;
        menuButton = new Button("menubutton", 0, 0, 0.7f);
        menuButton.SetPos(Gdx.graphics.getWidth() - menuButton.GetRect().getWidth(),
                Gdx.graphics.getHeight() - menuButton.GetRect().getHeight());

        exitButton = new Button("exitbutton", 0, 0, 0.7f);
        exitButton.SetCenterPos(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        font = new BitmapFont();
        font.getData().setScale(2.0f, 2.0f);

        menuState = MenuState.CLOSED;
    }

    public void render(SpriteBatch spriteBatch) {
        switch (menuState) {
            case OPEN: {
                menuButton.render(spriteBatch);
                exitButton.render(spriteBatch);
                break;
            }
            case CLOSED: {
                menuButton.render(spriteBatch);
                break;
            }
        }
        spriteBatch.begin();
        font.draw(spriteBatch, "Score: " + score, Gdx.graphics.getWidth() - 150, 50);
        spriteBatch.end();
    }

    public boolean ProcessInput(Rectangle cursor) {
        switch (menuState) {
            case OPEN: {
                if(cursor.overlaps(menuButton.GetRect())) {
                    menuState = MenuState.CLOSED;
                    return true;
                }
                if(cursor.overlaps(exitButton.GetRect())) {
                    Gdx.app.exit();
                    return true;
                }
                break;
            }
            case CLOSED: {
                if(cursor.overlaps(menuButton.GetRect())) {
                    menuState = MenuState.OPEN;
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public void SetScore(int score) {
        this.score = score;
    }

    public void IncrementScore() {
        this.score++;
    }
}
