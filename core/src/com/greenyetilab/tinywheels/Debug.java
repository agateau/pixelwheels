package com.greenyetilab.tinywheels;

/**
 * Debug options
 */
public class Debug {
    public boolean showDebugHud = false;
    public boolean showDebugLayer = false;
    public boolean drawVelocities = false;
    public boolean drawTileCorners = false;
    public boolean showHudDebugLines = false;

    public final static Debug instance = new Debug();
}
