package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.IO.LevelData;

import java.util.ArrayList;
import java.util.List;

public class World {

    private final Rectangle tileBounding = new Rectangle(0, 100, 100, 80);
    private final Rectangle tileTexRect = new Rectangle(0, 0, 100, 170);
    private final float levelScale = 0.7f;

    private List<TileLayer> layers;
    private LevelData levelData;
    Vector2 pos;

    public World(String levelName) {
        levelData = (LevelData)AssetManager.GetInstance().levels.get(levelName);
        LoadWorld();
    }

    public World(LevelData levelData) {
        this.levelData = levelData;
        LoadWorld();
    }

    private void LoadWorld() {
        String[][] layerValues = levelData.GetTileValues();
        layers = new ArrayList<TileLayer>();
        pos = new Vector2(Gdx.graphics.getWidth()/2 - (levelData.GetLevelWidth() * (tileTexRect.width*levelScale))/2,
                (tileTexRect.height) + 30);

        for(int i = 0; i < layerValues.length; i++) {
            AddLayer(new TileLayer(tileBounding,
                            tileTexRect,
                            new Vector2(0, 0),
                            new Vector2(levelData.GetLevelWidth(), levelData.getLevelHeight()), layerValues[i], levelScale, GetInteractions(i)),
                    pos.x, pos.y + (i* -30));
        }
    }

    public void AddLayer(TileLayer layer,float posX,float posY) {
        layers.add(layer);
        int layerIndex = layers.size()-1;
        layers.get(layerIndex).SetPos(pos.x, pos.y + (layerIndex * -30));
    }

    public void SetLayer(TileLayer layer, float posX, float posY, int index) {
        layers.set(index, layer);
        layers.get(index).SetPos(pos.x, pos.y + (index * -30));
    }

    public void RemoveTileAt(Vector3 itemPos) {
        layers.get((int)itemPos.z).RemoveTileAt(itemPos);
        levelData.SetTileValue((int)itemPos.x, (int)itemPos.y, (int)itemPos.z, "0");
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
        return layers.get(layer).TileCollisionCheck(rect);
    }

    public Vector2 GetTilePos(int tileIndex) { return layers.get(0).GetTilePos(tileIndex); }

    public Vector2 GetTilePos(int x, int y) {
        return layers.get(0).GetTilePos(x, y);
    }

    public Tile GetTile(int layer, int tileIndex) { return layers.get(layer).GetTile(tileIndex); }

    public LevelData GetLevelData() { return levelData; }
}
