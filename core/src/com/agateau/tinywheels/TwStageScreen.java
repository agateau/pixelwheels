package com.agateau.tinywheels;

import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.agateau.ui.StageScreen;

/**
 * A stage screen using the correct size for Tiny Wheels
 */
public class TwStageScreen extends StageScreen {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public TwStageScreen() {
        super(new ScalingViewport(Scaling.fit, WIDTH, HEIGHT));
    }
}
