package com.tw.p2pgldemo.IO;

import java.util.List;
import java.util.Map;

/**
 * Created by t_j_w on 13/03/2016.
 */
public class LevelData {
    private String[][] tileValues;
    private int levelWidth;
    private int levelHeight;
    private String name;

    public List<Interaction> getInteractionList() { return interactionList; }

    private List<Interaction> interactionList;

    public LevelData() {}

    public LevelData(String name, String[][] tileValues) {
        this.name = name;
        this.tileValues = tileValues;
    }

    public String[][] GetTileValues() {
        return tileValues;
    }

    public Interaction GetInteraction(int x, int y, int layer) {
        for (Interaction interaction : interactionList) {
            if(interaction.getPos().x == x && interaction.getPos().y == y && interaction.getPos().z == layer) {
                return interaction;
            }
        }
        return null;
    }

    public int GetLevelWidth() { return levelWidth; }

    public int getLevelHeight() { return levelHeight; }

}
