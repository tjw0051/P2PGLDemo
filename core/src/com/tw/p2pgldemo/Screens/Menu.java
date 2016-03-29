package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.tw.p2pgldemo.Entities.UI.*;

/**
 * Created by t_j_w on 27/03/2016.
 */
public class Menu {
    //Texture menuButton, exitButton;
    Rectangle menuButtonRect, exitButtonRect;
    MenuState menuState;
    Button menuButton, exitButton;

    private enum MenuState {
        OPEN,
        CLOSED
    }

    public Menu() {
        menuButton = new Button("menubutton", 0, 0, 0.7f);
        menuButton.SetPos(Gdx.graphics.getWidth() - menuButton.GetRect().getWidth(),
                Gdx.graphics.getHeight() - menuButton.GetRect().getHeight());

        exitButton = new Button("exitbutton", 0, 0, 0.7f);
        exitButton.SetCenterPos(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

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
}
