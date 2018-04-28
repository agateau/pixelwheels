package com.agateau.tinywheels.utils;

import com.agateau.tinywheels.gamesetup.GameInfo;

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
