package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gbozza.android.gigagal.util.Assets;

public class Platform {

    public final static String TAG = Platform.class.getName();

    private float mTop;
    private float mBottom;
    private float mLeft;
    private float mRight;
    private String mIdentifier;

    public Platform(float left, float top, float width, float height) {
        mTop = top;
        mBottom = top - height;
        mLeft = left;
        mRight = left + width;
    }

    public void render(SpriteBatch batch) {
        float width = mRight - mLeft;
        float height = mTop - mBottom;

        Assets.instance.platformAssets.platformNinePatch.draw(batch, mLeft - 1, mBottom - 1, width + 2, height + 2);
    }

    public float getTop() {
        return mTop;
    }

    public float getBottom() {
        return mBottom;
    }

    float getLeft() {
        return mLeft;
    }

    float getRight() {
        return mRight;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

}
