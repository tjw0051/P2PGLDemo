package com.tw.p2pgldemo.Networking;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by t_j_w on 15/03/2016.
 */
public class PlayerState {


    private String name;
    private String textureName;
    private String world;
    private Vector3 pos;
    private Vector3 destination;

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public Vector3 getPos() {
        return pos;
    }

    public String getTextureName() { return textureName; }

    public Vector3 getDestination() {
        return destination;
    }

    public PlayerState() {

    }

    public PlayerState(String name, String textureName, String world, Vector3 pos, Vector3 destination) {
        this.name = name;
        this.textureName = textureName;
        this.world = world;
        this.pos = pos;
        this.destination = destination;
    }
}
