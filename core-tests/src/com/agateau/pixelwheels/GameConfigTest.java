/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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
package com.agateau.pixelwheels;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.utils.TestGdxPreferences;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GameConfigTest {

    @Test
    public void setupInputHandlers_DesktopBlankState() {
        // GIVEN a device without touchscreen
        Input input = mock(Input.class);
        when(input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)).thenReturn(false);
        Gdx.input = input;

        Gdx.app = mock(Application.class);

        // WHEN creating the default config
        GameConfig config = new GameConfig(new TestGdxPreferences());

        // THEN handlers for the players are the 4 keyboard handlers
        Array<GameInputHandler> keyboardHandlers =
                GameInputHandlerFactories.getFactoryById("keyboard").getAllHandlers();

        for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
            GameInputHandler handler = config.getPlayerInputHandler(idx);
            GameInputHandler keyboardHandler = keyboardHandlers.get(idx);
            assertThat("idx=" + idx, handler, is(keyboardHandler));
        }
    }

    @Test
    public void setupInputHandlers_PhoneBlankState() {
        // GIVEN a device with a touchscreen
        Input input = mock(Input.class);
        when(input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)).thenReturn(true);
        Gdx.input = input;

        Gdx.app = mock(Application.class);

        // WHEN creating the default config
        GameConfig config = new GameConfig(new TestGdxPreferences());

        // THEN the input handler for player 1 is the PieTouchHandler
        GameInputHandler pieTouchHandler =
                GameInputHandlerFactories.getFactoryById("pie").getAllHandlers().first();

        GameInputHandler p1Handler = config.getPlayerInputHandler(0);
        assertThat(p1Handler, is(pieTouchHandler));
    }
}
