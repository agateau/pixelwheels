package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class InputHudIndicator extends Actor {
    private final static float INDICATOR_OPACITY = 0.6f;
    private final static float ICON_ZOOM = 2f;

    private final TextureRegion[] mIcons = new TextureRegion[2];

    private boolean mIsPressed = false;

    /**
     * name is a string like "left" or "right"
     */
    public InputHudIndicator(Assets assets, String name) {
        mIcons[0] = assets.findRegion("hud-" + name);
        mIcons[1] = assets.findRegion("hud-" + name + "-down");
        setWidth(GamePlay.instance.hudButtonSize);
        setHeight(GamePlay.instance.hudButtonSize);
    }

    void setPressed(boolean isPressed) {
        mIsPressed = isPressed;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        TextureRegion icon = mIcons[mIsPressed ? 1 : 0];
        Color color = batch.getColor();

        float w = icon.getRegionWidth();
        float h = icon.getRegionHeight();
        float oldA = color.a;
        color.a = alpha * INDICATOR_OPACITY;
        batch.setColor(color);
        batch.draw(
                icon,
                MathUtils.round(getX() + (getWidth() - ICON_ZOOM * w) / 2),
                MathUtils.round(getY() + (getHeight() - ICON_ZOOM * h) / 2),
                0, 0,
                w, h,
                ICON_ZOOM, ICON_ZOOM,
                0 // rotation
        );
        color.a = oldA;
        batch.setColor(color);
    }
}
