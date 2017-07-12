package com.gbozza.android.gigagal.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gbozza.android.gigagal.entities.Enemy;
import com.gbozza.android.gigagal.entities.Platform;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class GameOverOverlay {

    public final static String TAG = GameOverOverlay.class.getName();

    private final Viewport mViewport;
    private final BitmapFont mFont;
    private Array<Enemy> mEnemies;
    private long mStartTime;

    public GameOverOverlay() {
        mViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        mFont = new BitmapFont(Gdx.files.internal(Constants.FONT_FILE));
        mFont.getData().setScale(1);
    }

    public void init() {
        mStartTime = TimeUtils.nanoTime();
        mEnemies = new Array<Enemy>(Constants.ENEMY_COUNT);
        for (int i = 0; i < Constants.ENEMY_COUNT; i++) {
            Platform fakePlatform = new Platform(
                    MathUtils.random(mViewport.getWorldWidth()),
                    MathUtils.random(-Constants.ENEMY_CENTER.y, mViewport.getWorldHeight()
                    ), 0, 0);
            Enemy enemy = new Enemy(fakePlatform);
            mEnemies.add(enemy);
        }
    }

    public void render(SpriteBatch batch) {
        mViewport.apply();
        batch.setProjectionMatrix(mViewport.getCamera().combined);
        batch.begin();
        float timeElapsed = Utils.secondsSince(mStartTime);
        int enemiesToShow = (int) (Constants.ENEMY_COUNT * (timeElapsed / Constants.LEVEL_END_DURATION));
        for (int i = 0; i < enemiesToShow; i++){
            Enemy enemy = mEnemies.get(i);
            enemy.update(0);
            enemy.render(batch);
        }
        mFont.draw(batch, Constants.GAME_OVER_MESSAGE, mViewport.getWorldWidth() / 2, mViewport.getWorldHeight() / 2.5f, 0, Align.center, false);
        batch.end();
    }

    public Viewport getViewport() {
        return mViewport;
    }

}