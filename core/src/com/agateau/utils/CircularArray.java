/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.utils;

/**
 * An array which loops after reaching the end.
 *
 * <p>Adding a new element when the array is full overwrites the first element.
 *
 * <p>It only makes sense to use this with mutable objects, since the goal is to reuse the existing
 * instances.
 */
public abstract class CircularArray<T> {
    private final T[] mItems;
    // Points to the first valid item, if any
    private int mBegin = 0;
    // Points to the item after the last valid item
    private int mEnd = 0;

    public CircularArray(int size) {
        // +1 so that mEnd has an item to point to
        mItems = (T[]) new Object[size + 1];
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

    public T add() {
        T element = mItems[mEnd];
        if (element == null) {
            element = createInstance();
            mItems[mEnd] = element;
        }
        mEnd = getNextIndex(mEnd);
        if (mBegin == mEnd) {
            mBegin = getNextIndex(mBegin);
        }
        return element;
    }

    protected abstract T createInstance();
}
