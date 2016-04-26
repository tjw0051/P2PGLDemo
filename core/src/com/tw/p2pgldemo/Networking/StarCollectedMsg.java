package com.tw.p2pgldemo.Networking;

import com.badlogic.gdx.math.Vector3;

public class StarCollectedMsg {
    private Vector3 pos;

    public StarCollectedMsg(Vector3 pos) {
        this.pos = pos;
    }

    public Vector3 GetPos() { return pos; }
}
