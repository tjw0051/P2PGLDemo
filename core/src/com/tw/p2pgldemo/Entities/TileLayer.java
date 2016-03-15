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

    /**
     * @param tileBoundingBox  Shape of tile
     * @param textureRect   Shape of texture
     * @param pos   position offset of entire layer
     * @param cells Number of cells in layer
     */
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
                    /*
                    Rectangle textureShape = new Rectangle(textureRect.x * scaling, textureRect.y * scaling,
                            textureRect.width * scaling, textureRect.height * scaling);
                    Rectangle tileBoundingBox = new Rectangle(pos.x + ((x * this.tileBoundingBox.width + this.tileBoundingBox.x - this.tileBoundingBox.x) * scaling),
                                                Gdx.graphics.getHeight() - (pos.y + (y * this.tileBoundingBox.height * scaling)) - textureRect.height,
                                                this.tileBoundingBox.width * scaling,
                                                this.tileBoundingBox.height * scaling);
                                                */
                    Rectangle textureShape = new Rectangle(textureRect.x * scaling, textureRect.y * scaling,
                            textureRect.width * scaling, textureRect.height * scaling);
                    Rectangle tileBounding = new Rectangle(tileBoundingBox.x * scaling, tileBoundingBox.y * scaling,
                            tileBoundingBox.width * scaling, tileBoundingBox.height * scaling);
                    Tile tile = new Tile(tileBounding, textureShape, tex, new Vector2(
                            pos.x + (x * this.tileBoundingBox.width * scaling),
                            pos.y + (y * this.tileBoundingBox.height * scaling)),
                            new Vector2(x, y));
                    for(Interaction interaction: interactionList) {
                        if(interaction.getPos().x == x && interaction.getPos().y == y) {
                            tile.SetInteraction(interaction);
                        }
                    }
                    /*
                    if(AssetManager.GetInstance().interactions.containsKey(tileValue)) {
                        tile.isInteractive = true;
                        tile.interactionParams = (String[])AssetManager.GetInstance().interactions.get(tileValue);

                    }*/
                    tiles.add(tile);
                }
                tileValuesIter++;
            }
        }
    }

    public void SetPos(float x, float y) {
        this.pos.x  = x;
        this.pos.y = y;
        /*
        int tileIter = 0;
        for(int yy = 0; yy < cells.y; yy++) {
            for (int xx = 0; xx < cells.x; xx++) {
                tiles.get(tileIter).SetPos(pos.x + (xx * this.tileBoundingBox.width * scaling),
                        pos.y + (yy * this.tileBoundingBox.height * scaling));
                tileIter++;
            }
        } */
        for(int i = 0; i < tiles.size(); i ++) {
            Vector2 layerIndex = tiles.get(i).GetLayer();
            tiles.get(i).SetPos(pos.x + (layerIndex.x * this.tileBoundingBox.width * scaling),
                    pos.y + (layerIndex.y * this.tileBoundingBox.height * scaling));
        }

        /*
        for(int i = 0; i < tiles.size(); i++) {
            tiles.get(i).SetPos(pos.x + (tiles.get(i).pos.x * tileBoundingBox.width * scaling),
                    Gdx.graphics.getHeight() - (pos.y + (tiles.get(i).pos.y * tileBoundingBox.height * scaling)) - textureRect.height);

        }
        */

    }

    /** Check for a collision with a tile in the tile layer.
     * @param collider  Position of object colliding.
     * @return  index of intersecting tile. Returns -1 if no collision.
     */
    public int TileCollisionCheck(Rectangle collider) {
        //for(int i = 0; i < tiles.size(); i++) {
        for(int i = tiles.size(); i --> 0;) {
            if (tiles.get(i).GetRect().overlaps(collider))
                return i;
        }
        return -1;
    }

    public Vector2 GetTilePos(int tileIndex) {
        return tiles.get(tileIndex).GetCenter();
    }

    public Tile GetTile(int tileIndex) { return tiles.get(tileIndex); }

    public void render(SpriteBatch spriteBatch) {

        spriteBatch.begin();
       spriteBatch.end();

        for(int i = 0; i < tiles.size(); i++) {
            tiles.get(i).render(spriteBatch);
        }
    }
    private static float InvertHeight(float height) {
        return Gdx.graphics.getHeight() - height;
    }
}
