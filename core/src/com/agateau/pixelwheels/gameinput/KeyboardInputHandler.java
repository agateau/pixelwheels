/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.gameinput;

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.Constants;
import com.agateau.ui.KeyMapper;
import com.badlogic.gdx.utils.Array;

/** Handle keyboard input, for desktop mode */
public class KeyboardInputHandler extends InputMapperInputHandler {
    public static class Factory implements GameInputHandlerFactory {
        final Array<GameInputHandler> mHandlers = new Array<>();

        Factory() {
            for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
                KeyMapper keyMapper = KeyMapper.createGameInstance(idx);
                mHandlers.add(new KeyboardInputHandler(keyMapper));
            }
        }

        @Override
        public String getId() {
            return "keyboard";
        }

        @Override
        public String getName() {
            return tr("Keyboard");
        }

        @Override
        public Array<GameInputHandler> getAllHandlers() {
            return mHandlers;
        }
    }

    KeyboardInputHandler(KeyMapper keyMapper) {
        super(keyMapper);
    }

    public KeyMapper getKeyMapper() {
        return (KeyMapper) getInputMapper();
    }
}
