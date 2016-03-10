package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.AssetManager;

/**
 * Created by t_j_w on 10/03/2016.
 */
public class World {

    TileLayer[] layers;

    public World(String levelName) {
        String[][] layerValues = AssetManager.LoadLevel(levelName);
        layers = new TileLayer[layerValues.length];

        for(int i = 0; i < layerValues.length; i++) {
            layers[i] = new TileLayer(new Rectangle(0, 0, 100, 122), //50, 82, 75
                    new Rectangle(0, 0, 100, 170),
                    new Vector2(0, 0),
                    new Vector2(10, 10), layerValues[i], 0.7f);
            layers[i].SetPos(500, 100);
        }
    }

    public void render(SpriteBatch batch) {
        for(TileLayer layer: layers)
            layer.render(batch);
        batch.begin();
        batch.end();
    }

    public int TileCollisionCheck(Rectangle rect) {
        return layers[0].TileCollisionCheck(rect);
    }

    public Vector2 GetTilePos(int tileIndex) {
        return layers[0].GetTilePos(tileIndex);
    }
}
