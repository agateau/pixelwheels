package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.UiBuilder;

/**
 * Display the high score table
 */
public class RacerListPane extends ScrollPane {
    public RacerListPane() {
        super(null);
    }

    public void init(Skin skin, Array<Racer> racers, Racer playerRacer) {
        Table table = new Table(skin);
        for (int idx = 0; idx < racers.size; ++idx) {
            Racer racer = racers.get(idx);
            String style = racer == playerRacer ? "newHighScore" : "highScore";
            String name = racer == playerRacer ? "You" : "CPU";
            table.add(String.format("%d.", idx + 1), style).right();
            table.add(name, style).expandX().right();
            table.row();
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
}
