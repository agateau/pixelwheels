package com.greenyetilab.race;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Super simple message bus system
 */
public class NMessageBus {
    private static final HashMap<String, Array<Handler>> mHandlerForChannel = new HashMap<String, Array<Handler>>();

    public interface Handler {
        public void handle(String channel, Object data);
    }

    public static void register(String channel, Handler handler) {
        Array<Handler> array = mHandlerForChannel.get(channel);
        if (array == null) {
            array = new Array<Handler>();
            mHandlerForChannel.put(channel, array);
        }
        array.add(handler);
    }

    public static void post(String channel, Object data) {
        Array<Handler> array = mHandlerForChannel.get(channel);
        if (array == null) {
            return;
        }
        for (Handler handler: array) {
            handler.handle(channel, data);
        }
    }
}
