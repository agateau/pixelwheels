package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class InputHudIndicator extends Actor {
    private TextureRegion mIcon;

    public InputHudIndicator() {
        this(null);
    }

    public InputHudIndicator(TextureRegion icon) {
        mIcon = icon;
        setWidth(GamePlay.instance.hudButtonSize);
        setHeight(GamePlay.instance.hudButtonSize);
    }

    void setIcon(TextureRegion region) {
        mIcon = region;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (mIcon == null) {
            return;
        }
        batch.draw(
                mIcon,
                MathUtils.round(getX() + (getWidth() - mIcon.getRegionWidth()) / 2),
                MathUtils.round(getY() + (getHeight() - mIcon.getRegionHeight()) / 2)
        );
    }
}
