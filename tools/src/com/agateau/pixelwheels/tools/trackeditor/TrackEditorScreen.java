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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TrackEditorScreen extends StageScreen implements Editor {
    private static final Color CURRENT_COLOR = Color.RED;
    private static final Color NORMAL_COLOR = Color.WHITE;
    private static final long AUTO_SAVE_INTERVAL_MS = 10 * 1000;
    private static final float CROSS_RADIUS = 12;
    private final FileHandle mTmxFile;
    private final TrackIO mTrackIO;
    private final SpriteBatch mBatch = new SpriteBatch();
    private final OrthographicCamera mCamera = new OrthographicCamera();
    private final ShapeRenderer mShapeRenderer = new ShapeRenderer();
    private final Vector2 mViewCenter = new Vector2();

    private OrthogonalTiledMapRenderer mRenderer;
    private Array<LapPositionTableIO.Line> mLines;
    private float mZoom = 1f;

    private final EditorActionStack mActionStack = new EditorActionStack();

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

    private void addAction(EditorAction action) {
        mActionStack.add(action);
    }

    private void undo() {
        mActionStack.undo();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (shift) {
                mCurrentLineIdx = mCurrentLineIdx == 0 ? (mLines.size - 1) : (mCurrentLineIdx - 1);
            } else {
                mCurrentLineIdx = (mCurrentLineIdx + 1) % mLines.size;
            }
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
            addAction(new InsertSectionAction(this));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            addAction(new MoveSelectionAction(this, -delta, 0));
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            addAction(new MoveSelectionAction(this, delta, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            addAction(new MoveSelectionAction(this, 0, delta));
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            addAction(new MoveSelectionAction(this, 0, -delta));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            if (mLines.size > 2) {
                addAction(new DeleteSectionAction(this));
            }
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

    @Override
    public void markNeedSave() {
        if (mNeedSaveSince == 0) {
            mNeedSaveSince = System.currentTimeMillis();
        }
    }

    @Override
    public Array<LapPositionTableIO.Line> lines() {
        return mLines;
    }

    @Override
    public int currentLineIdx() {
        return mCurrentLineIdx;
    }

    @Override
    public void setCurrentLineIdx(int idx) {
        mCurrentLineIdx = idx;
    }

    @Override
    public boolean isP1Selected() {
        return mSelectP1;
    }

    @Override
    public boolean isP2Selected() {
        return mSelectP2;
    }

    @Override
    public void setP1Selected(boolean selected) {
        mSelectP1 = selected;
    }

    @Override
    public void setP2Selected(boolean selected) {
        mSelectP2 = selected;
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
