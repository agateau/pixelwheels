package com.greenyetilab.tinywheels;

import com.badlogic.gdx.utils.Array;

/**
 * Details about the game to start
 */
public class GameInfo {
    public static class PlayerInfo {
        String vehicleId;
        GameInputHandler inputHandler;
    }
    public String mapName;
    public final Array<PlayerInfo> playerInfos = new Array<PlayerInfo>();

    public void addPlayerInfo(String vehicleId) {
        String inputHandlerId = TheGame.getPreferences().getString(PrefConstants.INPUT, PrefConstants.INPUT_DEFAULT);
        GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(inputHandlerId);
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.vehicleId = vehicleId;
        playerInfo.inputHandler = factory.create();
        playerInfos.add(playerInfo);
    }
}
