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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class InsertSectionAction extends EditorAction {
    private static final Vector2 NEW_DELTA = new Vector2(12, 12);

    int mInsertedLineIdx = -1;

    public InsertSectionAction(Editor editor) {
        super(editor);
    }

    @Override
    public void undo() {
        editor().setCurrentLineIdx(mInsertedLineIdx - 1);
        editor().lines().removeIndex(mInsertedLineIdx);
        editor().markNeedSave();
    }

    @Override
    public void redo() {
        Array<LapPositionTableIO.Line> lines = editor().lines();
        int currentLineIdx = editor().currentLineIdx();
        LapPositionTableIO.Line current = lines.get(currentLineIdx);
        if (currentLineIdx == lines.size - 1) {
            LapPositionTableIO.Line newLine = new LapPositionTableIO.Line();
            newLine.p1.set(current.p1).add(NEW_DELTA);
            newLine.p2.set(current.p2).add(NEW_DELTA);
            newLine.order = current.order + 1;
            lines.add(newLine);
        } else {
            LapPositionTableIO.Line next = lines.get(currentLineIdx + 1);
            LapPositionTableIO.Line newLine = new LapPositionTableIO.Line();
            newLine.p1.set(current.p1).lerp(next.p1, 0.5f);
            newLine.p2.set(current.p2).lerp(next.p2, 0.5f);
            newLine.order = MathUtils.lerp(current.order, next.order, 0.5f);
            lines.insert(currentLineIdx + 1, newLine);
        }
        mInsertedLineIdx = currentLineIdx + 1;
        editor().setCurrentLineIdx(mInsertedLineIdx);
        editor().markNeedSave();
    }

    @Override
    public boolean mergeWith(EditorAction other) {
        return false;
    }
}
