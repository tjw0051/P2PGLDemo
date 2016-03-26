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
    private UDPChannel udpChannel;
    private Json json;
    private String name;
    private P2PGL.Profile.Profile profile;
    private PlayerState playerState;
    private List<PlayerState> localPlayerStates;

    private Connection() {
        json = new Json();
        localPlayerStates = new ArrayList<PlayerState>();
    }

    /** Create profile with random name from 0-100. Connect to server.
     *  Listen on UDP channel (Connection() channel + 1)
     * @param name  name of player.
     */
    public int Connect(String name) {
        int err = 0;
        this.name = name;
        err = ConnectDHT();
        ConnectUDP();

        return err;
    }

    public int Disconnect() {
        try {
            conn.Disconnect();
            udpChannel.Stop();
        } catch(IOException ioe) {
            return -1;
        }
        return 0;
    }

    private int ConnectDHT() {
        int port = new RandomXS128().nextInt(100);
        //profile = new Profile(InetAddress.getLoopbackAddress(), 4000 + port, name);
        profile = new Profile(InetAddress.getLoopbackAddress(), 4000 + port, name);
        conn = new P2PGL.Connection(profile, new KademliaFacade());
        try {
            conn.Connect("server", InetAddress.getLoopbackAddress(), 4000);
            conn.StoreProfile();
            return 0;
        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to connect to server");
            return -1;
        }
    }

    private void ConnectUDP() {
        udpChannel = new UDPChannel(profile, profile.GetUDPPort());
        udpChannel.addListener(this);
        udpChannel.Listen();
    }

    /** Save the players state at [name] + "STATE"
     * @param world
     * @param pos
     * @param destination
     */
    public int SaveState(String world, Vector3 pos, Vector3 destination) {
        playerState = new PlayerState(name, world, pos, destination);
        try {
            conn.Store(new Key(name + "STATE"), json.toJson(playerState));
        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to store player profile");
            return -1;
        }
        return 0;
    }

    public int SendState(String world, Vector3 pos, Vector3 destination) {
        return SendState(new PlayerState(name, world, pos, destination));
    }

    public int SendState(PlayerState playerState) {
        this.playerState = playerState;
        try {
            udpChannel.Broadcast(playerState, PlayerState.class);
        } catch(IOException ioe) {
            DiagnoseIOE(ioe);
        }
        return 0;
    }

    /** Retrieves the state of a player from the player name.
     * @param playerName
     * @return returns null if the playerState cannot be retrieved.
     */
    public PlayerState GetState(String playerName) {
        try {
            String jsonState = conn.Get(new Key(playerName + "STATE"));
            return json.fromJson(PlayerState.class, jsonState);

        } catch (IOException ioe) {
            Gdx.app.log("Error", "Unable to retrieve state of player: " + playerName);
        }
        return null;
    }

    public void ConnectLocalPlayers() {
        //Get all player profiles from DHT.

        try {
            IKey[] users = conn.ListUsers();
            System.out.println("Users found: " + users.length);
            if(users == null)
                return;
            List<IProfile> profiles = new ArrayList<IProfile>();
            profiles = conn.GetProfiles(users);
            //System.out.println("Profiles found: " + profiles.size());
            if(profiles == null || profiles.isEmpty())
                return;
            List<PlayerState> allStates = GetStatesFromDHT(profiles);
            //System.out.println("States found: " + allStates.size());
            if(allStates.isEmpty())
                return;
            //If other player is in the same world as this player, add to UDP channel.
            for(int i = 0; i < allStates.size(); i++) {
                //Find Server Profile
                if(allStates.get(i).getWorld().equals(playerState.getWorld())) {
                    for(IProfile prof : profiles) {
                        if(prof.GetName().equals(allStates.get(i).getName()))
                            udpChannel.Add(prof);
                    }
                }
            }
        } catch (IOException ioe) {
            DiagnoseIOE(ioe);
        }
    }

    private void DiagnoseIOE(Exception e) {
        System.out.println("IO Exception: ");
        e.printStackTrace();
    }

    public List<PlayerState> GetStatesFromUDP() {
        return localPlayerStates;
    }

    public List<PlayerState> GetStatesFromDHT(List<IProfile> profiles) {
        List<PlayerState> states = new ArrayList<PlayerState>();
        for(int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).GetName().equals("server"))
                Gdx.app.log("Info", "Found server profile");
                //Find This player profile
            else if (profiles.get(i).GetName().equals(name)) {
                //Don't get state of THIS player.
            //Find other player profile
            } else if (!profiles.get(i).GetName().equals("server")) {
                Gdx.app.log("Info", "player found:" + profiles.get(i).GetName());
                PlayerState state = GetState(profiles.get(i).GetName());
                if(state != null) {
                    states.add(state);
                }
            }
        }
        return states;
    }

    public void MessageReceived(String messageType, IKey key) {
        try {
            if (messageType.equals("com.tw.p2pgldemo.Networking.PlayerState")) {
                if(!udpChannel.Contains(key)) {
                    try {
                        System.out.println("adding new profile");

                        udpChannel.Add(conn.GetProfile(key));
                    } catch (IOException ioe) {
                        System.out.println("Cannot get profile for unknown UDP client");
                    }
                }
                localPlayerStates.add((PlayerState) udpChannel.ReadNext());
            }
        } catch(ClassNotFoundException cnfe) {
        }
    }

    public String GetName() {
        return name;
    }
}
