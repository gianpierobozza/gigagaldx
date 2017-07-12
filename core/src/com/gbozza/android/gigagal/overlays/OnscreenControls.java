package com.gbozza.android.gigagal.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gbozza.android.gigagal.entities.GigaGal;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class OnscreenControls extends InputAdapter {

    public static final String TAG = OnscreenControls.class.getName();

    private final Viewport mViewport;
    private GigaGal mGigaGal;
    private Vector2 mMoveLeftCenter;
    private Vector2 mMoveRightCenter;
    private Vector2 mShootCenter;
    private Vector2 mJumpCenter;
    private int mMoveLeftPointer;
    private int mMoveRightPointer;
    private int mJumpPointer;

    public OnscreenControls(GigaGal gigaGal) {
        mViewport = new ExtendViewport(
                Constants.ONSCREEN_CONTROLS_VIEWPORT_SIZE,
                Constants.ONSCREEN_CONTROLS_VIEWPORT_SIZE);

        mMoveLeftCenter = new Vector2();
        mMoveRightCenter = new Vector2();
        mShootCenter = new Vector2();
        mJumpCenter = new Vector2();
        mGigaGal = gigaGal;

        recalculateButtonPositions();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 viewportPosition = mViewport.unproject(new Vector2(screenX, screenY));
        if (viewportPosition.dst(mShootCenter) < Constants.BUTTON_RADIUS) {
            mGigaGal.shoot();
        } else if (viewportPosition.dst(mJumpCenter) < Constants.BUTTON_RADIUS) {
            mJumpPointer = pointer;
            mGigaGal.setJumpButtonPressed(true);
        } else if (viewportPosition.dst(mMoveLeftCenter) < Constants.BUTTON_RADIUS) {
            mMoveLeftPointer = pointer;
            mGigaGal.setLeftButtonPressed(true);
        } else if (viewportPosition.dst(mMoveRightCenter) < Constants.BUTTON_RADIUS) {
            mMoveRightPointer = pointer;
            mGigaGal.setRightButtonPressed(true);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 viewportPosition = mViewport.unproject(new Vector2(screenX, screenY));
        if (pointer == mMoveLeftPointer && viewportPosition.dst(mMoveRightCenter) < Constants.BUTTON_RADIUS) {
            mGigaGal.setLeftButtonPressed(false);
            mGigaGal.setRightButtonPressed(true);
            mMoveLeftPointer = 0;
            mMoveRightPointer = pointer;
        }
        if (pointer == mMoveRightPointer && viewportPosition.dst(mMoveLeftCenter) < Constants.BUTTON_RADIUS) {
            mGigaGal.setRightButtonPressed(false);
            mGigaGal.setLeftButtonPressed(true);
            mMoveRightPointer = 0;
            mMoveLeftPointer = pointer;
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    public void render(SpriteBatch batch) {
        mViewport.apply();
        batch.setProjectionMatrix(mViewport.getCamera().combined);
        batch.begin();
        if (!Gdx.input.isTouched(mJumpPointer)) {
            mGigaGal.setJumpButtonPressed(false);
            mJumpPointer = 0;
        }
        if (!Gdx.input.isTouched(mMoveLeftPointer)) {
            mGigaGal.setLeftButtonPressed(false);
            mMoveLeftPointer = 0;
        }
        if (!Gdx.input.isTouched(mMoveRightPointer)) {
            mGigaGal.setRightButtonPressed(false);
            mMoveRightPointer = 0;
        }
        Utils.drawTextureRegion(
                batch,
                Assets.instance.onscreenControlsAssets.moveLeft,
                mMoveLeftCenter,
                Constants.BUTTON_CENTER
        );
        Utils.drawTextureRegion(
                batch,
                Assets.instance.onscreenControlsAssets.moveRight,
                mMoveRightCenter,
                Constants.BUTTON_CENTER
        );
        Utils.drawTextureRegion(
                batch,
                Assets.instance.onscreenControlsAssets.shoot,
                mShootCenter,
                Constants.BUTTON_CENTER
        );
        Utils.drawTextureRegion(
                batch,
                Assets.instance.onscreenControlsAssets.jump,
                mJumpCenter,
                Constants.BUTTON_CENTER
        );
        batch.end();
    }

    public void recalculateButtonPositions() {
        mMoveLeftCenter.set(Constants.BUTTON_RADIUS * 3 / 4, Constants.BUTTON_RADIUS);
        mMoveRightCenter.set(Constants.BUTTON_RADIUS * 2, Constants.BUTTON_RADIUS * 3 / 4);
        mShootCenter.set(
                mViewport.getWorldWidth() - Constants.BUTTON_RADIUS * 2f,
                Constants.BUTTON_RADIUS * 3 / 4
        );
        mJumpCenter.set(
                mViewport.getWorldWidth() - Constants.BUTTON_RADIUS * 3 / 4,
                Constants.BUTTON_RADIUS
        );
    }

    public Viewport getViewport() {
        return mViewport;
    }

}