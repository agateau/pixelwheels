/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

/** A scroll pane to show credits */
public class CreditsScrollPane extends ScrollPane {
    private static final float AUTO_SCROLL_PX_PER_S = 45;
    private final VerticalGroup mGroup;

    public CreditsScrollPane() {
        super(null);
        mGroup = new VerticalGroup();
        setActor(mGroup);
        setupAutoScroll();
    }

    public VerticalGroup getGroup() {
        return mGroup;
    }

    @Override
    protected void sizeChanged() {
        if (mGroup != null) {
            mGroup.setWidth(getWidth());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        UiInputMapper inputMapper = UiInputMapper.getInstance();
        if (inputMapper.isKeyJustPressed(VirtualKey.DOWN)) {
            scroll(1);
        } else if (inputMapper.isKeyJustPressed(VirtualKey.UP)) {
            scroll(-1);
        }
    }

    private void scroll(int dy) {
        float scrollAmount = getHeight() / 2;
        float y = MathUtils.clamp(getScrollY() + scrollAmount * dy, 0, getMaxY());
        setScrollY(y);
    }

    private void setupAutoScroll() {
        addAction(
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        float maxY = getMaxY();
                        float y = Math.min(getScrollY() + AUTO_SCROLL_PX_PER_S * delta, maxY);
                        setScrollY(y);
                        return false;
                    }
                });
    }
}
