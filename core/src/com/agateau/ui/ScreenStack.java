/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui;

import com.agateau.utils.Assert;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.Stack;

public class ScreenStack {
    private final Game mGame;
    private final Stack<Screen> mStack = new Stack<Screen>();
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
     * A blocking screen override the normal stack, once a blocking screen is shown,
     * screens from the stack won't be shown unless hideBlockingScreen() is called.
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
