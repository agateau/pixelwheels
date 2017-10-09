/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Super simple message bus system
 */
public class NMessageBus {
    private final HashMap<String, Array<Handler>> mHandlerForChannel = new HashMap<String, Array<Handler>>();

    public interface Handler {
        public void handle(String channel, Object data);
    }

    public void register(String channel, Handler handler) {
        Array<Handler> array = mHandlerForChannel.get(channel);
        if (array == null) {
            array = new Array<Handler>();
            mHandlerForChannel.put(channel, array);
        }
        array.add(handler);
    }

    public void post(String channel) {
        post(channel, null);
    }

    public void post(String channel, Object data) {
        Array<Handler> array = mHandlerForChannel.get(channel);
        if (array == null) {
            return;
        }
        for (Handler handler: array) {
            handler.handle(channel, data);
        }
    }
}
