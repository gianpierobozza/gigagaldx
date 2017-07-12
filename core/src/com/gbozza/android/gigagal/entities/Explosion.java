package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class Explosion {

    public final static String TAG = Explosion.class.getName();

    private final Vector2 mPosition;
    private final long mStartTime;
    private float mOffset = 0;

    public Explosion(Vector2 position) {
        mPosition = position;
        mStartTime = TimeUtils.nanoTime();
    }

    public void render(SpriteBatch batch) {
        if (!isFinished() && !yetToStart()) {
            Utils.drawTextureRegion(
                    batch,
                    (TextureRegion) Assets.instance.explosionAssets.explosion.getKeyFrame(Utils.secondsSince(mStartTime) - mOffset),
                    mPosition.x - Constants.EXPLOSION_CENTER.x,
                    mPosition.y - Constants.EXPLOSION_CENTER.y
            );
        }
    }

    private boolean yetToStart(){
        return Utils.secondsSince(mStartTime) - mOffset < 0;
    }

    public boolean isFinished() {
        float elapsedTime = Utils.secondsSince(mStartTime) - mOffset;
        return Assets.instance.explosionAssets.explosion.isAnimationFinished(elapsedTime);
    }

    public void setOffset(float offset) {
        mOffset = offset;
    }

}