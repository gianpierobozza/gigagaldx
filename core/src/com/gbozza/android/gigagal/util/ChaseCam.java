package com.gbozza.android.gigagal.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.gbozza.android.gigagal.entities.GigaGal;

public class ChaseCam {

    public final static String TAG = ChaseCam.class.getName();

    private Camera mCamera;
    private GigaGal mTarget;
    private Boolean mFollowing;

    public ChaseCam() {
        mFollowing = true;
    }

    public void update(float delta){
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)){
            mFollowing = !mFollowing;
        }

        if (mFollowing) {
            mCamera.position.x = mTarget.getPosition().x;
            mCamera.position.y = mTarget.getPosition().y;
        } else { // WASD Controls
            if (Gdx.input.isKeyPressed(Keys.A)) {
                mCamera.position.x -= delta * Constants.CHASE_CAM_MOVE_SPEED;
            }
            if (Gdx.input.isKeyPressed(Keys.D)) {
                mCamera.position.x += delta * Constants.CHASE_CAM_MOVE_SPEED;
            }
            if (Gdx.input.isKeyPressed(Keys.W)) {
                mCamera.position.y += delta * Constants.CHASE_CAM_MOVE_SPEED;
            }
            if (Gdx.input.isKeyPressed(Keys.S)) {
                mCamera.position.y -= delta * Constants.CHASE_CAM_MOVE_SPEED;
            }
        }
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void setTarget(GigaGal gg) {
        mTarget = gg;
    }

}

