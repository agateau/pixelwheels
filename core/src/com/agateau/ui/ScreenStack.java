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
package com.agateau.ui;

import com.agateau.utils.Assert;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import java.util.Stack;

public class ScreenStack {
    private final Game mGame;
    private final Stack<Screen> mStack = new Stack<>();
    private Screen mBlockingScreen;

    public ScreenStack(Game game) {
        mGame = game;
    }

    public void push(Screen screen) {
        mStack.push(screen);
        setScreen(screen);
    }

    public void pop() {
        Assert.check(!mStack.isEmpty(), "mScreenStack is empty");
        mStack.pop().dispose();
        Assert.check(!mStack.isEmpty(), "mScreenStack is empty");
        setScreen(mStack.peek());
    }

    public void replace(Screen screen) {
        if (!mStack.isEmpty()) {
            mStack.pop().dispose();
        }
        push(screen);
    }

    public void clear() {
        while (!mStack.isEmpty()) {
            mStack.pop().dispose();
        }
    }

    /**
     * A blocking screen overrides the normal stack, once a blocking screen is shown, screens from
     * the stack won't be shown unless hideBlockingScreen() is called.
     */
    public void showBlockingScreen(Screen screen) {
        Assert.check(mBlockingScreen == null, "There is already a blocking screen");
        mBlockingScreen = screen;
        mGame.setScreen(mBlockingScreen);
    }

    public void hideBlockingScreen() {
        Assert.check(mBlockingScreen != null, "There is no blocking screen");
        mBlockingScreen.dispose();
        mBlockingScreen = null;
        mGame.setScreen(mStack.peek());
    }

    private void setScreen(Screen screen) {
        if (mBlockingScreen == null) {
            mGame.setScreen(screen);
        }
    }
}
