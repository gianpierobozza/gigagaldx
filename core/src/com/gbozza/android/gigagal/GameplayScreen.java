package com.gbozza.android.gigagal;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.gbozza.android.gigagal.overlays.GameOverOverlay;
import com.gbozza.android.gigagal.overlays.GigaGalHud;
import com.gbozza.android.gigagal.overlays.OnscreenControls;
import com.gbozza.android.gigagal.overlays.VictoryOverlay;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.ChaseCam;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.LevelLoader;
import com.gbozza.android.gigagal.util.Utils;

class GameplayScreen extends ScreenAdapter {

    public static final String TAG = GameplayScreen.class.getName();

    private Level mLevel;
    private SpriteBatch mBatch;
    private ChaseCam mChaseCam;
    private GigaGalHud mHud;
    private VictoryOverlay mVictoryOverlay;
    private GameOverOverlay mGameOverOverlay;
    private OnscreenControls mOnscreenControls;
    private long mLevelEndOverlayStartTime;

    @Override
    public void show() {
        AssetManager am = new AssetManager();
        Assets.instance.init(am);

        mBatch = new SpriteBatch();
        mChaseCam = new ChaseCam();
        mHud = new GigaGalHud();
        mVictoryOverlay = new VictoryOverlay();
        mGameOverOverlay = new GameOverOverlay();

        startNewLevel();
    }

    @Override
    public void resize(int width, int height) {
        mHud.getViewport().update(width, height, true);
        mVictoryOverlay.getViewport().update(width, height, true);
        mGameOverOverlay.getViewport().update(width, height, true);
        mLevel.getViewport().update(width, height, true);
        mChaseCam.setCamera(mLevel.getViewport().getCamera());

        mOnscreenControls.getViewport().update(width, height, true);
        mOnscreenControls.recalculateButtonPositions();
    }

    @Override
    public void dispose() {
        Assets.instance.dispose();
        mBatch.dispose();
    }

    @Override
    public void render(float delta) {
        mLevel.update(delta);
        mChaseCam.update(delta);
        Gdx.gl.glClearColor(
                Constants.BACKGROUND_COLOR.r,
                Constants.BACKGROUND_COLOR.g,
                Constants.BACKGROUND_COLOR.b,
                Constants.BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mLevel.render(mBatch);
        if (onMobile()) {
            mOnscreenControls.render(mBatch);
        }
        mHud.render(mBatch, mLevel.getGG().getLives(), mLevel.getGG().getAmmo(), mLevel.getScore());
        renderLevelEndOverlays(mBatch);
    }

    private void renderLevelEndOverlays(SpriteBatch batch) {
        if (mLevel.getVictory()) {
            if (mLevelEndOverlayStartTime == 0) {
                mLevelEndOverlayStartTime = TimeUtils.nanoTime();
                mVictoryOverlay.init();
            }
            mVictoryOverlay.render(batch);
            if (Utils.secondsSince(mLevelEndOverlayStartTime) > Constants.LEVEL_END_DURATION) {
                mLevelEndOverlayStartTime = 0;
                levelComplete();
            }
        }

        if (mLevel.getGameOver()) {
            if (mLevelEndOverlayStartTime == 0) {
                mLevelEndOverlayStartTime = TimeUtils.nanoTime();
                mGameOverOverlay.init();
            }
            mGameOverOverlay.render(batch);
            if (Utils.secondsSince(mLevelEndOverlayStartTime) > Constants.LEVEL_END_DURATION) {
                mLevelEndOverlayStartTime = 0;
                levelFailed();
            }
        }
    }

    private void startNewLevel() {
        mLevel = LevelLoader.load("level1");
        mOnscreenControls = new OnscreenControls(mLevel.getGG());

        if (onMobile()) {
            Gdx.input.setInputProcessor(mOnscreenControls);
        }

        mChaseCam.setCamera(mLevel.getViewport().getCamera());
        mChaseCam.setTarget(mLevel.getGG());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private boolean onMobile() {
        return Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS;
    }

    private void levelComplete() {
        startNewLevel();
    }

    private void levelFailed() {
        startNewLevel();
    }

}
