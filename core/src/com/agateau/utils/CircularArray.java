/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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
package com.agateau.utils;

/**
 * An array which loops after reaching the end. Adding a new element when the array is full
 * overwrites the first element
 */

public class CircularArray<T> {
    private final T[] mItems;
    private int mBegin = 0;
    private int mEnd = 0;

    public CircularArray(int size) {
        mItems = (T[])new Object[size];
    }

    public T get(int index) {
        return mItems[index];
    }

    public int getBeginIndex() {
        return mBegin;
    }

    public int getEndIndex() {
        return mEnd;
    }

    public int getNextIndex(int idx) {
        return (idx + 1) % mItems.length;
    }

    public void add(T element) {
        mItems[mEnd] = initElement(mItems[mEnd], element);
        mEnd = getNextIndex(mEnd);
        if (mEnd == mBegin) {
            mBegin = getNextIndex(mBegin);
        }
    }

    protected T initElement(T existingElement, T newElement) {
        return newElement;
    }
}
