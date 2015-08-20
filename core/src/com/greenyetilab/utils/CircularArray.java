package com.greenyetilab.utils;

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
