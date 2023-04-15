/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/** Controller managing the count-down label */
public class CountDownHudController {
    private final GameWorld mGameWorld;
    private final Hud mHud;

    private final Label mCountDownLabel;

    public CountDownHudController(Assets assets, GameWorld gameWorld, Hud hud) {
        mGameWorld = gameWorld;
        mHud = hud;
        Skin skin = assets.ui.skin;

        AnchorGroup root = hud.getRoot();

        mCountDownLabel = new Label("", skin, "hudCountDown");
        mCountDownLabel.setAlignment(Align.bottom);

        root.addPositionRule(mCountDownLabel, Anchor.BOTTOM_CENTER, root, Anchor.CENTER);
    }

    public Hud getHud() {
        return mHud;
    }

    @SuppressWarnings("UnusedParameters")
    public void act(float delta) {
        CountDown countDown = mGameWorld.getCountDown();
        if (countDown.isFinished()) {
            mCountDownLabel.setVisible(false);
            return;
        }
        float alpha = countDown.getPercent();
        int count = countDown.getValue();

        mCountDownLabel.setColor(1, 1, 1, alpha);

        String text = count > 0 ? String.valueOf(count) : "GO!";
        mCountDownLabel.setText(text);
    }
}
