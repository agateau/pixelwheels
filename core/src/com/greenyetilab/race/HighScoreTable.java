package com.greenyetilab.race;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntArray;

/**
 * Handle the player high scores
 */
public class HighScoreTable {
    private static final int SIZE = 10;
    private final IntArray mScores = new IntArray();
    private FileHandle mFile;

    public HighScoreTable() {
        fill();
    }

    public void init(FileHandle file) {
        mFile = file;
        load();
    }

    public IntArray getScores() {
        return mScores;
    }

    /**
     * Adds a new score, returns the 0-based position where it was inserted, or -1 if the score is
     * too low to enter
     */
    public int insert(int score) {
        for (int i = 0; i < mScores.size; ++i) {
            if (score > mScores.get(i)) {
                mScores.insert(i, score);
                mScores.pop();
                save();
                return i;
            }
        }
        return -1;
    }

    public void reset() {
        fill();
        save();
    }

    private void load() {

    }

    private void save() {

    }

    private void fill() {
        mScores.clear();
        for (int i = 0; i < SIZE; ++i) {
            mScores.add((SIZE - i) * 100);
        }
    }
}
