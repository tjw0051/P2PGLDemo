package com.tw.p2pgldemo.IO;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by t_j_w on 15/03/2016.
 */
public class Interaction {


    private String name;
    private Vector3 pos;
    private String param;

    public Interaction() {}
    public Interaction(String name, Vector3 pos, String param) {
        this.name = name;
        this.pos = pos;
        this.param = param;
    }
    public String getName() {
        return name;
    }
    public String getParam() {
        return param;
    }
    public Vector3 getPos() {
        return pos;
    }
}
