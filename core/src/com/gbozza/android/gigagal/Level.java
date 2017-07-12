package com.gbozza.android.gigagal;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gbozza.android.gigagal.entities.Bullet;
import com.gbozza.android.gigagal.entities.Enemy;
import com.gbozza.android.gigagal.entities.ExitPortal;
import com.gbozza.android.gigagal.entities.Explosion;
import com.gbozza.android.gigagal.entities.GigaGal;
import com.gbozza.android.gigagal.entities.Platform;
import com.gbozza.android.gigagal.entities.Powerup;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Enums.Direction;

public class Level {

    public static final String TAG = Level.class.getName();

    private Viewport mViewport;
    private GigaGal mGigaGal;
    private ExitPortal mExitPortal;
    private Array<Platform> mPlatforms;
    private DelayedRemovalArray<Enemy> mEnemies;
    private DelayedRemovalArray<Bullet> mBullets;
    private DelayedRemovalArray<Explosion> mExplosions;
    private DelayedRemovalArray<Powerup> mPowerups;
    private int mScore;
    private boolean mGameOver;
    private boolean mVictory;

    public Level() {
        mViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);

        mGigaGal = new GigaGal(new Vector2(15, 40), this);
        mPlatforms = new Array<Platform>();
        mBullets = new DelayedRemovalArray<Bullet>();
        mEnemies = new DelayedRemovalArray<Enemy>();
        mExplosions = new DelayedRemovalArray<Explosion>();
        mPowerups = new DelayedRemovalArray<Powerup>();
        mExitPortal = new ExitPortal(Constants.EXIT_PORTAL_DEFAULT_LOCATION);

        mGameOver = false;
        mVictory = false;
        mScore = 0;
    }

    void update(float delta) {
        if (mGigaGal.getLives() < 0) {
            mGameOver = true;
        } else if (mGigaGal.getPosition().dst(mExitPortal.getPosition()) < Constants.EXIT_PORTAL_RADIUS) {
            mVictory = true;
        }

        if (!mGameOver && !mVictory) {
            // Update GG
            mGigaGal.update(delta, mPlatforms);

            // Update Bullets
            mBullets.begin();
            for (Bullet bullet : mBullets) {
                bullet.update(delta);
                if (!bullet.active) {
                    mBullets.removeValue(bullet, false);
                }
            }
            mBullets.end();

            // Update Enemies
            mEnemies.begin();
            for (int i = 0; i < mEnemies.size; i++) {
                Enemy enemy = mEnemies.get(i);
                enemy.update(delta);
                if (enemy.getHealth() < 1) {
                    spawnExplosion(enemy.getPosition());
                    mEnemies.removeIndex(i);
                    mScore += Constants.ENEMY_KILL_SCORE;
                }
            }
            mEnemies.end();

            // Explosions
            mExplosions.begin();
            for (int i = 0; i < mExplosions.size; i++) {
                if (mExplosions.get(i).isFinished()) {
                    mExplosions.removeIndex(i);
                }
            }
            mExplosions.end();
        }
    }

    public void render(SpriteBatch batch) {
        mViewport.apply();
        batch.setProjectionMatrix(mViewport.getCamera().combined);

        batch.begin();

        for (Platform platform : mPlatforms) {
            platform.render(batch);
        }

        mExitPortal.render(batch);

        for (Powerup powerup : mPowerups) {
            powerup.render(batch);
        }

        for (Enemy enemy : mEnemies) {
            enemy.render(batch);
        }

        mGigaGal.render(batch);

        for (Bullet bullet : mBullets) {
            bullet.render(batch);
        }

        for (Explosion explosion : mExplosions) {
            explosion.render(batch);
        }

        batch.end();
    }

    GigaGal getGG() {
        return mGigaGal;
    }

    public void setGG(GigaGal gigaGal) {
        mGigaGal = gigaGal;
    }

    public DelayedRemovalArray<Enemy> getEnemies() {
        return mEnemies;
    }

    public DelayedRemovalArray<Powerup> getPowerups() {
        return mPowerups;
    }

    public Array<Platform> getPlatforms() {
        return mPlatforms;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    boolean getGameOver() {
        return mGameOver;
    }

    public void setGameOver(boolean flag) {
        mGameOver = flag;
    }

    boolean getVictory() {
        return mVictory;
    }

    int getScore() {
        return mScore;
    }

    public void addToScore(int add) {
        mScore += add;
    }

    public void setExitPortal(ExitPortal exitPortal) {
        mExitPortal = exitPortal;
    }

    public void spawnBullet(Level level, Vector2 position, Direction direction) {
        mBullets.add(new Bullet(level, position, direction));
    }

    public void spawnExplosion(Vector2 position) {
        mExplosions.add(new Explosion(position));
    }

}
