package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class Powerup {

    public final static String TAG = Powerup.class.getName();

    private final Vector2 mPosition;

    public Powerup(Vector2 position) {
        mPosition = position;
    }

    public void render(SpriteBatch batch) {
        final TextureRegion region = Assets.instance.powerupAssets.powerup;
        Utils.drawTextureRegion(batch, region, mPosition, Constants.POWERUP_CENTER);
    }

    public Vector2 getPosition() {
        return mPosition;
    }

}