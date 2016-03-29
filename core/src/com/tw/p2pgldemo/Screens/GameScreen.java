package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.Entities.Tile;
import com.tw.p2pgldemo.Entities.World;
import com.tw.p2pgldemo.Game;
import com.tw.p2pgldemo.Entities.Player;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.Networking.Connection;
import com.tw.p2pgldemo.Networking.PlayerState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class GameScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    //TileLayer tileLayer;
    private Player player;
    private List<Player> players;
    private World world;
    private Menu menu;
    private String worldName = "a5";
    private long lastStateTime;

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

        menu = new Menu();
        world = new World(worldName);
        //Load player.
        player = CreatePlayer(500, 500, playerName, playerTextureName);
        players = new ArrayList<Player>();
        //Save player state to DHT
        Connection.GetInstance().JoinWorld(worldName);
        //Connection.GetInstance().SaveState(player.GetState());
        //Connection.GetInstance().ConnectLocalPlayers();
        lastStateTime = System.currentTimeMillis();
        /*
        List<PlayerState> states = Connection.GetInstance().GetPlayerStates();
        players = new ArrayList<Player>();
        for(PlayerState state: states) {
            players.add(new Player(new Rectangle(state.getPos().x, state.getPos().y, 64, 96),
                    (Texture)game.assetManager.characterTextures.get("pirate"),
                    this, "insert_name_here"));
        }
        */
    }

    private Player CreatePlayer(float x, float y, String playerName, String playerTextureName) {
        return new Player(new Rectangle(x, y, 64, 96),
                (Texture)AssetManager.GetInstance().characterTextures.get(playerTextureName),
                    this, playerName, playerTextureName);
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

        if(!players.isEmpty()) {
            for (int i = 0; i < players.size(); i++) {
                players.get(i).render(game.batch);
            }
        }
        game.batch.begin();
        game.batch.end();

        ProcessInput();
        if(System.currentTimeMillis() - lastStateTime > 400) {
            UpdatePlayerStates();
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

    private void UpdatePlayerStates() {
        List<PlayerState> playerStates = Connection.GetInstance().GetStatesFromUDP();
        Iterator playersIter = players.iterator();
        Iterator stateIter = playerStates.iterator();

        //Iterate through players
        while(playersIter.hasNext()) {
            boolean found = false;
            Player player = (Player) playersIter.next();

            //Iterate through player states
            while(stateIter.hasNext()) {
                PlayerState state = (PlayerState) stateIter.next();
                //If a state for player is found, update the player's state.
                if(state.getName().equals(player.GetName())) {
                    player.SetState(state);
                    stateIter.remove();
                    found = true;
                }
            }
            //If no new state is found for the player, update its timeout counter
            if(found == false) {
                player.playerTimeoutTick++;
                //If timeout counter is over 3, remove player
                if(player.playerTimeoutTick > 2)
                    playersIter.remove();
            }
        }
        //Loop through the remaining states and create new players for each.
        for(PlayerState state : playerStates) {
            Vector3 playerPos = state.getPos();
            players.add(CreatePlayer(playerPos.x, playerPos.y,
                    state.getName(), state.getTextureName()));
        }

        /*
        for(int i = 0; i < playerStates.size(); i++) {
            boolean found = false;
            for(Player playerX : players) {
                if(playerX.GetName().equals(playerStates.get(i).getName())) {
                    playerX.SetState(playerStates.get(i));
                    found = true;
                }
            }
            if(!found) {
                Vector3 playerPos = playerStates.get(i).getPos();
                players.add(CreatePlayer(playerPos.x, playerPos.y,
                        playerStates.get(i).getName(), playerStates.get(i).getTextureName()));
            }
        }
        */
        //Send this player's state
        Connection.GetInstance().SendState(player.GetState());
    }

    public void LoadWorld(String worldName) {
        this.worldName = worldName;
        //Connection.GetInstance().SaveState(player.GetState());
        Connection.GetInstance().JoinWorld(worldName);
        world = new World(worldName);
    }

    public void Teleport(String worldName) {
        this.worldName = worldName;
        LoadWorld(worldName);
        players.clear();
        //Connection.GetInstance().SaveState(player.GetState());
        //Connection.GetInstance().ConnectLocalPlayers();
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
