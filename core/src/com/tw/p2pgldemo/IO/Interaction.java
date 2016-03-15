package com.tw.p2pgldemo.IO;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by t_j_w on 15/03/2016.
 */
public class Interaction {


    private String name;
    private Vector3 pos;
    private String[] params;

    public Interaction() {}
    public Interaction(String name, Vector3 pos, String[] params) {
        this.name = name;
        this.pos = pos;
        this.params = params;
    }
    public String getName() {
        return name;
    }

    public String[] getParams() { return params; }

    public Vector3 getPos() {
        return pos;
    }
}
