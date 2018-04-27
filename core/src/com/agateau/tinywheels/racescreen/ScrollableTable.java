/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels.racescreen;

import com.agateau.ui.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.XmlReader;

/**
 * A generic, scrollable table to present tabular data
 *
 * addHeaderRow and addContentRow should always be called with
 * the same number of values.
 */
public class ScrollableTable extends ScrollPane {
    private static final String HEADER_STYLE = "tableHeaderRow";
    private final Table mTable;
    private final CellCreator mCellCreator;

    public interface CellCreator {
        /**
         * Must create cells in @p table using the table.add() method
         *
         */
        void createCells(Table table, String style, String... values);
    }

    @SuppressWarnings("WeakerAccess")
    public ScrollableTable(Skin skin, CellCreator cellCreator) {
        super(null);
        mCellCreator = cellCreator;
        mTable = new Table(skin);
        setWidget(mTable);
    }

    /**
     * Add an header row
     */
    public void addHeaderRow(String... values) {
        mCellCreator.createCells(mTable, HEADER_STYLE, values);
        mTable.row();
    }

    /**
     * Add a content row. All Label inside the row will use
     * the style @p style.
     */
    public void addContentRow(String style, String... values) {
        mCellCreator.createCells(mTable, style, values);
        mTable.row();
    }

    public static void register(UiBuilder builder, String className, final CellCreator cellCreator) {
        builder.registerActorFactory(className, new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(UiBuilder uiBuilder, XmlReader.Element element) {
                return new ScrollableTable(uiBuilder.getSkin(), cellCreator);
            }
        });
    }
}
