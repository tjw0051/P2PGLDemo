package com.tw.p2pgldemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Set;

/**
 * 2D Animation with libGDX: https://github.com/libgdx/libgdx/wiki/2D-Animation
 */
public class Player {

    Rectangle rectangle;
    Texture texture;
    Vector2 cellSize = new Vector2(32, 48);
    Vector2 cells = new Vector2(4, 4);
    TextureRegion[] walkLeftFrames, walkRightFrames,
            walkForwardFrames, walkBackFrames;
    TextureRegion idleFrame, currentFrame;
    Animation walkLeftAnim, walkRightAnim, walkForwardAnim, walkBackAnim;
    Vector2 pos;
    float animTime;

    public enum PlayerState {
        STANDING,
        WALKING

    }

    public Player(Rectangle rect, Texture tex) {
        this.rectangle = rect;
        this.texture = tex;
        SetupAnimations();
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
        animTime += Gdx.graphics.getDeltaTime();
        currentFrame = walkForwardAnim.getKeyFrame(animTime, true);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, rectangle.x, rectangle.y, rectangle.getWidth(), rectangle.getHeight());
        spriteBatch.end();
    }
}
