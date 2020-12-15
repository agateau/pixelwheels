/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui;

import com.agateau.utils.Assert;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * An helper class to fill a table with rows
 *
 * <p>addHeaderRow and addContentRow should always be called with the same number of values.
 */
public abstract class TableRowCreator {
    private static final String HEADER_STYLE = "tableHeaderRow";
    private final int mColumns;
    private Table mTable;

    private String mNextStyle = "";
    private int mPadding = 0;

    /** Must create a cell in @p table using the table.add() method */
    @SuppressWarnings("rawtypes")
    protected abstract Cell createCell(Table table, int column, String style, String value);

    public TableRowCreator(int columns) {
        mColumns = columns;
    }

    public void setTable(Table table) {
        mTable = table;
    }

    public void setSpacing(int padding) {
        mPadding = padding;
    }

    public void setRowStyle(String style) {
        mNextStyle = style;
    }

    /** Add a content row */
    public void addRow(String... values) {
        Assert.check(values.length == mColumns, "Wrong number of columns");
        for (int column = 0; column < mColumns; ++column) {
            //noinspection rawtypes
            Cell cell = createCell(mTable, column, values[column], mNextStyle);
            if (column < mColumns - 1) {
                cell.padRight(mPadding);
            }
        }
        mTable.row();
    }

    /**
     * Returns the cell from the last created row
     *
     * <p>if @p column is < 0: starts from the end, so -1 is the last column
     */
    public <T extends Actor> Cell<T> getCreatedRowCell(int column) {
        if (column < 0) {
            column += mColumns;
        }
        //noinspection unchecked
        return mTable.getCells().get(mTable.getCells().size - mColumns + column);
    }

    /** Add an header row */
    public void addHeaderRow(String... values) {
        setRowStyle(HEADER_STYLE);
        addRow(values);
    }
}
