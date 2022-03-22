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
    int mNextIndex = 0;

    public void add(EditorAction action) {
        action.redo();
        if (!mActions.isEmpty()) {
            EditorAction lastAction = mActions.get(mActions.size - 1);
            if (lastAction.mergeWith(action)) {
                return;
            }
        }
        if (mNextIndex < mActions.size) {
            mActions.removeRange(mNextIndex, mActions.size - 1);
        }
        mActions.add(action);
        ++mNextIndex;
    }

    public void undo() {
        if (mNextIndex == 0) {
            return;
        }
        --mNextIndex;
        mActions.get(mNextIndex).undo();
    }

    public void redo() {
        if (mNextIndex == mActions.size) {
            return;
        }
        mActions.get(mNextIndex).redo();
        mNextIndex++;
    }
}
