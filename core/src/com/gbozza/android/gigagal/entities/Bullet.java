package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gbozza.android.gigagal.Level;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Enums.Direction;
import com.gbozza.android.gigagal.util.Utils;

public class Bullet {

    public final static String TAG = Bullet.class.getName();

    private final Direction mDirection;
    private Vector2 mPosition;
    private final Level mLevel;

    public boolean active;

    public Bullet(Level level, Vector2 position, Direction direction) {
        mPosition = position;
        mDirection = direction;
        mLevel = level;

        active = true;
    }

    public void update(float delta) {
        switch (mDirection) {
            case LEFT:
                mPosition.x -= delta * Constants.BULLET_MOVE_SPEED;
                break;
            case RIGHT:
                mPosition.x += delta * Constants.BULLET_MOVE_SPEED;
                break;
        }

        for (Enemy enemy : mLevel.getEnemies()) {
            if (mPosition.dst(enemy.getPosition()) < Constants.ENEMY_SHOT_RADIUS) {
                mLevel.spawnExplosion(mPosition);
                active = false;
                enemy.decreaseHealth();
                mLevel.addToScore(Constants.ENEMY_HIT_SCORE);
            }
        }

        final float worldWidth = mLevel.getViewport().getWorldWidth();
        final float cameraX = mLevel.getViewport().getCamera().position.x;
        if (mPosition.x < cameraX - worldWidth / 2 || mPosition.x > cameraX + worldWidth / 2) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {TextureRegion region = Assets.instance.bulletAssets.bullet;
        Utils.drawTextureRegion(batch, region, mPosition, Constants.BULLET_CENTER);
    }

}