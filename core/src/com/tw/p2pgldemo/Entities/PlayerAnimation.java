package com.tw.p2pgldemo.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.Networking.PlayerState;

/**
 * Created by t_j_w on 26/03/2016.
 */
public class PlayerAnimation {

    TextureRegion[] walkLeftFrames, walkRightFrames,
            walkForwardFrames, walkBackFrames;
    TextureRegion idleFrame, currentFrame;
    Animation walkLeftAnim, walkRightAnim, walkForwardAnim, walkBackAnim;
    Vector2 cellSize = new Vector2(32, 48);
    Vector2 cells = new Vector2(4, 4);
    Direction direction;
    Texture texture;
    float animTime;

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public PlayerAnimation(Texture texture) {
        this.texture = texture;
        direction = Direction.FORWARD;

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

    public TextureRegion Animate(Player.MovementState playerState, Vector2 pos, Vector2 destination) {
        animTime += Gdx.graphics.getDeltaTime();

        switch (playerState) {
            case STANDING: {
                currentFrame = idleFrame;
                break;
            }
            case WALKING: {
                CalculateDirection(pos, destination);
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
        return currentFrame;
    }

    private void CalculateDirection(Vector2 pos, Vector2 destination) {
        float xDiff = pos.x - destination.x;
        float yDiff = pos.y - destination.y;
        if(Math.abs(xDiff) > Math.abs(yDiff))//moving horizontally
            direction = (pos.x > destination.x) ? Direction.LEFT : Direction.RIGHT;
        else
            direction = (pos.y > destination.y) ? Direction.FORWARD : Direction.BACKWARD;
    }

}
