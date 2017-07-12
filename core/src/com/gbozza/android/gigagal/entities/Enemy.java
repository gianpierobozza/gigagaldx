package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Enums.Direction;
import com.gbozza.android.gigagal.util.Utils;


public class Enemy {

    public final static String TAG = Enemy.class.getName();

    private final Platform mPlatform;
    private Direction mDirection;
    private Vector2 mPosition;
    private final long startTime;
    private int mHealth;

    public Enemy(Platform platform) {
        mPlatform = platform;
        mDirection = Direction.RIGHT;
        mPosition = new Vector2(mPlatform.getLeft(), mPlatform.getTop() + Constants.ENEMY_CENTER.y);
        startTime = TimeUtils.nanoTime();
        mHealth = Constants.ENEMY_HEALTH;
    }

    public void update(float delta) {
        switch (mDirection) {
            case LEFT:
                mPosition.x -= Constants.ENEMY_MOVEMENT_SPEED * delta;
                break;
            case RIGHT:
                mPosition.x += Constants.ENEMY_MOVEMENT_SPEED * delta;
        }

        if (mPosition.x < mPlatform.getLeft()) {
            mPosition.x = mPlatform.getLeft();
            mDirection = Direction.RIGHT;
        } else if (mPosition.x > mPlatform.getRight()) {
            mPosition.x = mPlatform.getRight();
            mDirection = Direction.LEFT;
        }

        final float elapsedTime = Utils.secondsSince(startTime);
        final float bobMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * elapsedTime / Constants.ENEMY_BOB_PERIOD);
        mPosition.y = mPlatform.getTop() + Constants.ENEMY_CENTER.y + Constants.ENEMY_BOB_AMPLITUDE * bobMultiplier;
    }

    public void render(SpriteBatch batch) {
        final TextureRegion region = Assets.instance.enemyAssets.enemy;
        Utils.drawTextureRegion(batch, region, mPosition, Constants.ENEMY_CENTER);
    }

    public Vector2 getPosition() {
        return mPosition;
    }

    public int getHealth() {
        return mHealth;
    }

    void decreaseHealth() {
        mHealth--;
    }

}