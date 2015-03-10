package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class InputHudIndicator extends Actor {
    private final TextureRegion mIcon;

    public InputHudIndicator(TextureRegion icon) {
        mIcon = icon;
        setWidth(20);
        setHeight(120);
    }
    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(
                mIcon,
                MathUtils.round(getX() + (getWidth() - mIcon.getRegionWidth()) / 2),
                MathUtils.round(getY() + (getHeight() - mIcon.getRegionHeight()) / 2)
        );
    }
}
