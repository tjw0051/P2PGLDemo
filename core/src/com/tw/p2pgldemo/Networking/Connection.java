package com.tw.p2pgldemo.Networking;

import P2PGL.Connection.IHybridConnection;
import P2PGL.P2PGL;
import P2PGL.Profile.*;
import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.tw.p2pgldemo.IO.LevelData;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public int Connect(String name, String worldName, String textureName, InetAddress address, int serverPort, int thisPort) {
        int err = 0;
        this.name = name;
        err = ConnectDHT(worldName, textureName, address, serverPort, thisPort);
        conn.AddMessageListener(this);

        return err;
    }

    public int Disconnect() {
        try {
            conn.Disconnect();
        } catch(IOException ioe) {
            return -1;
        }
        return 0;
    }

    public LevelData JoinWorld(String worldName) {
        try {
            conn.Broadcast("exit", String.class);
            conn.JoinLocalChannel(worldName);
            localPlayerStates.clear();
            localStates.clear();
            return GetWorldData(worldName);
        } catch (IOException ioe) {
            System.out.println("Error changing UDP Channel");
            ioe.printStackTrace();
        }
        return null;
    }

    public void SetWorld(LevelData levelData) {
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

    private int ConnectDHT(String worldName, String textureName, InetAddress address, int serverPort, int thisPort) {

        profile = new MyProfile(InetAddress.getLoopbackAddress(), thisPort, thisPort +1, worldName, name, new Key());
        profile.SetTextureName(textureName);

        //Inject custom factory with new Kademlia config
        P2PGL.GetInstance().SetFactory(new CustomP2PGLFactory());
        conn = P2PGL.GetInstance().GetConnection(profile);

        try {
            conn.Connect("server", address, serverPort);
            return 0;
        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to connect to server");
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
        List<PlayerState> currStates = new ArrayList<PlayerState>(localStates.values());
        localStates.clear();
        return currStates;
    }

    public List<StarCollectedMsg> GetStarMessages() { return starMessages; }

    public void MessageReceivedListener(Object obj, Class messageType, IKey key) {
            if(messageType.equals(String.class)) {
                String mesg = (String) obj;
                if(mesg == "exit") {
                    conn.GetLocalChannel().Remove(key);
                }
            }

            if(messageType.equals(PlayerState.class)) {
                System.out.println("PlayerState: " + key.toString());
                PlayerState state = (PlayerState)obj;
                localStates.put(state.getKey(), state);
            }
            if(messageType.equals(StarCollectedMsg.class)) {
                System.out.println("Star received");
                starMessages.add((StarCollectedMsg)obj);
            }
    }

    public void RemovePlayer(IKey key) {
        conn.GetLocalChannel().Remove(key);
    }

    public String GetName() {
        return name;
    }
}
