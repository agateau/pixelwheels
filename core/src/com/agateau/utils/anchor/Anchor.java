package com.agateau.utils.anchor;

public class Anchor {
    public static final Anchor TOP_LEFT = new Anchor(0, 1);
    public static final Anchor TOP_CENTER = new Anchor(0.5f, 1);
    public static final Anchor TOP_RIGHT = new Anchor(1, 1);
    public static final Anchor CENTER_LEFT = new Anchor(0, 0.5f);
    public static final Anchor CENTER = new Anchor(0.5f, 0.5f);
    public static final Anchor CENTER_RIGHT = new Anchor(1, 0.5f);
    public static final Anchor BOTTOM_LEFT = new Anchor(0, 0);
    public static final Anchor BOTTOM_CENTER = new Anchor(0.5f, 0);
    public static final Anchor BOTTOM_RIGHT = new Anchor(1, 0);

    public float hPercent;
    public float vPercent;

    public Anchor(float hPercent, float vPercent) {
        this.hPercent = hPercent;
        this.vPercent = vPercent;
    }
}
