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
import com.tw.p2pgldemo.Entities.UI.Button;
import com.tw.p2pgldemo.Game;
import com.tw.p2pgldemo.IO.AssetManager;
import com.tw.p2pgldemo.IO.Interaction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainMenu implements Screen, Input.TextInputListener{

    Button exitButton, connectButton;
    Button arrowLeftButton, arrowRightButton, characterButton, inputButton,
            ipButton, serverPortButton, thisPortButton;
    BitmapFont font;
    MenuState menuState;
    Game game;
    String playerName;

    int currentCharacterTextureIter;
    List<Texture> charTextures;
    List<String> charTextureNames;
    int thisPort, serverPort;
    InetAddress ipAddress = InetAddress.getLoopbackAddress();
    String currentInputBox;

    private enum MenuState {
        MAINMENU,
        CHARACTERMENU,
        LOADING
    }

    public MainMenu(Game game) {
        this.game = game;
        game.assetManager.LoadUI();
        game.assetManager.LoadCharacters();

        thisPort = new RandomXS128().nextInt(100) + 2000;
        serverPort = 4000;

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
        ipButton = new Button("glowbox", 120, 714, 200, 40);
        serverPortButton = new Button("glowbox", 460, 714, 150, 40);
        thisPortButton = new Button("glowbox", 750, 714, 150, 40);
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
                ipButton.render(game.batch);
                serverPortButton.render(game.batch);
                thisPortButton.render(game.batch);
                game.batch.begin();
                font.draw(game.batch, "Enter player name:", 580, 530);
                font.draw(game.batch, playerName, 590, 470);
                font.draw(game.batch, "IP: \t" + ipAddress.getHostAddress(), 112, 740);
                font.draw(game.batch, "Server Port: \t" + Integer.toString(serverPort), 386, 740);
                font.draw(game.batch, "Port: \t" + Integer.toString(thisPort), 722, 740);
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
            Rectangle cursorRect = new Rectangle(Gdx.input.getX(),
                    Gdx.graphics.getHeight() - Gdx.input.getY(), 1, 1);

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
                        currentInputBox = "name";
                        Gdx.input.getTextInput(this, "Enter Name:", "", "");
                    }

                    if(cursorRect.overlaps(ipButton.GetRect())) {
                        currentInputBox = "ip";
                        Gdx.input.getTextInput(this, "Enter IP Address:", "", "");
                    }

                    if(cursorRect.overlaps(serverPortButton.GetRect())) {
                        currentInputBox = "serverPort";
                        Gdx.input.getTextInput(this, "Enter Port Address:", "", "");
                    }

                    if(cursorRect.overlaps(thisPortButton.GetRect())) {
                        currentInputBox = "thisPort";
                        Gdx.input.getTextInput(this, "Enter Port Address:", "", "");
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
            if(com.tw.p2pgldemo.Networking.Connection.GetInstance()
                    .Connect(playerName, "a5",
                            charTextureNames.get(currentCharacterTextureIter), ipAddress,
                            serverPort, thisPort) == 0) {//Integer.toString(playerId));
                game.setScreen(new GameScreen(game, playerName, charTextureNames.get(currentCharacterTextureIter)));
                dispose();
            }
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
        if(currentInputBox.equals("name"))
            playerName = text;
        if(currentInputBox.equals("ip")) {
            try {
                InetAddress newip = InetAddress.getByName("text");
                ipAddress = newip;
            } catch (UnknownHostException uhs) {
                System.out.println("Unknown host");
            }
        }
        if(currentInputBox.equals("serverPort")) {
            int newport = Integer.parseInt(text);
            if(newport > 0 && newport < 65536)
                serverPort = newport;
        }
        if(currentInputBox.equals("thisPort")) {
            int newport = Integer.parseInt(text);
            if(newport > 0 && newport < 65536)
                thisPort = newport;
        }
    }

    @Override
    public void canceled() {

    }
}
