package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class ExitPortal {

    public final static String TAG = ExitPortal.class.getName();

    private final Vector2 mPosition;
    private final long mStartTime;

    public ExitPortal(Vector2 position) {
        mPosition = position;
        mStartTime = TimeUtils.nanoTime();
    }

    public void render(SpriteBatch batch) {
        final float elapsedTime = Utils.secondsSince(mStartTime);
        final TextureRegion region = (TextureRegion) Assets.instance.exitPortalAssets.exitPortal.getKeyFrame(elapsedTime, true);
        Utils.drawTextureRegion(batch, region, mPosition, Constants.EXIT_PORTAL_CENTER);
    }

    public Vector2 getPosition() {
        return mPosition;
    }

}