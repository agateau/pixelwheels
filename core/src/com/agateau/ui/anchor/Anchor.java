/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui.anchor;

public class Anchor {
    public static final Anchor TOP_LEFT = new Anchor(0, 1);
    public static final Anchor TOP_CENTER = new Anchor(0.5f, 1);
    public static final Anchor TOP_RIGHT = new Anchor(1, 1);
    public static final Anchor CENTER_LEFT = new Anchor(0, 0.5f);
    public static final Anchor CENTER = new Anchor(0.5f, 0.5f);
    public static final Anchor CENTER_RIGHT = new Anchor(1, 0.5f);
    public static final Anchor BOTTOM_LEFT = new Anchor(0, 0);
    public static final Anchor BOTTOM_CENTER = new Anchor(0.5f, 0);
    public static final Anchor BOTTOM_RIGHT = new Anchor(1, 0);

    public float hPercent;
    public float vPercent;

    public Anchor(float hPercent, float vPercent) {
        this.hPercent = hPercent;
        this.vPercent = vPercent;
    }
}
