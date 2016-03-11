package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class Tile {
    Texture texture;
    Rectangle rect;
    Rectangle tileBoundingBox;
    Rectangle textureRect;
    Vector2 scale;
    Vector2 pos;
    Vector2 layerIndex;

    public Tile(Rectangle tileBoundingBox, Rectangle textureRect, Texture texture, Vector2 pos, Vector2 layerIndex) {
        this.tileBoundingBox = tileBoundingBox;
        this.textureRect = textureRect;
        this.texture = texture;
        this.pos = pos;
        this.layerIndex = layerIndex;
        this.rect = new Rectangle(this.pos.x - tileBoundingBox.x,
                Gdx.graphics.getHeight() - (this.pos.y - tileBoundingBox.y),
                textureRect.width, textureRect.height);
    }

    public void SetPos(float x, float y) {
        //tileBoundingBox.x = x;
        //tileBoundingBox.y = y;
        this.pos.x = x;
        this.pos.y = y;
        this.rect.x = pos.x - tileBoundingBox.x;
        this.rect.y = Gdx.graphics.getHeight() - (pos.y - tileBoundingBox.y);
    }

    public Vector2 GetCenter() {
        /*
        return new Vector2(tileBoundingBox.x + (tileBoundingBox.width / 2),
                tileBoundingBox.y + (tileBoundingBox.height / 2));
                */
        return new Vector2(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
    }

    public Rectangle GetRect() {
        //return tileBoundingBox;
        return rect;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        /*
        spriteBatch.draw(texture,
                tileBoundingBox.x, tileBoundingBox.y, textureRect.width, textureRect.height);
                */
        spriteBatch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        spriteBatch.end();
    }
}
