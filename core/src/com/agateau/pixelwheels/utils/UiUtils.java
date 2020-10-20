package com.agateau.pixelwheels.utils;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.ui.uibuilder.UiBuilder;

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
}
