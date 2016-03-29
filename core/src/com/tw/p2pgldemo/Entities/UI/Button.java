package com.tw.p2pgldemo.Entities.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;

/**
 * Created by t_j_w on 27/03/2016.
 */
public class Button {
    private TextureRegion texture;
    private Rectangle rect;
    private float scale;
    private boolean flipX, flipY;


    private Button() {
        flipX = false;
        flipY = false;
    }

    public Button(String textureName, int posX, int posY, float scale) {
        this();
        SetTexture(textureName);
        rect = new Rectangle(posX, posY, texture.getRegionWidth() * scale, texture.getRegionHeight() * scale);
    }

    public Button(Texture texture, int posX, int posY, float scale) {
        this();
        SetTexture(texture);
        rect = new Rectangle(posX, posY, texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public Button(TextureRegion textureRegion, int posX, int posY, float scale) {
        this();
        SetTexture(textureRegion);
        rect = new Rectangle(posX, posY, texture.getRegionWidth() * scale, texture.getRegionHeight() * scale);
    }

    public Vector2 GetCenterPos() {
        return new Vector2(rect.x + rect.width/2, rect.y + rect.height/2);
    }

    public void SetPos(float x, float y) {
        rect.x = x;
        rect.y = y;
    }

    public void SetTexture(TextureRegion textureRegion) {
        this.texture = textureRegion;
    }

    public void SetTexture(Texture texture) {
        this.texture = new TextureRegion(texture);
    }

    public void SetTexture(String textureName) {
        this.texture = new TextureRegion((Texture) com.tw.p2pgldemo.IO.AssetManager.GetInstance().ui.get(textureName));
    }

    public void SetCenterPos(int x, int y) {
        rect.x = x - rect.width/2;
        rect.y = y - rect.height/2;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(texture, rect.x, rect.y,
                rect.width, rect.height);
        spriteBatch.end();
    }

    public void Flip(boolean flipX, boolean flipY) {
        texture.flip(flipX, flipY);
    }

    public Rectangle GetRect() {
        return rect;
    }
}
