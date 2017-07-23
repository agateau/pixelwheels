package com.agateau.tinywheels;

import com.badlogic.gdx.utils.Array;

/**
 * Details about the game to start
 */
public class GameInfo {
    public static class PlayerInfo {
        String vehicleId;
        GameInputHandler inputHandler;
    }
    public MapInfo mapInfo;
    public final Array<PlayerInfo> playerInfos = new Array<PlayerInfo>();

    public void addPlayerInfo(String vehicleId, GameInputHandler inputHandler) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.vehicleId = vehicleId;
        playerInfo.inputHandler = inputHandler;
        playerInfos.add(playerInfo);
    }
}
