package com.greenyetilab.tinywheels;

/**
 * Created by aurelien on 19/12/14.
 */
public class HealthComponent {
    public static final float DYING_DURATION = 0.3f;

    private int mOldHealth = 1; // Used to detect health decrease in act()
    private int mHealth = 1;
    private int mMaxHealth = 1;
    private float mKilledTime = 0;

    boolean act(float dt) {
        if (mOldHealth > mHealth) {
            mOldHealth = mHealth;
            onHealthDecreased();
        }
        if (mHealth == 0) {
            if (mKilledTime == 0) {
                onJustDied();
            }
            mKilledTime += dt;
            if (mKilledTime >= DYING_DURATION) {
                onFullyDead();
                return false;
            }
        }
        return true;
    }

    public State getState() {
        if (mHealth > 0) {
            return State.ALIVE;
        }
        return mKilledTime < DYING_DURATION ? State.DYING : State.DEAD;
    }

    public float getKilledTime() {
        return mKilledTime;
    }

    public int getHealth() {
        return mHealth;
    }

    public int getMaxHealth() {
        return mMaxHealth;
    }

    public void setInitialHealth(int health) {
        mOldHealth = health;
        mHealth = health;
        mMaxHealth = health;
    }

    public void decreaseHealth() {
        if (mHealth == 0) {
            return;
        }
        mHealth--;
    }

    public void kill() {
        mHealth = 0;
    }

    protected void onHealthDecreased() {
    }

    protected void onJustDied() {
    }

    protected void onFullyDead() {
    }

    public static enum State {
        ALIVE,
        DYING,
        DEAD
    }
}
