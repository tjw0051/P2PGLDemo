package com.tw.p2pgldemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Loads assets and exposes them to the game.
 */
public class AssetManager {
    public static AssetManager assetManager = new AssetManager();
    public static AssetManager GetInstance() { return assetManager; }

    public Texture grass;
    public Map loadedTextures, characterTextures; //String, Texture
    public Map levels; // String, String[]

    public AssetManager() {}

    public void LoadWorldAssets() {
        grass = new Texture(Gdx.files.internal("Textures/World/grass.png"));
    }

    public void LoadTextureKey() {
        loadedTextures = LoadKey("Textures/World/TextureKey.txt", "Textures/World/");
        /*
        FileHandle file = Gdx.files.internal("Textures/World/TextureKey.txt");
        String text = file.readString();
        String[] splitText = text.split("\r\n");
        loadedTextures = new HashMap();
        for(int i = 0; i < splitText.length; i++) {
            String [] keyValue = splitText[i].split("\\s+");
            //Gdx.app.log("P2PGL", keyValue[0]);
            //Gdx.app.log("P2PGL", keyValue[1]);
            if(keyValue[1] != "0")
                loadedTextures.put(keyValue[0], new Texture(Gdx.files.internal("Textures/World/" + keyValue[1])));
        }
        */
    }

    public void LoadCharacterKey() {
        characterTextures = LoadKey("Textures/Characters/CharacterKey.txt", "Textures/Characters/");
    }

    public HashMap LoadKey(String filename, String textureDir) {
        FileHandle file = Gdx.files.internal(filename);
        String text = file.readString();
        String[] splitText = text.split("\r\n");
        HashMap textures = new HashMap();
        for(int i = 0; i < splitText.length; i++) {
            String [] keyValue = splitText[i].split("\\s+");
            //Gdx.app.log("P2PGL", keyValue[0]);
            //Gdx.app.log("P2PGL", keyValue[1]);
            if(keyValue[1] != "0")
                textures.put(keyValue[0], new Texture(Gdx.files.internal(textureDir + keyValue[1])));
        }
        return textures;
    }

    public static String[][] LoadLevel(String levelName) {
        String level[][];

        FileHandle file = Gdx.files.internal("Levels/" + levelName + ".txt");
        String text = file.readString();
        int noOfLayers = Integer.parseInt((text.split("\\s+", 2))[0]);
        level = new String[noOfLayers][];
        String layers = (text.split("\\s+", 2))[1];
        for(int i = 0; i < noOfLayers; i++) {
            String layer = layers.split("\r\n\r\n")[i];
            level[i] = layer.split("\\s+");
        }

        //Gdx.app.log("P2PGL", " val1= " + layer1Values[0] + " valLast = " + layer1Values[layer1Values.length - 1]);
        //levels.put(levelName, layer1Values);
        return level;
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
