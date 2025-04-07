/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;

/** Utility methods to deal with the platform */
public class PlatformUtils {
    public interface Impl {
        void openURI(String uri);
    }

    private static class DefaultImpl implements Impl {
        @Override
        public void openURI(String uri) {
            Gdx.net.openURI(uri);
        }
    }

    private enum UiType {
        BUTTONS,
        TOUCH
    }

    private static UiType sUiType;
    private static Impl sImpl = new DefaultImpl();

    public static void setup(Impl impl) {
        sImpl = impl;
    }

    public static boolean isTouchUi() {
        init();
        return sUiType == UiType.TOUCH;
    }

    public static boolean isDesktop() {
        switch (Gdx.app.getType()) {
            case Desktop:
            case HeadlessDesktop:
            case Applet:
            case WebGL:
                return true;
            default:
                return false;
        }
    }

    /** An implementation of Gdx.net.openURI which works on Linux */
    public static void openURI(String uri) {
        sImpl.openURI(uri);
    }

    private static void init() {
        if (sUiType != null) {
            return;
        }
        String envValue = System.getenv("AGC_UI_TYPE");
        if (envValue == null) {
            sUiType = isDesktop() ? UiType.BUTTONS : UiType.TOUCH;
        } else {
            sUiType = UiType.valueOf(envValue);
            NLog.d("Forcing UI type to %s", sUiType);
        }
        NLog.i("UI type: %s", sUiType);
    }
}
