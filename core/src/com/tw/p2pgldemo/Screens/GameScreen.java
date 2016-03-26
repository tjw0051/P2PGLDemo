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
import com.tw.p2pgldemo.Networking.Connection;
import com.tw.p2pgldemo.Networking.PlayerState;

import java.util.ArrayList;
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
    private String worldName = "a1";
    private long lastStateTime;

    public GameScreen(Game game) {
        this.game = game;


        //Set up camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Load Assets
        game.assetManager.LoadWorldAssets();
        game.assetManager.LoadLevels();
        game.assetManager.LoadTextures();
        game.assetManager.LoadCharacters();
        LoadWorld(worldName);
        //Load player.
        player = CreatePlayer(500, 500, Connection.GetInstance().GetName());
        players = new ArrayList<Player>();
        //Save player state to DHT
        Connection.GetInstance().SaveState(worldName, player.GetPos(), new Vector3(1,2,3));
        Connection.GetInstance().ConnectLocalPlayers();
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

    private Player CreatePlayer(float x, float y, String playerName) {
        return new Player(new Rectangle(x, y, 64, 96),
                    (Texture)game.assetManager.characterTextures.get("pirate"),
                    this, playerName);
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

        processInput();
        if(System.currentTimeMillis() - lastStateTime > 1000) {
            UpdatePlayerStates();
            lastStateTime = System.currentTimeMillis();
            //Connection.GetInstance().SendState(worldName, player.GetPos(), player.GetDestination());
        }


    }

    private void processInput() {
        if(Gdx.input.isTouched()) {
            //int tileGroundCollision = tileLayer.TileCollisionCheck(new Rectangle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1));
            int tileGroundCollision = world.TileCollisionCheck(
                    new Rectangle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1),
                    player.GetLayer() - 1);
            if(tileGroundCollision != -1) {
                Vector2 tileCenterPos = world.GetTilePos(tileGroundCollision);

                int tileCollision = world.TileCollisionCheck(
                        new Rectangle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1),
                        player.GetLayer());
                if(tileCollision != -1) {
                    Tile tile = world.GetTile(player.GetLayer(), tileCollision);
                    if(tile.isInteractive()) {
                        player.Go(tileCenterPos.x, tileCenterPos.y, tile.GetInteraction());
                    }
                }
                else
                    player.Go(tileCenterPos.x, tileCenterPos.y);
            }
        }
    }

    private void UpdatePlayerStates() {
        List<PlayerState> playerStates = Connection.GetInstance().GetStatesFromUDP();
        for(int i = 0; i < playerStates.size(); i++) {
            boolean found = false;
            for(Player playerX : players) {
                if(playerX.GetName().equals(playerStates.get(i).getName())) {
                    playerX.Update(playerStates.get(i));
                    found = true;
                }
            }
            if(!found) {
                Vector3 playerPos = playerStates.get(i).getPos();
                players.add(CreatePlayer(playerPos.x, playerPos.y, playerStates.get(i).getName()));
            }
        }
        Connection.GetInstance().SendState(player.GetState());
    }

    public void LoadWorld(String worldName) { world = new World(worldName); }

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
