package com.greenyetilab.race;

/**
 * Represents a bonus on the screen hud
 */
public class BonusIndicator extends InputHudIndicator {
    private Bonus mBonus;

    public Bonus getBonus() {
        return mBonus;
    }

    public void setBonus(Bonus bonus) {
        mBonus = bonus;
        if (mBonus == null) {
            setIcon(null);
        } else {
            setIcon(mBonus.getIconRegion());
        }
    }
}
