/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A fake actor, which can be added to a stage to get event-based access to key press events.
 *
 * <p>Add it to the stage and override onKeyJustPressed()
 */
public class UiInputActor extends Actor {
    @Override
    public void act(float delta) {
        UiInputMapper mapper = UiInputMapper.getInstance();
        for (VirtualKey key : VirtualKey.values()) {
            if (mapper.isKeyJustPressed(key)) {
                onKeyJustPressed(key);
            }
        }
    }

    public void onKeyJustPressed(VirtualKey key) {}
}
