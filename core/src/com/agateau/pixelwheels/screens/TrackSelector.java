/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/** A menu item to select a track */
public class TrackSelector extends GridMenuItem<Track> {
    private Assets mAssets;
    private RewardManager mRewardManager;

    private class Renderer extends TextureRegionItemRendererAdapter<Track> {
        @Override
        protected TextureRegion getItemRegion(Track track) {
            return isItemEnabled(track)
                    ? mAssets.getTrackRegion(track)
                    : mAssets.getLockedTrackRegion();
        }

        @Override
        public boolean isItemEnabled(Track track) {
            return mRewardManager.isTrackUnlocked(track);
        }
    }

    public TrackSelector(Menu menu) {
        super(menu);
    }

    public void init(Assets assets, RewardManager rewardManager) {
        mAssets = assets;
        mRewardManager = rewardManager;
        setItemSize(160, 160);
        setItemRenderer(new Renderer());
        Array<Track> tracks = new Array<>();
        for (Championship championship : mAssets.championships) {
            tracks.addAll(championship.getTracks());
        }
        setItems(tracks);
    }
}
