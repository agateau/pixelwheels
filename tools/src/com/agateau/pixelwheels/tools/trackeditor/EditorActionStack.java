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

import com.badlogic.gdx.utils.Array;

class EditorActionStack {
    private final Array<EditorAction> mActions = new Array<>();

    public void add(EditorAction action) {
        action.redo();
        if (!mActions.isEmpty()) {
            EditorAction lastAction = mActions.get(mActions.size - 1);
            if (lastAction.mergeWith(action)) {
                return;
            }
        }
        mActions.add(action);
    }

    public void undo() {
        if (mActions.isEmpty()) {
            return;
        }
        EditorAction action = mActions.pop();
        action.undo();
    }
}
