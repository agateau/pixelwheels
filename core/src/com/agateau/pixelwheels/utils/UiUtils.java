package com.agateau.pixelwheels.utils;

import com.agateau.pixelwheels.gamesetup.GameInfo;

public class UiUtils {
    public static String getEntrantRowStyle(GameInfo.Entrant entrant) {
        if (entrant instanceof GameInfo.Player) {
            int index = ((GameInfo.Player)entrant).getIndex();
            return "player" + String.valueOf(index) + "ScoreRow";
        } else {
            return "scoreRow";
        }
    }
}
