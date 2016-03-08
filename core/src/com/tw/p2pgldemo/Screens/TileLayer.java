package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.tw.p2pgldemo.AssetManager;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class TileLayer {
    Rectangle tileRect;
    Rectangle textureRect;
    Vector2 pos;
    Vector2 cells;
    String levelName;
    String[] tileValues;
    AssetManager aM;
    int screenHeight;
    java.util.List<Tile> tiles;
    float scaling;

    /**
     * @param tileRect  Shape of tile
     * @param textureRect   Shape of texture
     * @param pos   position offset of entire layer
     * @param cells Number of cells in layer
     * @param level Level name of file to be loaded
     */
    public TileLayer(Rectangle tileRect, Rectangle textureRect, Vector2 pos, Vector2 cells, String level, float scaling) {
        this.tileRect = tileRect;
        this.textureRect = textureRect;
        this.pos = pos;
        this.cells = cells;
        tileValues = AssetManager.LoadLevel("a1");
        aM = AssetManager.GetInstance();
        this.scaling = scaling;
        CreateTiles();
    }

    private void CreateTiles() {
        tiles = new ArrayList<Tile>();

        int tileValuesIter = 0;
        for(int x = 0; x < cells.x; x++) {
            for(int y = 0; y < cells.y; y++) {
                String tileValue = tileValues[tileValuesIter];
                if(!tileValue.equals("0")) {
                    Texture tex = (Texture) aM.loadedTextures.get(tileValue);
                    Rectangle textureShape = new Rectangle(textureRect.x * scaling, textureRect.y * scaling,
                            textureRect.width * scaling, textureRect.height * scaling);
                    Rectangle tileShape = new Rectangle(pos.x + (x * tileRect.width * scaling),
                                                Gdx.graphics.getHeight() - (pos.y + (y * tileRect.height * scaling)) - textureRect.height,
                                                tileRect.width * scaling,
                                                tileRect.height * scaling);
                    tiles.add(new Tile(tileShape, textureShape, tex));

                }
                tileValuesIter++;
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {

        spriteBatch.begin();
        /*
        int tileValuesIter = 0;
        for(int x = 0; x < cells.x; x++) {
            for(int y = 0; y < cells.y; y++) {
                String tileValue = tileValues[tileValuesIter];
                if(!tileValue.equals("0")) {
                    Texture tex = (Texture) aM.loadedTextures.get(tileValue);
                    spriteBatch.draw(tex,
                            pos.x + (x * tileRect.width),
                            InvertHeight(pos.y + (y * tileRect.height)) - tileRect.height,
                            textureRect.width,
                            textureRect.height);

                }
                tileValuesIter++;
            }
        }
        */
       spriteBatch.end();

        for(int i = 0; i < tiles.size(); i++) {
            tiles.get(i).render(spriteBatch);
        }
    }
    private static float InvertHeight(float height) {
        return Gdx.graphics.getHeight() - height;
    }
}
