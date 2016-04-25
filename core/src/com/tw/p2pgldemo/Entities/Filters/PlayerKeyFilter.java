package com.tw.p2pgldemo.Entities.Filters;

import P2PGL.Util.IKey;
import com.tw.p2pgldemo.Entities.Player;

import java.util.List;

/**
 * Created by t_j_w on 24/04/2016.
 */
public class PlayerKeyFilter {

    public static int PlayerWithKey(List<Player> players, IKey key) {
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).GetKey().equals(key))
                return i;
        }
        return -1;
    }
}
