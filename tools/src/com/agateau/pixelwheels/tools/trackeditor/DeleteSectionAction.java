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

class DeleteSectionAction extends EditorAction {
    private int mRemovedLineIdx;
    private LapPositionTableIO.Line mRemovedLine;

    public DeleteSectionAction(Editor editor) {
        super(editor);
    }

    @Override
    public void undo() {
        editor().lines().insert(mRemovedLineIdx, mRemovedLine);
        editor().setCurrentLineIdx(mRemovedLineIdx);
        editor().markNeedSave();
    }

    @Override
    public void redo() {
        mRemovedLineIdx = editor().currentLineIdx();
        mRemovedLine = editor().lines().removeIndex(mRemovedLineIdx);
        if (editor().currentLineIdx() == editor().lines().size) {
            editor().setCurrentLineIdx(editor().currentLineIdx() - 1);
        }
        editor().markNeedSave();
    }

    @Override
    public boolean mergeWith(EditorAction other) {
        return false;
    }
}
