package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.IO.Interaction;

import java.util.ArrayList;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class TileLayer {
    private Rectangle tileBoundingBox;
    private Rectangle textureRect;
    private Vector2 pos;
    private Vector2 cells;
    private String levelName;
    private String[] tileValues;
    private AssetManager aM;
    private int screenHeight;
    private java.util.List<Tile> tiles;
    private float scaling;

    public TileLayer(Rectangle tileBoundingBox, Rectangle textureRect,
                     Vector2 pos, Vector2 cells, String[] tileValues,
                     float scaling, java.util.List<Interaction> interactionList) {
        this.tileBoundingBox = tileBoundingBox;
        this.textureRect = textureRect;
        this.pos = pos;
        this.cells = cells;
        this.tileValues = tileValues;
        aM = AssetManager.GetInstance();
        this.scaling = scaling;
        CreateTiles(interactionList);
    }

    private void CreateTiles(java.util.List<Interaction> interactionList) {
        tiles = new ArrayList<Tile>();

        int tileValuesIter = 0;
        for(int y = 0; y < cells.y; y++) {
            for(int x = 0; x < cells.x; x++) {
                String tileValue = tileValues[tileValuesIter];
                if(!tileValue.equals("0")) {
                    Texture tex = (Texture) aM.loadedTextures.get(tileValue);
                    Rectangle textureShape =
                            new Rectangle(textureRect.x * scaling, textureRect.y * scaling,
                            textureRect.width * scaling, textureRect.height * scaling);
                    Rectangle tileBoundingBox =
                            new Rectangle(this.tileBoundingBox.x * scaling, this.tileBoundingBox.y * scaling,
                            this.tileBoundingBox.width * scaling, this.tileBoundingBox.height * scaling);
                    Tile tile = new Tile(tileBoundingBox, textureShape, tex, new Vector2(
                            pos.x + (x * this.tileBoundingBox.width * scaling),
                            pos.y + (y * this.tileBoundingBox.height * scaling)),
                            new Vector2(x, y));
                    for(Interaction interaction: interactionList) {
                        if(interaction.getPos().x == x && interaction.getPos().y == y) {
                            tile.SetInteraction(interaction);
                        }
                    }
                    tiles.add(tile);
                }
                tileValuesIter++;
            }
        }
    }

    public void SetPos(float x, float y) {
        this.pos.x  = x;
        this.pos.y = y;
        for(int i = 0; i < tiles.size(); i ++) {
            Vector2 layerIndex = tiles.get(i).GetLayer();
            tiles.get(i).SetPos(pos.x + (layerIndex.x * this.tileBoundingBox.width * scaling),
                    pos.y + (layerIndex.y * this.tileBoundingBox.height * scaling));
        }
    }

    /** Check for a collision with a tile in the tile layer.
     * @param collider  Position of object colliding.
     * @return  index of intersecting tile. Returns -1 if no collision.
     */
    public int TileCollisionCheck(Rectangle collider) {
        //for(int i = 0; i < tiles.size(); i++) {
        for(int i = tiles.size(); i --> 0;) {
            if (tiles.get(i).GetBoundingBox().overlaps(collider))
                return i;
        }
        return -1;
    }

    public Vector2 GetTilePos(int tileIndex) {
        return tiles.get(tileIndex).GetCenter();
    }

    public Vector2 GetTilePos(int x, int y) {
        for(int i = 0; i < tiles.size(); i++) {
            if(tiles.get(i).getLayerIndex().x == x && tiles.get(i).getLayerIndex().y == y) {
                return GetTilePos(i);
            }
        }
        return null;
    }

    public Tile GetTile(int tileIndex) { return tiles.get(tileIndex); }

    private static float InvertHeight(float height) {
        return Gdx.graphics.getHeight() - height;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.end();
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).render(spriteBatch);
        }
    }
}
