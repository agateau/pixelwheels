package com.greenyetilab.tinywheels;

import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.greenyetilab.utils.StageScreen;

/**
 * A stage screen using the correct size for Tiny Wheels
 */
public class TwStageScreen extends StageScreen {
    public TwStageScreen() {
        super(new ScalingViewport(Scaling.fit, 800, 480));
    }
}
