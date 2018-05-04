/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.racer.LapPositionComponent;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

/**
 * Appears on top of RaceScreen at the end of the race
 */
public class FinishedOverlay extends Overlay {
    private final PwGame mGame;
    private final RaceScreen.Listener mListener;

    public FinishedOverlay(PwGame game, RaceScreen.Listener listener, final Array<Racer> racers) {
        super(game.getAssets().dot);
        mGame = game;
        mListener = listener;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent(racers));
            }
        };
        setContent(createContent(racers));
    }

    private Actor createContent(Array<Racer> racers) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        ScrollableTable.register(builder, "ScrollableTable", new ScrollableTable.CellCreator() {
            @Override
            public void createCells(Table table, String style, String... values) {
                table.add(values[0], style).right().padRight(24);
                table.add(values[1], style).left().expandX();
                table.add(values[2], style).right().padRight(24);
                table.add(values[3], style).right().padRight(24);
                table.add(values[4], style).right();
            }
        });
        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
        Menu menu = builder.getActor("menu");
        menu.addButton("OK").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mListener.onNextTrackPressed();
            }
        });

        ScrollableTable scrollableTable = builder.getActor("scrollableTable");
        scrollableTable.addHeaderRow("#", "Racer", "Best Lap", "Total", "Score");
        for (int idx = 0; idx < racers.size; ++idx) {
            Racer racer = racers.get(idx);
            String style = UiUtils.getEntrantRowStyle(racer.getEntrant());
            LapPositionComponent lapPositionComponent = racer.getLapPositionComponent();
            scrollableTable.setRowStyle(style);
            scrollableTable.addRow(
                    String.format(Locale.US, "%d.", idx + 1),
                    racer.getVehicle().getName(),
                    StringUtils.formatRaceTime(lapPositionComponent.getBestLapTime()),
                    StringUtils.formatRaceTime(lapPositionComponent.getTotalTime()),
                    String.valueOf(racer.getEntrant().getScore())
            );
        }
        return content;
    }
}
