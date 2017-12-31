package com.agateau.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * An item to pick a text from a selection
 */
public class SelectorMenuItem<T> extends RangeMenuItem {
    private static class Entry<T> {
        final String text;
        final T data;

        Entry(String text, T value) {
            this.text = text;
            this.data = value;
        }
    }

    private Label mMainLabel;

    private Array<Entry<T>> mEntries = new Array<Entry<T>>();

    public SelectorMenuItem(Menu menu) {
        super(menu);
    }

    @Override
    protected Actor createMainActor(Menu menu) {
        mMainLabel = new Label("", menu.getSkin());
        mMainLabel.setAlignment(Align.center);
        return mMainLabel;
    }

    @Override
    protected void updateMainActor() {
        mMainLabel.setText(mEntries.get(getValue()).text);
    }

    public void addEntry(String text, T data) {
        mEntries.add(new Entry<T>(text, data));
        setRange(0, mEntries.size - 1);
    }

    public T getData() {
        Entry<T> entry = mEntries.get(getValue());
        return entry.data;
    }

    public void setData(T data) {
        for (int idx = 0; idx < mEntries.size; ++idx) {
            if (mEntries.get(idx).data == data) {
                setValue(idx);
                return;
            }
        }
        setValue(0);
    }
}
