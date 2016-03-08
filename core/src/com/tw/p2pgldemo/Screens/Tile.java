package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.AssetManager;

import javax.xml.soap.Text;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class Tile {
    Texture texture;
    Rectangle tileRect;
    Rectangle textureRect;
    Vector2 scale;

    public Tile(Rectangle tileRect, Rectangle textureRect, Texture texture) {
        this.tileRect = tileRect;
        this.textureRect = textureRect;
        this.texture = texture;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(texture,
                tileRect.x, tileRect.y, textureRect.width, textureRect.height);
        spriteBatch.end();
    }
}
