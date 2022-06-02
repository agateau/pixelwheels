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

/** Represents an action modifying the track */
public abstract class EditorAction {
    private final Editor mEditor;

    public EditorAction(Editor editor) {
        mEditor = editor;
    }

    public Editor editor() {
        return mEditor;
    }

    public abstract void undo();

    public abstract void redo();

    /**
     * Returns true if @p other can be merged into this, instead of being added to the undo stack
     */
    public abstract boolean mergeWith(EditorAction other);
}
