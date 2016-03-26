package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.Networking.PlayerState;
import com.tw.p2pgldemo.Screens.GameScreen;

import java.util.Set;

/**
 * 2D Animation with libGDX: https://github.com/libgdx/libgdx/wiki/2D-Animation
 */
public class Player {

    static final float walkingSpeed = 0.5f;
    int layer = 1;

    String name;
    GameScreen gameScreen;
    Rectangle rectangle;

    TextureRegion currentFrame;

    Vector2 pos, destination;

    MovementState playerState;

    Interaction destinationInteraction;
    PlayerAnimation playerAnimation;

    public enum MovementState {
        STANDING,
        WALKING
    }

    public Player(Rectangle rect, Texture tex, GameScreen gameScreen, String name) {
        this.gameScreen = gameScreen;
        this.rectangle = rect;
        //this.texture = tex;
        this.name = name;
        playerAnimation = new PlayerAnimation(tex);
        playerAnimation.SetupAnimations();
        playerState = MovementState.STANDING;
        pos = new Vector2(rect.x, rect.y);
        destination = new Vector2(pos.x, pos.y);
    }

    public void render(SpriteBatch spriteBatch) {
        switch (playerState) {
            case STANDING: {
                //currentFrame = idleFrame;
                break;
            }
            case WALKING: {
                //CalculateDirection();
                pos.interpolate(destination, walkingSpeed, Interpolation.linear);
                //Arrived at destination
                if((int)pos.x == (int)destination.x && (int)pos.y == (int)destination.y) {
                    destination = pos;
                    playerState = MovementState.STANDING.STANDING;
                    /*
                    com.tw.p2pgldemo.Networking.Connection.GetInstance().SaveState("a1", new Vector3(pos.x, pos.y, 0),
                            new Vector3(0,0,0));
                    */
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
        spriteBatch.end();
    }

    public void SetState(com.tw.p2pgldemo.Networking.PlayerState playerState) {
        this.pos.x = playerState.getPos().x;
        this.pos.y = playerState.getPos().y;
        this.destination.x = playerState.getDestination().x;
        this.destination.y = playerState.getDestination().y;
    }

    private void Interact() {
        if(destinationInteraction.getName().toString().equals("teleport")) {
            System.out.println("teleported");
            gameScreen.LoadWorld((destinationInteraction.getParams())[0]);
            int spawnPosX = Integer.parseInt((destinationInteraction.getParams())[1]);
            int spawnPosY = Integer.parseInt((destinationInteraction.getParams())[2]);
            Vector2 newPos = gameScreen.getWorld().GetTilePos(spawnPosX, spawnPosY);
            if(newPos != null)
                pos = newPos;
            //Teleport the player
        }
    }



    //Order player to walk to position
    public void Go(float x, float y) {
        if(playerState != MovementState.WALKING) {
            playerState = MovementState.WALKING;
            destination = new Vector2(x - rectangle.width / 2, y);
        }
    }

    public void Go(float x, float y, Interaction interaction) {
        if(playerState != MovementState.WALKING) {
            destinationInteraction = interaction;
            Go(x, y);
        }
    }
/*
    public void Go(Tile tile) {
        playerState = MovementState.WALKING;
        destination.x = tile.GetRect().x;
        destination.y = tile.GetRect().y;
    }
*/
    public int GetLayer() { return layer; }

    public Vector3 GetPos() {
        return new Vector3(pos.x, pos.y, layer);
    }

    public Vector3 GetDestination() { return new Vector3(destination.x, destination.y, layer); }

    public String GetName() { return name; }

    public com.tw.p2pgldemo.Networking.PlayerState GetState() {
        return new com.tw.p2pgldemo.Networking.PlayerState(
                name,
                gameScreen.GetWorldName(),
                GetPos(),
                GetDestination());
    }
}
