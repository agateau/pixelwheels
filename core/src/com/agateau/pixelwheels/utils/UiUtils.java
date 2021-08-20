package com.agateau.pixelwheels.utils;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.ui.uibuilder.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class UiUtils {
    public static String getEntrantRowStyle(GameInfo.Entrant entrant) {
        if (entrant.isPlayer()) {
            int index = ((GameInfo.Player) entrant).getIndex();
            return "player" + index + "RankingRow";
        } else {
            return "aiRankingRow";
        }
    }

    public static UiBuilder createUiBuilder(Assets assets) {
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);
        builder.addAtlas("ui", assets.ui.atlas);
        return builder;
    }

    public static void dumpStage(StringBuilder builder, Stage stage) {
        dumpActorChildren(builder, stage.getRoot(), 0);
    }

    private static void dumpActorChildren(StringBuilder builder, Group parent, int indent) {
        for (Actor actor : parent.getChildren()) {
            for (int idx = 0; idx < indent; idx++) {
                builder.append("  ");
            }
            dumpActor(builder, actor);
            builder.append('\n');
            if (actor instanceof Group) {
                dumpActorChildren(builder, (Group) actor, indent + 1);
            }
        }
    }

    private static void dumpActor(StringBuilder builder, Actor actor) {
        builder.append(
                StringUtils.format(
                        "%s x=%d y=%d w=%d h=%d",
                        getActorClassName(actor),
                        (int) actor.getX(),
                        (int) actor.getHeight(),
                        (int) actor.getWidth(),
                        (int) actor.getHeight()));
        String name = actor.getName();
        if (name != null) {
            builder.append(" name=");
            builder.append(name);
        }
    }

    /**
     * A variant of getClass().getSimpleName() which does not return an empty string for anonymous
     * classes
     */
    private static String getActorClassName(Actor actor) {
        String name = actor.getClass().getName();
        int dot = name.lastIndexOf('.');
        if (dot > -1) {
            return name.substring(dot + 1);
        } else {
            return name;
        }
    }
}
