package com.gbozza.android.gigagal.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gbozza.android.gigagal.entities.Explosion;
import com.gbozza.android.gigagal.util.Constants;

public class VictoryOverlay {

    public final static String TAG = VictoryOverlay.class.getName();

    private final Viewport mViewport;
    private final BitmapFont mFont;
    private Array<Explosion> mExplosions;

    public VictoryOverlay() {
        mViewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        mFont = new BitmapFont(Gdx.files.internal(Constants.FONT_FILE));
        mFont.getData().setScale(1);
    }

    public void init() {
        mExplosions = new Array<Explosion>(Constants.EXPLOSION_COUNT);
        for (int i = 0; i < Constants.EXPLOSION_COUNT; i++) {
            Explosion explosion = new Explosion(new Vector2(
                    MathUtils.random(mViewport.getWorldWidth()),
                    MathUtils.random(mViewport.getWorldHeight())
            ));
            explosion.setOffset(MathUtils.random(Constants.LEVEL_END_DURATION));
            mExplosions.add(explosion);
        }
    }

    public void render(SpriteBatch batch) {
        mViewport.apply();
        batch.setProjectionMatrix(mViewport.getCamera().combined);
        batch.begin();
        for (Explosion explosion : mExplosions){
            explosion.render(batch);
        }
        mFont.draw(batch, Constants.VICTORY_MESSAGE, mViewport.getWorldWidth() / 2, mViewport.getWorldHeight() / 2.5f, 0, Align.center, false);
        batch.end();
    }

    public Viewport getViewport() {
        return mViewport;
    }

}