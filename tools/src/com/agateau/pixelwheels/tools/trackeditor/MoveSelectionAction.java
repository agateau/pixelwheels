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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class MoveSelectionAction extends EditorAction {
    private static final float MOVE_UNIT = 1;

    private final Vector2 mDelta = new Vector2();
    private final Array<Vector2> mPoints = new Array<>(/*ordered=*/ false, 2);

    public MoveSelectionAction(Editor editor, int dx, int dy) {
        super(editor);
        mPoints.clear();
        LapPositionTableIO.Line line = editor().lines().get(editor().currentLineIdx());
        if (editor.isP1Selected()) {
            mPoints.add(line.p1);
        }
        if (editor.isP2Selected()) {
            mPoints.add(line.p2);
        }
        mDelta.set(dx, dy).scl(MOVE_UNIT);
    }

    @Override
    public void undo() {
        for (Vector2 point : mPoints) {
            point.sub(mDelta);
        }
        editor().markNeedSave();
    }

    @Override
    public void redo() {
        for (Vector2 point : mPoints) {
            point.add(mDelta);
        }
        editor().markNeedSave();
    }

    @Override
    public boolean mergeWith(EditorAction otherAction) {
        if (!(otherAction instanceof MoveSelectionAction)) {
            return false;
        }
        MoveSelectionAction other = (MoveSelectionAction) otherAction;
        if (mPoints.size != other.mPoints.size) {
            return false;
        }
        // Use identity because we want to know if the action affects the same line ends, not if
        // the line ends are at the same position (they are not)
        if (!mPoints.containsAll(other.mPoints, /* identity=*/ true)) {
            return false;
        }
        mDelta.add(other.mDelta);
        return true;
    }
}
