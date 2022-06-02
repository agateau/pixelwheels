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
package com.agateau.pixelwheels.tools.trackeditor;

import com.agateau.pixelwheels.map.LapPositionTableIO;
import com.badlogic.gdx.utils.Array;

public interface Editor {
    void markNeedSave();

    Array<LapPositionTableIO.Line> lines();

    int currentLineIdx();

    void setCurrentLineIdx(int idx);

    boolean isP1Selected();

    boolean isP2Selected();

    void setP1Selected(boolean selected);

    void setP2Selected(boolean selected);
}
