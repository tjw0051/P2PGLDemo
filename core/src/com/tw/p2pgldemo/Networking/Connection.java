package com.tw.p2pgldemo.Networking;

import P2PGL.Connection.IHybridConnection;
import P2PGL.P2PGL;
import P2PGL.Profile.*;
import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.tw.p2pgldemo.IO.LevelData;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by t_j_w on 15/03/2016.
 */
public class Connection implements MessageReceivedListener{
    private static final Connection connection = new Connection();
    public static Connection GetInstance() { return connection; }

    private IHybridConnection conn;
    private Json json;
    private String name;
    private MyProfile profile;
    private PlayerState playerState;
    private List<PlayerState> localPlayerStates;
    private java.util.Map<IKey, PlayerState> localStates;
    private List<StarCollectedMsg> starMessages;

    private Connection() {
        json = new Json();
        localPlayerStates = new ArrayList<PlayerState>();
        localStates = new HashMap<IKey, PlayerState>();
        starMessages = new ArrayList<StarCollectedMsg>();
    }

    /** Create profile with random name from 0-100. Connect to server.
     *  Listen on UDP channel (Connection() channel + 1)
     * @param name  name of player.
     */
    public int Connect(String name, String worldName, String textureName) {
        int err = 0;
        this.name = name;
        err = ConnectDHT(worldName, textureName);
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
            conn.JoinLocalChannel(worldName);
            localPlayerStates.clear();
            localStates.clear();
            return GetWorldData(worldName);
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

    public LevelData GetWorldData(String levelName) {
        try {
            LevelData levelData = conn.Get(new Key(levelName), LevelData.class);
            return levelData;
        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        }
        return null;
    }

    public IKey GetKey() {
        return conn.GetKey();
    }

    public IProfile GetProfile(IKey key) {
        try {
            return conn.GetProfile(key);
        } catch (IOException ioe) {
            return null;
        }
    }

    private int ConnectDHT(String worldName, String textureName) {
        int port = new RandomXS128().nextInt(100);
        profile = new MyProfile(InetAddress.getLoopbackAddress(), 4000 + port, 4001 + port, worldName, name, new Key());
        profile.SetTextureName(textureName);
        //profile.SetLocalChannel(worldName);

        //conn = KademliaConnectionFactory.Get(profile); //new P2PGL.Connection(profile, new KademliaFacade());
        conn = P2PGL.GetInstance().GetConnection(profile);
        try {
            conn.Connect("server", InetAddress.getLoopbackAddress(), 4000);
            return 0;
        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to connect to server");
            //ConnectDHT(worldName, textureName);
            return -1;
        }
    }

    public int SendState(PlayerState playerState) {
        this.playerState = playerState;
        playerState.setKey(profile.GetKey());
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
            return conn.Get(new Key(playerName + "STATE"), PlayerState.class);

        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to retrieve state of player: " + playerName);
        } catch (ClassNotFoundException cnfe) {

        }
        return null;
    }

    public List<PlayerState> GetStatesFromUDP() {
        return new ArrayList<PlayerState>(localStates.values());
    }

    public List<StarCollectedMsg> GetStarMessages() { return starMessages; }

    public void MessageReceivedListener(Object obj, Class messageType, IKey key) {
            if(messageType.equals(PlayerState.class)) {
                System.out.println("PlayerState: " + key.toString());
                //localPlayerStates.add((PlayerState)obj);
                PlayerState state = (PlayerState)obj;
                localStates.put(state.getKey(), state);
                System.out.println("players: " + localStates.size());
            }
            if(messageType.equals(StarCollectedMsg.class)) {
                System.out.println("Star received");
                starMessages.add((StarCollectedMsg)obj);
            }
    }

    public String GetName() {
        return name;
    }
}
