package com.tw.p2pgldemo.Entities;

import P2PGL.Util.IKey;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.Screens.GameScreen;

/**
 * 2D Animation with libGDX: https://github.com/libgdx/libgdx/wiki/2D-Animation
 */
public class Player {

    private static final float walkingSpeed = 100f;
    private int layer = 1;

    private String name, textureName;
    private IKey key;
    private GameScreen gameScreen;
    private Rectangle rectangle;
    private BitmapFont font;
    private TextureRegion currentFrame;
    private Vector2 pos, destination;
    private MovementState playerState;
    private Interaction destinationInteraction;
    private PlayerAnimation playerAnimation;
    private Vector2 normDirection;
    private float distance;

    public long lastUpdated;
    public int playerTimeoutTick = 0;

    public enum MovementState {
        STANDING,
        WALKING
    }

    public Player(Rectangle rect, Texture tex, GameScreen gameScreen,
                  String name, IKey key, String textureName) {
        this.gameScreen = gameScreen;
        this.rectangle = rect;
        this.textureName = textureName;
        this.name = name;
        this.key = key;
        playerAnimation = new PlayerAnimation(tex);
        playerAnimation.SetupAnimations();
        playerState = MovementState.STANDING;
        pos = new Vector2(rect.x, rect.y);
        destination = new Vector2(pos.x, pos.y);
        font = new BitmapFont();
        lastUpdated = System.currentTimeMillis();
    }

    public void render(SpriteBatch spriteBatch) {
        switch (playerState) {
            case STANDING: {
                break;
            }
            case WALKING: {
                Vector2 direction = new Vector2(destination.x - pos.x, destination.y - pos.y);
                normDirection = direction.nor();
                pos.add((normDirection.scl(walkingSpeed).scl(Gdx.graphics.getDeltaTime())));
                if(Vector2.dst(pos.x, pos.y, destination.x, destination.y) <= 1) {
                    playerState = MovementState.STANDING.STANDING;
                    pos.x = destination.x;
                    pos.y = destination.y;
                    if(destinationInteraction != null) {
                        Interact();
                        destinationInteraction = null;
                    }
                }
                break;
            }
        }
        currentFrame = playerAnimation.Animate(playerState, pos, destination);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, pos.x, pos.y, rectangle.getWidth(), rectangle.getHeight());
        font.draw(spriteBatch, name, pos.x + rectangle.getWidth()/2, pos.y + rectangle.getHeight() + 20);
        spriteBatch.end();
    }

    public void SetState(com.tw.p2pgldemo.Networking.PlayerState playerState) {
        this.pos.x = playerState.getPos().x;
        this.pos.y = playerState.getPos().y;
        this.destination.x = playerState.getDestination().x;
        this.destination.y = playerState.getDestination().y;
        if(!pos.equals(destination)) {
            this.playerState = MovementState.WALKING;
        }
        this.playerTimeoutTick = 0;
        lastUpdated = System.currentTimeMillis();
    }

    private void Interact() {
        if(destinationInteraction.getName().toString().equals("teleport")) {
            gameScreen.Teleport((destinationInteraction.getParams())[0]);
            int spawnPosX = Integer.parseInt((destinationInteraction.getParams())[1]);
            int spawnPosY = Integer.parseInt((destinationInteraction.getParams())[2]);
            Vector2 newPos = gameScreen.getWorld().GetTilePos(spawnPosX, spawnPosY);
            if(newPos != null)
                pos = newPos;
        }
        if(destinationInteraction.getName().toString().equals("pickup")) {
            gameScreen.ProcessInteraction(destinationInteraction);
        }
    }

    /** Direct player to walk to position x,y and perform interaction
     *  at destination.
     * @param x destination
     * @param y destination
     * @param interaction   Interaction to perform at destination.
     */
    public void Go(float x, float y, Interaction interaction) {
        destination.x = x - rectangle.width /2;
        destination.y = y;
        distance  = pos.dst(destination);
        playerState = MovementState.WALKING;
        if(interaction != null)
            destinationInteraction = interaction;
    }
    public int GetLayer() { return layer; }

    public Vector3 GetPos() {
        return new Vector3(pos.x, pos.y, layer);
    }

    public Vector3 GetDestination() { return new Vector3(destination.x, destination.y, layer); }

    public IKey GetKey() { return key; }

    public com.tw.p2pgldemo.Networking.PlayerState GetState() {
        return new com.tw.p2pgldemo.Networking.PlayerState(
                name,
                textureName,
                gameScreen.GetWorldName(),
                GetPos(),
                GetDestination());
    }
}
