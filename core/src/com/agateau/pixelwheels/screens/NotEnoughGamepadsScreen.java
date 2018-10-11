package com.agateau.pixelwheels.screens;

import com.agateau.ui.UiAssets;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.IntArray;

public class NotEnoughGamepadsScreen extends PwStageScreen {
    public NotEnoughGamepadsScreen(UiAssets uiAssets) {
        super(uiAssets);
    }

    @Override
    public void onBackPressed() {

    }

    public void setMissingGamepads(IntArray missingGamepads) {
        for (int idx = 0; idx < missingGamepads.size; ++idx) {
            int playerId = missingGamepads.get(idx);
            NLog.d("missing gamepad for player %d", playerId + 1);
        }
    }
}
