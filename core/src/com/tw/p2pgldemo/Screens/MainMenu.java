package com.tw.p2pgldemo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.tw.p2pgldemo.Entities.UI.Button;
import com.tw.p2pgldemo.Game;
import com.tw.p2pgldemo.IO.AssetManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by t_j_w on 08/03/2016.
 */
public class MainMenu implements Screen, Input.TextInputListener{

    Button exitButton, connectButton, inputButton;
    Button arrowLeftButton, arrowRightButton, characterButton;
    BitmapFont font;
    MenuState menuState;
    Game game;
    String playerName;

    int currentCharacterTextureIter;
    List<Texture> charTextures;
    List<String> charTextureNames;

    private enum MenuState {
        MAINMENU,
        CHARACTERMENU,
        LOADING
    }

    public MainMenu(Game game) {
        this.game = game;
        game.assetManager.LoadUI();
        game.assetManager.LoadCharacters();

        connectButton = new Button("connectbutton", 0, 0, 1.0f);
        exitButton = new Button("exitbutton", 0, 0, 1.0f);
        font = new BitmapFont();
        SetupMainMenu();

        arrowLeftButton = new Button("arrow", 100, 350, 1.0f);
        arrowLeftButton.Flip(true, false);
        arrowRightButton = new Button("arrow", 375, 350, 1.0f);

        characterButton = new Button(
                new TextureRegion((Texture)AssetManager.GetInstance().characterTextures.get("pirate"), 0, 0, 32, 48),
                175, 290, 5f);
        inputButton = new Button("button", 575, 410, 1.0f);
    }
    @Override
    public void show() { }

    /** Renders the menu. A switch statement manages rendering for different menu states.
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (menuState) {
            case MAINMENU: {
                connectButton.render(game.batch);
                exitButton.render(game.batch);
                break;
            }
            case LOADING: {
                //TODO: render loading icon
                break;
            }
            case CHARACTERMENU: {
                connectButton.render(game.batch);
                exitButton.render(game.batch);
                arrowLeftButton.render(game.batch);
                arrowRightButton.render(game.batch);
                characterButton.render(game.batch);
                inputButton.render(game.batch);
                game.batch.begin();
                font.draw(game.batch, "Enter player name:", 580, 530);
                font.draw(game.batch, playerName, 590, 470);
                game.batch.end();

                break;
            }
        }
        ProcessInput();
    }

    /**
     * Processes input from the user. A switch statement manages input for different
     * menu states.
     */
    private void ProcessInput() {
        if(Gdx.input.justTouched()) {
            Rectangle cursorRect = new Rectangle(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1);

            switch (menuState) {
                case MAINMENU: {
                    if(cursorRect.overlaps(exitButton.GetRect())) {
                        Gdx.app.exit();
                    }
                    if(cursorRect.overlaps(connectButton.GetRect())) {
                        SetupCharacterMenu();
                    }
                    break;
                }
                case CHARACTERMENU: {
                    if(cursorRect.overlaps(arrowLeftButton.GetRect())) {
                        CycleCharacters(-1);
                    }
                    if(cursorRect.overlaps(arrowRightButton.GetRect())) {
                        CycleCharacters(1);
                    }
                    if(cursorRect.overlaps(inputButton.GetRect())) {
                        Gdx.input.getTextInput(this, "Enter Name:", "", "");
                    }

                    if(cursorRect.overlaps(exitButton.GetRect())) {
                        SetupMainMenu();
                    }
                    if(cursorRect.overlaps(connectButton.GetRect())) {
                        Connect();
                    }
                    break;
                }
            }
        }

    }

    private void CycleCharacters(int direction) {
        int iter = currentCharacterTextureIter + direction;
        if(iter < 0)
            currentCharacterTextureIter = charTextures.size() -1;
        else if(iter >= charTextures.size())
            currentCharacterTextureIter = 0;
        else
            currentCharacterTextureIter = iter;

        TextureRegion region = new TextureRegion(charTextures.get(currentCharacterTextureIter), 0, 0, 32, 48);
        characterButton.SetTexture(region);
    }

    private void Connect() {
        if(!playerName.equals("")) {
            //int playerId = new RandomXS128().nextInt(100);
            com.tw.p2pgldemo.Networking.Connection.GetInstance().Connect(playerName, "a5");//Integer.toString(playerId));
            game.setScreen(new GameScreen(game, playerName, charTextureNames.get(currentCharacterTextureIter)));
            dispose();
        }
    }

    private void SetupMainMenu() {
        connectButton.SetCenterPos(Gdx.graphics.getWidth()/2, 500);
        exitButton.SetCenterPos(Gdx.graphics.getWidth()/2, 375);
        menuState = MenuState.MAINMENU;
    }

    private void SetupCharacterMenu() {
        currentCharacterTextureIter = 0;
        playerName = "Tom";
        charTextures = new ArrayList<Texture>(AssetManager.GetInstance().characterTextures.values());
        charTextureNames = new ArrayList<String>(AssetManager.GetInstance().characterTextures.keySet());
        exitButton.SetPos(25, 25);
        connectButton.SetPos(Gdx.graphics.getWidth() - 25 - connectButton.GetRect().getWidth(), 25);
        menuState = MenuState.CHARACTERMENU;
    }

    @Override
    public void resize(int width, int height) { }

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

    @Override
    public void input(String text) {
        playerName = text;
    }

    @Override
    public void canceled() {

    }
}
