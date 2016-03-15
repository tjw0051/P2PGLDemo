package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tw.p2pgldemo.Entities.Tile;
import com.tw.p2pgldemo.Entities.World;
import com.tw.p2pgldemo.Game;
import com.tw.p2pgldemo.Entities.Player;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class GameScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    //TileLayer tileLayer;
    private Player player;
    private World world;

    public GameScreen(Game game) {
        this.game = game;
        game.assetManager.LoadWorldAssets();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        game.assetManager.LoadInteractions();
        game.assetManager.LoadLevels();
        game.assetManager.LoadTextures();
        game.assetManager.LoadCharacters();
        //game.assetManager.LoadLevel("a1");
        //world  = new World("a1");
        LoadWorld("a1");
        /*
        tileLayer = new TileLayer(new Rectangle(0, 0, 100, 122), //50, 82, 75
                new Rectangle(0, 0, 100, 170),
                new Vector2(0, 0),
                new Vector2(10, 10), AssetManager.LoadLevel("a1"), 0.7f);
        tileLayer.SetPos(500, 100);
        */
        player = new Player(new Rectangle(500, 500, 64, 96),
                (Texture)game.assetManager.characterTextures.get("pirate"),
                this);
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
        game.batch.begin();
        game.batch.end();

        processInput();
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

    public void LoadWorld(String worldName) {
        world = new World(worldName);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
