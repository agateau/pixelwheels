package com.greenyetilab.tinywheels;

import com.badlogic.gdx.utils.Array;

/**
 * Details about the game to start
 */
public class GameInfo {
    public static class PlayerInfo {
        String vehicleId;
    }
    public String mapName;
    public final Array<PlayerInfo> playerInfos = new Array<PlayerInfo>();

    public void addPlayerInfo(String vehicleId) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.vehicleId = vehicleId;
        playerInfos.add(playerInfo);
    }
}
