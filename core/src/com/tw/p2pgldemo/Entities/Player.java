package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Set;

/**
 * 2D Animation with libGDX: https://github.com/libgdx/libgdx/wiki/2D-Animation
 */
public class Player {

    static final float walkingSpeed = 0.05f;

    Rectangle rectangle;
    Texture texture;
    Vector2 cellSize = new Vector2(32, 48);
    Vector2 cells = new Vector2(4, 4);
    TextureRegion[] walkLeftFrames, walkRightFrames,
            walkForwardFrames, walkBackFrames;
    TextureRegion idleFrame, currentFrame;
    Animation walkLeftAnim, walkRightAnim, walkForwardAnim, walkBackAnim;
    Vector2 lastPos, pos, destination;
    float animTime;
    PlayerState playerState;
    Direction direction;

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

    public Player(Rectangle rect, Texture tex) {
        this.rectangle = rect;
        this.texture = tex;
        SetupAnimations();
        playerState = PlayerState.STANDING;
        direction = Direction.FORWARD;
        pos = new Vector2(rect.x, rect.y);
        destination = new Vector2(pos.x, pos.y);
        lastPos = new Vector2(pos.x, pos.y);
    }

    public void SetupAnimations() {
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

    public void Animate() {
        animTime += Gdx.graphics.getDeltaTime();

        switch (playerState) {
            case STANDING: {
                //currentFrame = walkForwardAnim.getKeyFrame(animTime, true);
                currentFrame = idleFrame;
                break;
            }
            case WALKING: {
                CalculateDirection();
                //Arrived at destination
                if(pos.x == destination.x && pos.y == destination.y) {
                    playerState = PlayerState.STANDING;
                    direction = Direction.FORWARD;
                    currentFrame = idleFrame;
                }
                //Travelling to destination
                pos.interpolate(destination, walkingSpeed, Interpolation.linear);
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

    public void CalculateDirection() {
        float xDiff = pos.x - destination.x;
        float yDiff = pos.y - destination.y;
        if(Math.abs(xDiff) > Math.abs(yDiff))//moving horizontally
            direction = (pos.x > destination.x) ? Direction.LEFT : Direction.RIGHT;
        else
            direction = (pos.y > destination.y) ? Direction.FORWARD : Direction.BACKWARD;
    }

    //Order player to walk to position
    public void Go(float x, float y) {
        playerState = PlayerState.WALKING;
        destination = new Vector2(x - rectangle.width / 2, y);
    }
}
