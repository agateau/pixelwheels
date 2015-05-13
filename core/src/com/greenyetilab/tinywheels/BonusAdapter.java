package com.greenyetilab.tinywheels;

/**
 * An adapter for the Bonus class
 */
public abstract class BonusAdapter implements Bonus {
    protected Racer mRacer;

    @Override
    public void onPicked(Racer racer) {
        mRacer = racer;
    }

    @Override
    public void trigger() {

    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void aiAct(float delta) {

    }
}
