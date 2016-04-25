package com.tw.p2pgldemo.Screens;

import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.Entities.Filters.PlayerKeyFilter;
import com.tw.p2pgldemo.Entities.Tile;
import com.tw.p2pgldemo.Entities.World;
import com.tw.p2pgldemo.Game;
import com.tw.p2pgldemo.Entities.Player;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.IO.LevelData;
import com.tw.p2pgldemo.Networking.Connection;
import com.tw.p2pgldemo.Networking.MyProfile;
import com.tw.p2pgldemo.Networking.PlayerState;
import com.tw.p2pgldemo.Networking.StarCollectedMsg;
import kademlia.exceptions.ContentNotFoundException;

import java.util.*;
import java.util.Map;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class GameScreen implements Screen {
    private final long timeout = 1000L;

    private Game game;
    private OrthographicCamera camera;
    //TileLayer tileLayer;
    private Player player;
    private List<Player> players;
    private World world;
    private Menu menu;
    private String worldName = "a5";
    private long lastStateTime;
    private List<IKey> playerKeyCache;
    private IKey playerKey;

    public GameScreen(Game game, String playerName, String playerTextureName) {
        this.game = game;

        //Set up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Load Assets
        game.assetManager.LoadUI();
        game.assetManager.LoadWorldAssets();
        game.assetManager.LoadLevels();
        game.assetManager.LoadTextures();
        game.assetManager.LoadCharacters();
        this.playerKeyCache = new ArrayList<IKey>();
        playerKey = Connection.GetInstance().GetKey();

        menu = new Menu();
        LoadWorld(worldName);
        //Load player.
        player = CreatePlayer(500, 500, playerName, playerKey, playerTextureName);
        players = new ArrayList<Player>();
        //Save player state to DHT
        //Connection.GetInstance().JoinWorld(worldName);
        lastStateTime = System.currentTimeMillis();
    }

    private Player CreatePlayer(float x, float y, String playerName, IKey key, String playerTextureName) {
        return new Player(new Rectangle(x, y, 64, 96),
                (Texture)AssetManager.GetInstance().characterTextures.get(playerTextureName),
                    this, playerName, key, playerTextureName);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.604f, 0.914f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        world.render(game.batch);
        //tileLayer.render(game.batch);
        player.render(game.batch);

        //  Update Players - remove timed-out players
        long time = System.currentTimeMillis();
        Iterator playerIter = players.iterator();
        while(playerIter.hasNext()) {
            Player player = (Player)playerIter.next();
            //System.out.println(player.GetName() + " timeout: " + (time - player.lastUpdated));
            if(time - player.lastUpdated > timeout) {
                Connection.GetInstance().RemovePlayer(player.GetKey());
                playerIter.remove();
            }

            else
                player.render(game.batch);
        }

        game.batch.begin();
        game.batch.end();

        ProcessInput();
        if(System.currentTimeMillis() - lastStateTime > 400) {
            UpdatePlayerStates();
            UpdateStars();
            lastStateTime = System.currentTimeMillis();
            //Connection.GetInstance().SendState(worldName, player.GetPos(), player.GetDestination());
        }
        menu.render(game.batch);
    }

    private void ProcessInput() {
        Rectangle cursorRect = new Rectangle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1);

        if(Gdx.input.justTouched()) {
            //Player clicked menu component
            if(menu.ProcessInput(cursorRect)) {}
            //Player clicked world component
            else {
                //Check the player has clicked a tile block
                int tileGroundCollision = world.TileCollisionCheck(cursorRect, player.GetLayer() - 1);
                //If player has clicked a tile...
                if (tileGroundCollision != -1) {
                    Vector2 tileCenterPos = world.GetTilePos(tileGroundCollision);
                    int tileCollision = world.TileCollisionCheck(cursorRect, player.GetLayer());
                    //Player clicked an object on layer 2
                    if (tileCollision != -1) {}
                    else {
                        Tile tile = world.GetTile(player.GetLayer() - 1, tileGroundCollision);//tileCollision);
                        if (tile.isInteractive())
                            player.Go(tileCenterPos.x, tileCenterPos.y, tile.GetInteraction());
                            //}
                        else
                            player.Go(tileCenterPos.x, tileCenterPos.y, null);
                    }
                }
            }
        }
    }

    private void UpdateStars() {
        List<StarCollectedMsg> starMessages = Connection.GetInstance().GetStarMessages();
        if(starMessages == null)
            return;
        Iterator iter = starMessages.iterator();
        while(iter.hasNext()) {
            StarCollectedMsg starMessage = (StarCollectedMsg)iter.next();
            starMessage.GetPos().z = starMessage.GetPos().z + 1;
            getWorld().RemoveTileAt(starMessage.GetPos());
            iter.remove();
        }
        Connection.GetInstance().GetStarMessages().clear();
    }

    private void UpdatePlayerStates() {
        List<PlayerState> states = Connection.GetInstance().GetStatesFromUDP();
        Iterator playerStateIter = states.iterator();
        while (playerStateIter.hasNext()) {
            PlayerState state = (PlayerState)playerStateIter.next();
            int playerIndex = PlayerKeyFilter.PlayerWithKey(players, state.getKey());
            if(playerIndex != -1) {
                System.out.println("Updating player: " + state.getName() +
                        " world: " + state.getWorld() + " pos: " + state.getPos().x
                        + ", " + state.getPos().y);
                players.get(playerIndex).SetState(state);
                System.out.println("statecount: " + states.size());
                playerStateIter.remove();
                System.out.println("statecount: " + states.size());
            }
            else {
                MyProfile prof = (MyProfile) Connection.GetInstance().GetProfile(state.getKey());
                if(prof != null && prof.GetLocalChannelName().equals(worldName)) {
                    System.out.println("Adding player: " + state.getName());
                    players.add(CreatePlayer(state.getPos().x, state.getPos().y,
                            state.getName(), state.getKey(), prof.GetTextureName()));
                }
            }
        }

        //Send this player's state
        Connection.GetInstance().SendState(player.GetState());
    }

    public void LoadWorld(String worldName) {
        this.worldName = worldName;
        //Connection.GetInstance().SaveState(player.GetState());
        LevelData netData = Connection.GetInstance().JoinWorld(worldName);
        if(netData != null)
            world = new World(netData);
        else
            world = new World(worldName);
    }

    public void Teleport(String worldName) {
        this.worldName = worldName;
        Connection.GetInstance().SetWorld(world.GetLevelData());
        LoadWorld(worldName);
        players.clear();
        playerKeyCache.clear();
    }

    public void ProcessInteraction(Interaction interaction) {
        if(interaction.getName().equals("pickup")) {
            if(interaction.getParams()[0].equals("star")) {
                System.out.println("Player hit star");
                menu.IncrementScore();
                getWorld().RemoveTileAt(new Vector3(interaction.getPos().x,
                        interaction.getPos().y, interaction.getPos().z+1));
                Connection.GetInstance().StarCollected(interaction.getPos());
                //Connection.GetInstance().SetWorld(world.GetLevelData());
            }
        }
    }

    public World getWorld() { return world; }

    public String GetWorldName() { return worldName; }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
