package com.gbozza.android.gigagal.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.gbozza.android.gigagal.Level;
import com.gbozza.android.gigagal.util.Assets;
import com.gbozza.android.gigagal.util.Constants;
import com.gbozza.android.gigagal.util.Enums.Direction;
import com.gbozza.android.gigagal.util.Enums.JumpState;
import com.gbozza.android.gigagal.util.Enums.WalkState;
import com.gbozza.android.gigagal.util.Utils;

public class GigaGal {

    public final static String TAG = GigaGal.class.getName();

    private boolean mJumpButtonPressed;
    private boolean mLeftButtonPressed;
    private boolean mRightButtonPressed;
    private Vector2 mSpawnLocation;
    private Level mLevel;
    private Vector2 mLastFramePosition;
    private Vector2 mPosition;
    private Vector2 mVelocity;
    private Direction mFacing;
    private JumpState mJumpState;
    private WalkState mWalkState;
    private long mJumpStartTime;
    private long mWalkStartTime;
    private int mAmmo;
    private int mLives;

    public GigaGal(Vector2 spawnLocation, Level level) {
        mSpawnLocation = spawnLocation;
        mLevel = level;

        mPosition = new Vector2();
        mLastFramePosition = new Vector2();
        mVelocity = new Vector2();

        init();
    }

    private void init(){
        mAmmo = Constants.GIGAGAL_INITIAL_AMMO;
        mLives = Constants.GIGAGAL_INITIAL_LIVES;
        respawn();
    }

    private void respawn() {
        mPosition.set(mSpawnLocation);
        mLastFramePosition.set(mSpawnLocation);
        mVelocity.setZero();
        mJumpState = JumpState.FALLING;
        mFacing = Direction.RIGHT;
        mWalkState = WalkState.NOT_WALKING;
    }

    public void update(float delta, Array<Platform> platforms) {
        mLastFramePosition.set(mPosition);
        mVelocity.y -= Constants.GRAVITY;
        mPosition.mulAdd(mVelocity, delta);

        if (mPosition.y < Constants.KILL_PLANE) {
            mLives--;
            if (mLives > -1) {
                respawn();
            }
        }

        // Land on/fall off platforms
        if (mJumpState != JumpState.JUMPING) {
            if (mJumpState != JumpState.RECOILING) {
                mJumpState = JumpState.FALLING;
            }
            for (Platform platform : platforms) {
                if (landedOnPlatform(platform)) {
                    mJumpState = JumpState.GROUNDED;
                    mVelocity.y = 0;
                    mVelocity.x = 0;
                    mPosition.y = platform.getTop() + Constants.GIGAGAL_EYE_HEIGHT;
                }
            }
        }

        // Collide with enemies
        Rectangle gigaGalBounds = new Rectangle(
                mPosition.x - Constants.GIGAGAL_STANCE_WIDTH / 2,
                mPosition.y - Constants.GIGAGAL_EYE_HEIGHT,
                Constants.GIGAGAL_STANCE_WIDTH,
                Constants.GIGAGAL_HEIGHT);

        for (Enemy enemy : mLevel.getEnemies()) {
            Rectangle enemyBounds = new Rectangle(
                    enemy.getPosition().x - Constants.ENEMY_COLLISION_RADIUS,
                    enemy.getPosition().y - Constants.ENEMY_COLLISION_RADIUS,
                    2 * Constants.ENEMY_COLLISION_RADIUS,
                    2 * Constants.ENEMY_COLLISION_RADIUS
            );

            if (gigaGalBounds.overlaps(enemyBounds)) {
                if (mPosition.x < enemy.getPosition().x) {
                    recoilFromEnemy(Direction.LEFT);
                } else {
                    recoilFromEnemy(Direction.RIGHT);
                }
            }
        }

        // Move left/right
        if (mJumpState != JumpState.RECOILING) {
            boolean left = Gdx.input.isKeyPressed(Keys.LEFT) || mLeftButtonPressed;
            boolean right = Gdx.input.isKeyPressed(Keys.RIGHT) || mRightButtonPressed;

            if (left && !right) {
                moveLeft(delta);
            } else if (right && !left) {
                moveRight(delta);
            } else {
                mWalkState = WalkState.NOT_WALKING;
            }
        }

        // Jump
        if (Gdx.input.isKeyPressed(Keys.Z) || mJumpButtonPressed) {
            switch (mJumpState) {
                case GROUNDED:
                    startJump();
                    break;
                case JUMPING:
                    continueJump();
                    break;
                case FALLING:
                    break;
            }
        } else {
            endJump();
        }

        // Power-ups
        DelayedRemovalArray<Powerup> powerups = mLevel.getPowerups();
        powerups.begin();
        for (int i = 0; i < powerups.size; i++) {
            Powerup powerup = powerups.get(i);
            Rectangle powerupBounds = new Rectangle(
                    powerup.getPosition().x - Constants.POWERUP_CENTER.x,
                    powerup.getPosition().y - Constants.POWERUP_CENTER.y,
                    Assets.instance.powerupAssets.powerup.getRegionWidth(),
                    Assets.instance.powerupAssets.powerup.getRegionHeight()
            );
            if (gigaGalBounds.overlaps(powerupBounds)) {
                mAmmo += Constants.POWERUP_AMMO;
                mLevel.addToScore(Constants.POWERUP_SCORE);
                powerups.removeIndex(i);
            }
        }
        powerups.end();

        // Shoot
        if (Gdx.input.isKeyJustPressed(Keys.X)) {
            shoot();
        }
    }

    public void shoot() {
        if (mAmmo > 0) {
            mAmmo--;
            Vector2 bulletPosition;
            if (mFacing == Direction.RIGHT) {
                bulletPosition = new Vector2(
                        mPosition.x + Constants.GIGAGAL_CANNON_OFFSET.x,
                        mPosition.y + Constants.GIGAGAL_CANNON_OFFSET.y
                );
            } else {
                bulletPosition = new Vector2(
                        mPosition.x - Constants.GIGAGAL_CANNON_OFFSET.x,
                        mPosition.y + Constants.GIGAGAL_CANNON_OFFSET.y
                );
            }
            mLevel.spawnBullet(mLevel, bulletPosition, mFacing);
        }
    }

    private boolean landedOnPlatform(Platform platform) {
        boolean leftFootIn = false;
        boolean rightFootIn = false;
        boolean straddle = false;

        // First check if GigaGal's feet were above the platform top last frame and below the platform top this frame
        if (mLastFramePosition.y - Constants.GIGAGAL_EYE_HEIGHT >= platform.getTop() &&
                mPosition.y - Constants.GIGAGAL_EYE_HEIGHT < platform.getTop()) {

            // If so, find the position of GigaGal's left and right toes
            float leftFoot = mPosition.x - Constants.GIGAGAL_STANCE_WIDTH / 2;
            float rightFoot = mPosition.x + Constants.GIGAGAL_STANCE_WIDTH / 2;

            // See if either of GigaGal's toes are on the platform
            leftFootIn = (platform.getLeft() < leftFoot && platform.getRight() > leftFoot);
            rightFootIn = (platform.getLeft() < rightFoot && platform.getRight() > rightFoot);

            // See if GigaGal is straddling the platform
            straddle = (platform.getLeft() > leftFoot && platform.getRight() < rightFoot);
        }
        return leftFootIn || rightFootIn || straddle;
    }

    private void moveLeft(float delta) {
        if (mJumpState == JumpState.GROUNDED && mWalkState != WalkState.WALKING) {
            mWalkStartTime = TimeUtils.nanoTime();
        }

        mWalkState = WalkState.WALKING;
        mFacing = Direction.LEFT;
        mPosition.x -= delta * Constants.GIGAGAL_MOVE_SPEED;
    }

    private void moveRight(float delta) {
        if (mJumpState == JumpState.GROUNDED && mWalkState != WalkState.WALKING) {
            mWalkStartTime = TimeUtils.nanoTime();
        }

        mWalkState = WalkState.WALKING;
        mFacing = Direction.RIGHT;
        mPosition.x += delta * Constants.GIGAGAL_MOVE_SPEED;
    }

    private void startJump() {
        mJumpState = JumpState.JUMPING;
        mJumpStartTime = TimeUtils.nanoTime();
        continueJump();
    }

    private void continueJump() {
        if (mJumpState == JumpState.JUMPING) {
            if (Utils.secondsSince(mJumpStartTime) < Constants.GIGAGAL_MAX_JUMP_DURATION) {
                mVelocity.y = Constants.GIGAGAL_JUMP_SPEED;
            } else {
                endJump();
            }
        }
    }

    private void endJump() {
        if (mJumpState == JumpState.JUMPING) {
            mJumpState = JumpState.FALLING;
        }
    }

    private void recoilFromEnemy(Direction direction) {
        mJumpState = JumpState.RECOILING;
        mVelocity.y = Constants.GIGAGAL_KNOCKBACK_VELOCITY.y;
        if (direction == Direction.LEFT) {
            mVelocity.x = -Constants.GIGAGAL_KNOCKBACK_VELOCITY.x;
        } else {
            mVelocity.x = Constants.GIGAGAL_KNOCKBACK_VELOCITY.x;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion region = Assets.instance.gigaGalAssets.standingRight;

        if (mFacing == Direction.RIGHT && mJumpState != JumpState.GROUNDED) {
            region = Assets.instance.gigaGalAssets.jumpingRight;
        } else if (mFacing == Direction.RIGHT && mWalkState == WalkState.NOT_WALKING) {
            region = Assets.instance.gigaGalAssets.standingRight;
        } else if (mFacing == Direction.RIGHT && mWalkState == WalkState.WALKING) {
            float walkTimeSeconds = Utils.secondsSince(mWalkStartTime);
            region = (TextureRegion) Assets.instance.gigaGalAssets.walkingRightAnimation.getKeyFrame(walkTimeSeconds);
        } else if (mFacing == Direction.LEFT && mJumpState != JumpState.GROUNDED) {
            region = Assets.instance.gigaGalAssets.jumpingLeft;
        } else if (mFacing == Direction.LEFT && mWalkState == WalkState.NOT_WALKING) {
            region = Assets.instance.gigaGalAssets.standingLeft;
        } else if (mFacing == Direction.LEFT && mWalkState == WalkState.WALKING) {
            float walkTimeSeconds = Utils.secondsSince(mWalkStartTime);
            region = (TextureRegion) Assets.instance.gigaGalAssets.walkingLeftAnimation.getKeyFrame(walkTimeSeconds);
        }

        Utils.drawTextureRegion(batch, region,
                mPosition.x - Constants.GIGAGAL_EYE_POSITION.x,
                mPosition.y - Constants.GIGAGAL_EYE_POSITION.y
        );
    }

    public Vector2 getPosition() {
        return mPosition;
    }

    public int getAmmo() {
        return mAmmo;
    }

    public int getLives() {
        return mLives;
    }

    public void setJumpButtonPressed(boolean flag) {
        mJumpButtonPressed = flag;
    }

    public void setLeftButtonPressed(boolean flag) {
        mLeftButtonPressed = flag;
    }

    public void setRightButtonPressed(boolean flag) {
        mRightButtonPressed = flag;
    }

}
