package com.tw.p2pgldemo.Networking;

import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Created by t_j_w on 16/04/2016.
 */
public class MyProfile extends Profile implements IProfile {
    private String textureName;
    public MyProfile(InetAddress address, int port, int udpPort,
                     String localChannel, String name, IKey key) {
        super(address, port, udpPort, localChannel, name, key);
    }

    public String GetTextureName() { return textureName; }

    public void SetTextureName(String name) { this.textureName = name; }

    @Override
    public Type GetType() {
        return MyProfile.class;
    }
}
