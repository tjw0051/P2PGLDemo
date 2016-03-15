package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.IO.Interaction;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class Tile {
    private Texture texture;
    private Rectangle rect;
    private Rectangle tileBoundingBox;
    private Rectangle textureRect;
    private Vector2 scale;
    private Vector2 pos;
    private Vector2 layerIndex;
    private boolean isInteractive;
    private Interaction interaction;

    public Tile(Rectangle tileBoundingBox, Rectangle textureRect, Texture texture, Vector2 pos, Vector2 layerIndex) {
        this.tileBoundingBox = tileBoundingBox;
        this.textureRect = textureRect;
        this.texture = texture;
        this.pos = pos;
        this.layerIndex = layerIndex;
        this.isInteractive = false;
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

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        spriteBatch.end();
    }

    public void SetInteraction(Interaction interaction) {
        this.interaction = interaction;
        isInteractive = true;
    }

    public Rectangle GetRect() { return rect; }

    public Vector2 GetPos() { return pos; }

    public Vector2 GetLayer() { return layerIndex; }

    public Interaction GetInteraction() { return interaction; }

    public boolean isInteractive() { return isInteractive; }
}
