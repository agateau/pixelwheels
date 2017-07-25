package com.agateau.tinywheels;

import com.agateau.ui.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashSet;
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
                    String.format("%d.", idx + 1),
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
