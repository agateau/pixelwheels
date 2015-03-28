package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Represents a bonus on the screen hud
 */
public class BonusIndicator extends Actor {
    private Bonus mBonus;

    public BonusIndicator() {
        setWidth(100);
        setHeight(100);
    }

    public Bonus getBonus() {
        return mBonus;
    }

    public void setBonus(Bonus bonus) {
        mBonus = bonus;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        if (mBonus == null) {
            return;
        }
        TextureRegion region = mBonus.getIconRegion();
        float x = getX() + (getWidth() - region.getRegionWidth()) / 2;
        float y = getY() + (getHeight() - region.getRegionHeight()) / 2;
        batch.draw(mBonus.getIconRegion(), MathUtils.round(x), MathUtils.round(y));
    }
}
