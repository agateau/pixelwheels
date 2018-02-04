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
package com.agateau.tinywheels;

import com.agateau.tinywheels.racer.LapPositionComponent;
import com.agateau.tinywheels.racer.Racer;
import com.agateau.ui.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Display the high score table
 */
public class RacerListPane extends ScrollPane {
    public RacerListPane() {
        super(null);
    }

    public void init(Skin skin, Array<Racer> racers, Array<Racer> playerRacers) {
        Table table = new Table(skin);
        Set<Racer> playerSet = new HashSet<Racer>();
        for (Racer racer : playerRacers) {
            playerSet.add(racer);
        }
        addRow(table, "highScore", "#", "Racer", "Best Lap", "Total");
        for (int idx = 0; idx < racers.size; ++idx) {
            Racer racer = racers.get(idx);
            LapPositionComponent lapPositionComponent = racer.getLapPositionComponent();
            String style = playerSet.contains(racer) ? "newHighScore" : "highScore";
            addRow(table, style,
                    String.format(Locale.US, "%d.", idx + 1),
                    racer.getVehicle().getName(),
                    StringUtils.formatRaceTime(lapPositionComponent.getBestLapTime()),
                    StringUtils.formatRaceTime(lapPositionComponent.getTotalTime())
            );
        }
        setWidget(table);
    }

    public static void register(UiBuilder builder) {
        builder.registerActorFactory("RacerListPane", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new RacerListPane();
            }
        });
    }

    public static void addRow(Table table, String style, String v1, String v2, String v3, String v4) {
        table.add(v1, style).right().padRight(24);
        table.add(v2, style).left().expandX();
        table.add(v3, style).right().padRight(24);
        table.add(v4, style).right();
        table.row();
    }
}
