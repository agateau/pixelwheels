package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Indicate an input zone on the hud
 */
public class HudButton extends Actor {
    private final static float INDICATOR_OPACITY = 0.6f;
    private final static float INDICATOR_SIZE_CM = 1.5f;

    private final TextureRegion[] mRegions = new TextureRegion[2];

    private boolean mIsPressed = false;
    private TextureRegion mIcon = null;

    /**
     * name is a string like "left" or "right"
     */
    public HudButton(Assets assets, String name) {
        mRegions[0] = assets.findRegion("hud-" + name);
        mRegions[1] = assets.findRegion("hud-" + name + "-down");
    }

    @Override
    public void act(float dt) {
        float ppc = (Gdx.graphics.getPpcX() + Gdx.graphics.getPpcY()) / 2;
        float pxSize = INDICATOR_SIZE_CM * ppc;
        float stageSize = pxSize * getStage().getWidth() / Gdx.graphics.getWidth();

        setWidth(stageSize);
        setHeight(stageSize);
    }

    void setPressed(boolean isPressed) {
        mIsPressed = isPressed;
    }

    void setIcon(TextureRegion icon) {
        mIcon = icon;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        Color color = batch.getColor();
        float oldA = color.a;
        color.a = alpha * INDICATOR_OPACITY;
        batch.setColor(color);

        float zoom = MathUtils.floor(Math.min(getWidth() / mRegions[0].getRegionWidth(), getHeight() / mRegions[0].getRegionHeight()));

        drawScaledRegion(batch, mRegions[mIsPressed ? 1 : 0], zoom, 0);
        if (mIcon != null) {
            drawScaledRegion(batch, mIcon, zoom, mIsPressed ? 0f : 2f);
        }

        color.a = oldA;
        batch.setColor(color);
    }

    private void drawScaledRegion(Batch batch, TextureRegion region, float zoom, float vMargin) {
        float w = region.getRegionWidth() * zoom;
        float h = region.getRegionHeight() * zoom;
        batch.draw(
                region,
                MathUtils.round(getX() + (getWidth() - w) / 2),
                MathUtils.round(getY() + (getHeight() - h) / 2) + vMargin * zoom,
                w, h
        );
    }
}
