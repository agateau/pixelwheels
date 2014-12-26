package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class InputHudIndicator extends Actor {
    private final TextureRegion mBg;
    private final TextureRegion mIcon;
    private final Color mColor;

    public InputHudIndicator(TextureRegion bg, TextureRegion icon, boolean even) {
        mBg = bg;
        mIcon = icon;
        mColor = new Color(0.9f, 0.9f, 1, even ? 0.5f : 0.7f);
        setWidth(20);
        setHeight(60);
    }
    @Override
    public void draw(Batch batch, float alpha) {
        batch.setColor(mColor);
        batch.draw(mBg, getX(), getY(), getWidth(), getHeight());
        batch.draw(
                mIcon,
                MathUtils.round(getX() + (getWidth() - mIcon.getRegionWidth()) / 2),
                MathUtils.round(getY() + (getHeight() - mIcon.getRegionHeight()) / 2)
        );
        batch.setColor(Color.WHITE);
    }
}
