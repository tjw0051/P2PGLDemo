package com.tw.p2pgldemo.Networking;


import P2PGL.*;
import P2PGL.DHT.KademliaFacade;
import P2PGL.UDP.*;
import P2PGL.Profile.*;
import P2PGL.EventListener.MessageReceivedListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.tw.p2pgldemo.Entities.Player;
import com.tw.p2pgldemo.IO.LevelData;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by t_j_w on 15/03/2016.
 */
public class Connection implements MessageReceivedListener{
    private static final Connection connection = new Connection();
    public static Connection GetInstance() { return connection; }

    private P2PGL.Connection conn;
    //private UDPChannel udpChannel;
    private Json json;
    private String name;
    private P2PGL.Profile.Profile profile;
    private PlayerState playerState;
    private List<PlayerState> localPlayerStates;
    private List<StarCollectedMsg> starMessages;

    private Connection() {
        json = new Json();
        localPlayerStates = new ArrayList<PlayerState>();
        starMessages = new ArrayList<StarCollectedMsg>();
    }

    /** Create profile with random name from 0-100. Connect to server.
     *  Listen on UDP channel (Connection() channel + 1)
     * @param name  name of player.
     */
    public int Connect(String name, String worldName) {
        int err = 0;
        this.name = name;
        err = ConnectDHT(worldName);
        conn.AddMessageListener(this);
        //ConnectUDP();

        return err;
    }

    public int Disconnect() {
        try {
            conn.Disconnect();
            //udpChannel.Stop();
        } catch(IOException ioe) {
            return -1;
        }
        return 0;
    }

    public LevelData JoinWorld(String worldName) {
        try {
            conn.StartUDPChannel(worldName);
            localPlayerStates.clear();
            return GetWorld(worldName);
        } catch (IOException ioe) {
            System.out.println("Error changing UDP Channel");
        }
        return null;
    }

    public void SetWorld(LevelData levelData) {
        PlayerState state;
        try {
            conn.Store(new Key(levelData.GetName()), levelData, levelData.getClass());
        } catch (IOException ioe) {

        }
    }

    public LevelData GetWorld(String levelName) {
        try {
            LevelData levelData = conn.Get(new Key(levelName), LevelData.class);
            return levelData;
        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        }
        return null;
    }

    private int ConnectDHT(String worldName) {
        int port = new RandomXS128().nextInt(100);
        profile = new Profile(InetAddress.getLoopbackAddress(), 4000 + port, name);
        profile.SetUDPChannel(worldName);

        conn = new P2PGL.Connection(profile, new KademliaFacade());
        try {
            conn.Connect("server", InetAddress.getLoopbackAddress(), 4000);
            return 0;
        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to connect to server");
            return -1;
        }
    }

    public int SendState(PlayerState playerState) {
        this.playerState = playerState;
        try {
            conn.Broadcast(playerState, PlayerState.class);
        } catch(IOException ioe) {
            Gdx.app.log("Error", "Error sending state");
            //DiagnoseIOE(ioe);
        }
        return 0;
    }

    public void StarCollected(Vector3 starPos) {
        try {
            conn.Broadcast(new StarCollectedMsg(starPos), StarCollectedMsg.class);
        } catch(IOException ioe) {
            Gdx.app.log("Error", "Error sending star message");
        }
    }

    /** Retrieves the state of a player from the player name.
     * @param playerName
     * @return returns null if the playerState cannot be retrieved.
     */
    public PlayerState GetState(String playerName) {
        try {
            //String jsonState = conn.Get(new Key(playerName + "STATE"));
            //return json.fromJson(PlayerState.class, jsonState);
            return conn.Get(new Key(playerName + "STATE"), PlayerState.class);

        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to retrieve state of player: " + playerName);
        } catch (ClassNotFoundException cnfe) {

        }
        return null;
    }

    public List<PlayerState> GetStatesFromUDP() {
        return localPlayerStates;
    }

    public List<StarCollectedMsg> GetStarMessages() { return starMessages; }

    public void MessageReceivedListener(Object obj, Class messageType, IKey key) {
        //try {
            if(messageType.equals(PlayerState.class)) {
                System.out.println("PlayerState received");
                localPlayerStates.add((PlayerState)obj);
            }
            if(messageType.equals(StarCollectedMsg.class)) {
                System.out.println("Star received");
                starMessages.add((StarCollectedMsg)obj);
            }

            /*
            if (messageType.equals("com.tw.p2pgldemo.Networking.PlayerState")) {
                PlayerState state = (PlayerState) conn.GetUDPChannel().ReadNext();
                System.out.println("adding state: " + state.getName() + " world: " + state.getWorld());
                localPlayerStates.add(state);
            }
            if(messageType.equals(StarCollectedMsg.class.getTypeName())) {
                StarCollectedMsg starMsg = (StarCollectedMsg) conn.GetUDPChannel().ReadNext();
                if(starMsg != null)
                    starMessages.add(starMsg);
            }
            else
                Gdx.app.log("Error", "Unknown message: " + messageType);
                */
        //} catch(ClassNotFoundException cnfe) {
        //}
    }

    public String GetName() {
        return name;
    }
}
