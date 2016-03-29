package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.IO.LevelData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t_j_w on 10/03/2016.
 */
public class World {

    private final Rectangle tileBoundiing = new Rectangle(0, 100, 100, 80);
    private final Rectangle tileTexRect = new Rectangle(0, 0, 100, 170);
    private final float levelScale = 0.7f;

    private TileLayer[] layers;
    private LevelData levelData;
    Vector2 pos;

    public World(String levelName) {
        //String[][] layerValues = AssetManager.LoadLevel(levelName);
        levelData = (LevelData)AssetManager.GetInstance().levels.get(levelName);
        String[][] layerValues = levelData.GetTileValues();
        layers = new TileLayer[layerValues.length];
        pos = new Vector2(Gdx.graphics.getWidth()/2 - (levelData.GetLevelWidth() * (tileTexRect.width*levelScale))/2,
                          (tileTexRect.height) + 30);//Gdx.graphics.getHeight()/2 - 300); //- (levelData.getLevelHeight() * (tileTexRect.height*levelScale))/2);

        for(int i = 0; i < layerValues.length; i++) {
            layers[i] = new TileLayer(tileBoundiing, //50, 82, 75 //122
                    tileTexRect,
                    new Vector2(0, 0),
                    new Vector2(levelData.GetLevelWidth(), levelData.getLevelHeight()), layerValues[i], levelScale, GetInteractions(i));
            //layers[i].SetPos(500, 300 + (i * -30));
            layers[i].SetPos(pos.x, pos.y + (i * -30));
        }
    }

    public void render(SpriteBatch batch) {
        for(TileLayer layer: layers)
            layer.render(batch);
        batch.begin();
        batch.end();
    }

    public List<Interaction> GetInteractions(int layer) {
        List<Interaction> interactionList = new ArrayList<Interaction>();
        for(Interaction interaction : levelData.getInteractionList()) {
            if(interaction.getPos().z == layer)
                interactionList.add(interaction);
        }
        return interactionList;
    }

    public int TileCollisionCheck(Rectangle rect, int layer) {
        return layers[layer].TileCollisionCheck(rect);
    }

    public Vector2 GetTilePos(int tileIndex) { return layers[0].GetTilePos(tileIndex); }

    public Vector2 GetTilePos(int x, int y) {
        return layers[0].GetTilePos(x, y);
    }

    public Tile GetTile(int layer, int tileIndex) { return layers[layer].GetTile(tileIndex); }

    /** Return the list of tiles on all layers overlapping rect.
     * @param rect
     * @return
     */
}
