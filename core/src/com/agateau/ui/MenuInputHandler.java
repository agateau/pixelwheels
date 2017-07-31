package com.agateau.ui;

/**
 * Monitor input events for the menu
 */
public class MenuInputHandler {
    private static final float REPEAT_DELAY = 0.6f;
    private static final float REPEAT_RATE = 0.025f;
    enum State {
        STARTING,
        NORMAL,
        KEY_DOWN,
        REPEATING
    }
    private KeyMapper mKeyMapper = new KeyMapper();
    private State mState = State.STARTING;

    private VirtualKey mPressedVirtualKey = null;
    private VirtualKey mJustPressedVirtualKey = null;
    private float mRepeatDelay = 0;

    public boolean isPressed(VirtualKey vkey) {
        return mPressedVirtualKey == vkey && mRepeatDelay < 0;
    }

    public boolean isJustPressed(VirtualKey vkey) {
        return mJustPressedVirtualKey == vkey;
    }

    public void act(float delta) {
        if (mState == State.STARTING) {
            // If a key is already down at startup, ignore it. If no key is down, go to NORMAL state
            if (findPressedKey() == null) {
                mState = State.NORMAL;
            }
        } else if (mState == State.NORMAL) {
            // Not repeating yet
            mJustPressedVirtualKey = null;
            VirtualKey virtualKey = findPressedKey();
            if (virtualKey != null) {
                mPressedVirtualKey = virtualKey;
                // Set delay to -1 so that next call to isPressed() returns true
                mRepeatDelay = -1;
                mState = State.KEY_DOWN;
            }
        } else {
            // Repeating
            if (mKeyMapper.isKeyPressed(mPressedVirtualKey)) {
                if (mRepeatDelay > 0) {
                    mRepeatDelay -= delta;
                } else {
                    if (mState == State.KEY_DOWN) {
                        mRepeatDelay = REPEAT_DELAY;
                        mState = State.REPEATING;
                    } else {
                        mRepeatDelay = REPEAT_RATE;
                    }
                }
            } else {
                // Key has been released, not repeating anymore
                mState = State.NORMAL;
                mJustPressedVirtualKey = mPressedVirtualKey;
            }
        }
    }

    public void setKeyMapper(KeyMapper keyMapper) {
        mKeyMapper = keyMapper;
    }

    private VirtualKey findPressedKey() {
        for (VirtualKey virtualKey : VirtualKey.values()) {
            if (mKeyMapper.isKeyPressed(virtualKey)) {
                return virtualKey;
            }
        }
        return null;
    }
}
