package com.tw.p2pgldemo.IO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Loads assets and exposes them to the game.
 */
public class AssetManager {
    private static final AssetManager assetManager = new AssetManager();
    public static AssetManager GetInstance() { return assetManager; }

    public Texture grass;
    public Map loadedTextures, characterTextures; //String, Texture
    public Map levels; // String, String[][]
    public Map interactions;
    public Map ui;
    private Json json;

    private AssetManager() {
        json = new Json();
    }

    public void LoadWorldAssets() {
        grass = new Texture(Gdx.files.internal("Textures/World/grass.png"));
    }

    public void LoadTextures() {
        loadedTextures = new HashMap();// LoadKey("Textures/World/TextureKey.txt", "Textures/World/");
        HashMap key = LoadKey("Textures/World/TextureKey.txt");
        Iterator iter = key.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            loadedTextures.put(entry.getKey(),
                    new Texture(Gdx.files.internal("Textures/World/" + ((String[])entry.getValue())[1])));
        }
    }

    public void LoadCharacters() {
        //characterTextures = LoadKey("Textures/Characters/CharacterKey.txt", "Textures/Characters/");
        characterTextures = new HashMap();
        HashMap key = LoadKey("Textures/Characters/CharacterKey.txt");
        Iterator iter = key.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            characterTextures.put(entry.getKey(),
                    new Texture(Gdx.files.internal("Textures/Characters/" + ((String[])entry.getValue())[1])));
        }
    }

    public void LoadLevels() {
        levels = new HashMap();
        HashMap key = LoadKey("Levels/LevelKey.txt");
        Iterator iter = key.entrySet().iterator();
        FileHandle file;
        String text;
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            file = Gdx.files.internal("Levels/" + ((String[])entry.getValue())[1]);
            text = file.readString();
            LevelData level = json.fromJson(LevelData.class, text);
            levels.put(entry.getKey(), level);
        }
    }

    public void LoadUI() {
        ui = new HashMap();
        HashMap key = LoadKey("UI/UIKey.txt");
        Iterator iter = key.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ui.put(entry.getKey(),
                    new Texture(Gdx.files.internal("UI/" + ((String[])entry.getValue())[1])));
        }
    }

    public void LoadInteractions() {
        interactions = LoadKey("Textures/World/InteractionKey.txt");
    }

    public static HashMap LoadKey(String filename) {
        FileHandle file = Gdx.files.internal(filename);
        String text = file.readString();
        String[] splitText = text.split("\r\n");
        HashMap keyValuePair = new HashMap();
        for(int i = 0; i < splitText.length; i++) {
            String [] keyValue = splitText[i].split("\\s+");
            if(keyValue[1] != "0")
                keyValuePair.put(keyValue[0], keyValue);
        }
        return keyValuePair;
    }

    //TODO: test
    public void dispose() {
        grass.dispose();
        Iterator iter = loadedTextures.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            ((Texture)entry.getValue()).dispose();
            iter.remove();
        }
    }
}
