package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.AssetManager;
import org.w3c.dom.css.Rect;

import javax.xml.soap.Text;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class Tile {
    Texture texture;
    Rectangle tileRect;
    Rectangle textureRect;
    Vector2 scale;
    Vector2 cellPos;

    public Tile(Rectangle tileRect, Rectangle textureRect, Texture texture, Vector2 cellPos) {
        this.tileRect = tileRect;
        this.textureRect = textureRect;
        this.texture = texture;
        this.cellPos = cellPos;
    }

    public void SetPos(float x, float y) {
        tileRect.x = x;
        tileRect.y = y;
    }

    public Vector2 GetCenter() {
        return new Vector2(tileRect.x + (tileRect.width / 2), tileRect.y + (tileRect.height / 2));
    }

    public Rectangle GetRect() {
        return tileRect;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(texture,
                tileRect.x, tileRect.y, textureRect.width, textureRect.height);
        spriteBatch.end();
    }
}
