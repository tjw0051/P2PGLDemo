package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.tw.p2pgldemo.IO.Interaction;
import com.tw.p2pgldemo.Screens.GameScreen;

import java.util.Set;

/**
 * 2D Animation with libGDX: https://github.com/libgdx/libgdx/wiki/2D-Animation
 */
public class Player {

    static final float walkingSpeed = 0.5f;
    int layer = 1;

    GameScreen gameScreen;
    Rectangle rectangle;
    Texture texture;
    Vector2 cellSize = new Vector2(32, 48);
    Vector2 cells = new Vector2(4, 4);
    TextureRegion[] walkLeftFrames, walkRightFrames,
            walkForwardFrames, walkBackFrames;
    TextureRegion idleFrame, currentFrame;
    Animation walkLeftAnim, walkRightAnim, walkForwardAnim, walkBackAnim;
    Vector2 pos, destination;
    float animTime;
    PlayerState playerState;
    Direction direction;
    Interaction destinationInteraction;
    private long lastWalkTime;

    public enum PlayerState {
        STANDING,
        WALKING
    }
    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public Player(Rectangle rect, Texture tex, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.rectangle = rect;
        this.texture = tex;
        SetupAnimations();
        playerState = PlayerState.STANDING;
        direction = Direction.FORWARD;
        pos = new Vector2(rect.x, rect.y);
        destination = new Vector2(pos.x, pos.y);
    }

    private void SetupAnimations() {
        TextureRegion[][] allFrames = TextureRegion.split(texture, (int)cellSize.x, (int)cellSize.y);
        idleFrame = new TextureRegion(allFrames[0][0]);
        walkForwardFrames = new TextureRegion[] {allFrames[0][0], allFrames[0][1], allFrames[0][2], allFrames[0][3]};
        walkLeftFrames = new TextureRegion[] {allFrames[1][0], allFrames[1][1], allFrames[1][2], allFrames[1][3]};
        walkRightFrames = new TextureRegion[] {allFrames[2][0], allFrames[2][1], allFrames[2][2], allFrames[2][3]};
        walkBackFrames = new TextureRegion[] {allFrames[3][0], allFrames[3][1], allFrames[3][2], allFrames[3][3]};
        walkLeftAnim = new Animation(0.2f, walkLeftFrames);
        walkRightAnim = new Animation(0.2f, walkRightFrames);
        walkForwardAnim = new Animation(0.2f, walkForwardFrames);
        walkBackAnim = new Animation(0.2f, walkBackFrames);
        animTime = 0.0f;
    }

    public void render(SpriteBatch spriteBatch) {

        Animate();
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, pos.x, pos.y, rectangle.getWidth(), rectangle.getHeight());
        spriteBatch.end();
    }

    private void Animate() {
        animTime += Gdx.graphics.getDeltaTime();

        switch (playerState) {
            case STANDING: {
                currentFrame = idleFrame;
                break;
            }
            case WALKING: {
                CalculateDirection();
                //Travelling to destination
                pos.interpolate(destination, walkingSpeed, Interpolation.linear);
                //Arrived at destination
                if((int)pos.x == (int)destination.x && (int)pos.y == (int)destination.y) {
                    destination = pos;
                    playerState = PlayerState.STANDING;
                    if(destinationInteraction != null) {
                        Interact();
                        destinationInteraction = null;
                    }
                }
                //Animate walk
                switch (direction) {
                    case LEFT:
                        currentFrame = walkLeftAnim.getKeyFrame(animTime, true);
                        break;
                    case RIGHT:
                        currentFrame = walkRightAnim.getKeyFrame(animTime, true);
                        break;
                    case FORWARD:
                        currentFrame = walkForwardAnim.getKeyFrame(animTime, true);
                        break;
                    case BACKWARD:
                        currentFrame = walkBackAnim.getKeyFrame(animTime, true);
                }
                break;
            }
        }
    }

    private void Move() {
        long time = TimeUtils.nanoTime();
        //Vector2.len()
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

    private void CalculateDirection() {
        float xDiff = pos.x - destination.x;
        float yDiff = pos.y - destination.y;
        if(Math.abs(xDiff) > Math.abs(yDiff))//moving horizontally
            direction = (pos.x > destination.x) ? Direction.LEFT : Direction.RIGHT;
        else
            direction = (pos.y > destination.y) ? Direction.FORWARD : Direction.BACKWARD;
    }

    //Order player to walk to position
    public void Go(float x, float y) {
        if(playerState != PlayerState.WALKING) {
            playerState = PlayerState.WALKING;
            destination = new Vector2(x - rectangle.width / 2, y);
        }
        //Vector2 result = pos.lerp(new Vector2(x, y), 0.5f);
        //pos = result;
    }

    public void Go(float x, float y, Interaction interaction) {
        if(playerState != PlayerState.WALKING) {
            destinationInteraction = interaction;
            Go(x, y);
        }
    }

    public void Go(Tile tile) {
        playerState = PlayerState.WALKING;
        destination.x = tile.GetRect().x;
        destination.y = tile.GetRect().y;
    }

    public int GetLayer() { return layer; }
}
