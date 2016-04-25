package com.tw.p2pgldemo.Networking;

import P2PGL.Config.KademliaConfig;
import P2PGL.IP2PGLFactory;
import P2PGL.P2PGLFactory;

/**
 * Created by t_j_w on 25/04/2016.
 */
public class CustomP2PGLFactory extends P2PGLFactory implements IP2PGLFactory {
    @Override
    public KademliaConfig GetDHTConfig() {
        return new KademliaConfig(20000L, 2000L, 2000L, 10, 5, 3, 1);
    }
}
