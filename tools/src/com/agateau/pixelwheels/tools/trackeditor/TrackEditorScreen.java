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

import com.agateau.libgdx.AgcTmxMapLoader;
import com.agateau.pixelwheels.map.LapPositionTableIO;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.ui.StageScreen;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TrackEditorScreen extends StageScreen {
    private static final Color CURRENT_COLOR = Color.RED;
    private static final Color NORMAL_COLOR = Color.WHITE;
    private static final Vector2 NEW_DELTA = new Vector2(12, 12);
    private static final float MOVE_UNIT = 1;
    private static final long AUTO_SAVE_INTERVAL_MS = 10 * 1000;
    private static final float CROSS_RADIUS = 12;
    private final FileHandle mTmxFile;
    private final TrackIO mTrackIO;
    private final SpriteBatch mBatch = new SpriteBatch();
    private final OrthographicCamera mCamera = new OrthographicCamera();
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final Array<EditorAction> mEditorActions = new Array<>();
    private final Vector2 mViewCenter = new Vector2();

    private OrthogonalTiledMapRenderer mRenderer;
    private Array<LapPositionTableIO.Line> mLines;
    private float mZoom = 1f;

    private int mCurrentLineIdx = 0;
    private boolean mSelectP1 = true;
    private boolean mSelectP2 = true;

    private long mNeedSaveSince = 0;

    public TrackEditorScreen(FileHandle tmxFile) {
        super(new ScreenViewport());
        mTmxFile = tmxFile;
        mTrackIO = new TrackIO(mTmxFile.path());
        load();
    }

    @Override
    public void onBackPressed() {}

    @Override
    public boolean isBackKeyPressed() {
        return false;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        act();
        updateCamera();
        drawMap();
        drawSections();
    }

    private class InsertSectionAction implements EditorAction {
        int mInsertedLineIdx = -1;

        @Override
        public void undo() {
            mCurrentLineIdx = mInsertedLineIdx - 1;
            mLines.removeIndex(mInsertedLineIdx);
            markNeedSave();
        }

        @Override
        public void redo() {
            LapPositionTableIO.Line current = mLines.get(mCurrentLineIdx);
            if (mCurrentLineIdx == mLines.size - 1) {
                LapPositionTableIO.Line newLine = new LapPositionTableIO.Line();
                newLine.p1.set(current.p1).add(NEW_DELTA);
                newLine.p2.set(current.p2).add(NEW_DELTA);
                newLine.order = current.order + 1;
                mLines.add(newLine);
            } else {
                LapPositionTableIO.Line next = mLines.get(mCurrentLineIdx + 1);
                LapPositionTableIO.Line newLine = new LapPositionTableIO.Line();
                newLine.p1.set(current.p1).lerp(next.p1, 0.5f);
                newLine.p2.set(current.p2).lerp(next.p2, 0.5f);
                newLine.order = MathUtils.lerp(current.order, next.order, 0.5f);
                mLines.insert(mCurrentLineIdx + 1, newLine);
            }
            ++mCurrentLineIdx;
            mInsertedLineIdx = mCurrentLineIdx;
            markNeedSave();
        }

        @Override
        public boolean mergeWith(EditorAction other) {
            return false;
        }
    }

    private class MoveSelectionAction implements EditorAction {
        private final Vector2 mDelta = new Vector2();
        private final Array<Vector2> mPoints = new Array<>(/*ordered=*/ false, 2);

        public MoveSelectionAction(int dx, int dy) {
            mPoints.clear();
            LapPositionTableIO.Line line = mLines.get(mCurrentLineIdx);
            if (mSelectP1) {
                mPoints.add(line.p1);
            }
            if (mSelectP2) {
                mPoints.add(line.p2);
            }
            mDelta.set(dx, dy).scl(MOVE_UNIT);
        }

        @Override
        public void undo() {
            for (Vector2 point : mPoints) {
                point.sub(mDelta);
            }
            markNeedSave();
        }

        @Override
        public void redo() {
            for (Vector2 point : mPoints) {
                point.add(mDelta);
            }
            markNeedSave();
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

    private void addAction(EditorAction action) {
        action.redo();
        if (!mEditorActions.isEmpty()) {
            EditorAction lastAction = mEditorActions.get(mEditorActions.size - 1);
            if (lastAction.mergeWith(action)) {
                return;
            }
        }
        mEditorActions.add(action);
    }

    private void undo() {
        if (mEditorActions.isEmpty()) {
            return;
        }
        EditorAction action = mEditorActions.pop();
        action.undo();
    }

    private void act() {
        boolean control =
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                        || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        boolean shift =
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                        || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        int delta = shift ? 12 : 1;

        // Zoom
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)
                || Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            mZoom *= 2;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            mZoom /= 2;
        }
        // Previous / Next
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            mCurrentLineIdx = (mCurrentLineIdx + 1) % mLines.size;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            mCurrentLineIdx = mCurrentLineIdx == 0 ? (mLines.size - 1) : (mCurrentLineIdx - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            selectPoint(true, false);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            selectPoint(false, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            selectPoint(true, true);
        }
        // Scroll
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            scroll(-delta, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            scroll(delta, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            scroll(0, delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            scroll(0, -delta);
        }
        // Actions
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            addAction(new InsertSectionAction());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            addAction(new MoveSelectionAction(-delta, 0));
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            addAction(new MoveSelectionAction(delta, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            addAction(new MoveSelectionAction(0, delta));
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            addAction(new MoveSelectionAction(0, -delta));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) && control) {
            doSave();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && control) {
            undo();
        }
        save();
    }

    private void selectPoint(boolean p1, boolean p2) {
        mSelectP1 = p1;
        mSelectP2 = p2;
    }

    private void scroll(int dx, int dy) {
        mViewCenter.add(dx, dy);
    }

    private void updateCamera() {
        float width = getStage().getWidth();
        float height = getStage().getHeight();
        mCamera.viewportWidth = width;
        mCamera.viewportHeight = height;
        mCamera.zoom = 1 / mZoom;

        mCamera.position.set(mViewCenter, 0);
        mCamera.update();

        width *= mCamera.zoom;
        height *= mCamera.zoom;

        mRenderer.setView(
                mCamera.combined,
                mCamera.position.x - width / 2,
                mCamera.position.y - height / 2,
                width,
                height);
    }

    private void drawMap() {
        mBatch.setColor(1, 1, 1, 1);
        mBatch.disableBlending();
        mRenderer.render();
        mBatch.enableBlending();
    }

    private void drawSections() {
        mShapeRenderer.setProjectionMatrix(mCamera.combined);
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        mShapeRenderer.setColor(NORMAL_COLOR);
        drawLine(0);
        for (int idx = 1; idx < mLines.size; ++idx) {
            LapPositionTableIO.Line previous = mLines.get(idx - 1);
            LapPositionTableIO.Line line = mLines.get(idx);
            mShapeRenderer.line(previous.p1, line.p1);
            mShapeRenderer.line(previous.p2, line.p2);
            drawLine(idx);
        }
        mShapeRenderer.end();
    }

    private void drawLine(int idx) {
        LapPositionTableIO.Line line = mLines.get(idx);
        if (idx == mCurrentLineIdx) {
            mShapeRenderer.setColor(CURRENT_COLOR);
        }
        mShapeRenderer.line(line.p1, line.p2);
        if (idx == mCurrentLineIdx) {
            if (mSelectP1) {
                DrawUtils.drawCross(mShapeRenderer, line.p1, CROSS_RADIUS);
            }
            if (mSelectP2) {
                DrawUtils.drawCross(mShapeRenderer, line.p2, CROSS_RADIUS);
            }
            mShapeRenderer.setColor(NORMAL_COLOR);
        }
    }

    private void load() {
        TiledMap map = new AgcTmxMapLoader().load(mTmxFile.path());
        if (mRenderer != null) {
            mRenderer.dispose();
        }
        mRenderer = new OrthogonalTiledMapRenderer(map, mBatch);
        mLines = LapPositionTableIO.loadSectionLines(map);
    }

    private void markNeedSave() {
        if (mNeedSaveSince == 0) {
            mNeedSaveSince = System.currentTimeMillis();
        }
    }

    private void save() {
        if (mNeedSaveSince == 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - mNeedSaveSince < AUTO_SAVE_INTERVAL_MS) {
            return;
        }
        doSave();
    }

    private void doSave() {
        NLog.i("Saving changes");
        if (!mTrackIO.save(mLines)) {
            NLog.e("Saving failed");
            return;
        }
        mNeedSaveSince = 0;
    }
}
