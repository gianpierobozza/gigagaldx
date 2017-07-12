package com.gbozza.android.gigagal.overlays;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Utils;

public class GigaGalHud {

    public final static String TAG = GigaGalHud.class.getName();

    private final Viewport mViewport;
    private final BitmapFont mFont;

    public GigaGalHud() {
        mViewport = new ExtendViewport(Constants.HUD_VIEWPORT_SIZE, Constants.HUD_VIEWPORT_SIZE);
        mFont = new BitmapFont();
        mFont.getData().setScale(1);
    }

    public void render(SpriteBatch batch, int lives, int ammo, int score) {
        mViewport.apply();
        batch.setProjectionMatrix(mViewport.getCamera().combined);
        batch.begin();
        final String hudString =
                Constants.HUD_SCORE_LABEL + score + "\n" +
                        Constants.HUD_AMMO_LABEL + ammo;

        mFont.draw(batch, hudString, Constants.HUD_MARGIN, mViewport.getWorldHeight() - Constants.HUD_MARGIN);
        final TextureRegion standingRight = Assets.instance.gigaGalAssets.standingRight;
        for (int i = 1; i <= lives; i++) {
            final Vector2 drawPosition = new Vector2(
                    mViewport.getWorldWidth() - i * (Constants.HUD_MARGIN / 2 + standingRight.getRegionWidth()),
                    mViewport.getWorldHeight() - Constants.HUD_MARGIN - standingRight.getRegionHeight()
            );
            Utils.drawTextureRegion(
                    batch,
                    standingRight,
                    drawPosition
            );
        }
        batch.end();
    }

    public Viewport getViewport() {
        return mViewport;
    }

}
