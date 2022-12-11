/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/** Helper class to save and restore the coordinates of the GL viewport */
public class GLViewportSaver {
    private final IntBuffer mBuffer;

    public GLViewportSaver() {
        mBuffer = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    public void save() {
        Gdx.gl20.glGetIntegerv(GL20.GL_VIEWPORT, mBuffer);
    }

    public void restore() {
        Gdx.gl20.glViewport(mBuffer.get(0), mBuffer.get(1), mBuffer.get(2), mBuffer.get(3));
    }
}
