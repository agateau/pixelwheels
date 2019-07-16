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

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * An helper class to fill a table with rows
 *
 * addHeaderRow and addContentRow should always be called with
 * the same number of values.
 */
public abstract class TableRowCreator {
    private static final String HEADER_STYLE = "tableHeaderRow";
    private Table mTable;

    private String mNextStyle = "";

    /**
     * Must create cells in @p table using the table.add() method
     *
     */
    protected abstract void createCells(Table table, String style, String... values);

    public void setTable(Table table) {
        mTable = table;
    }

    public TableRowCreator setRowStyle(String style) {
        mNextStyle = style;
        return this;
    }

    /**
     * Add a content row
     */
    public TableRowCreator addRow(String... values) {
        createCells(mTable, mNextStyle, values);
        mTable.row();
        return this;
    }

    /**
     * Add an header row
     */
    public void addHeaderRow(String... values) {
        setRowStyle(HEADER_STYLE);
        addRow(values);
    }
}
