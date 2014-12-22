package com.greenyetilab.race;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.log.NLog;

import java.io.IOException;

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

    public void load() {
        if (mFile.exists()) {
            load(FileUtils.parseXml(mFile));
        }
    }

    public void load(XmlReader.Element root) {
        if (root == null) {
            NLog.e("called with a null object, not loading anything");
            return;
        }
        int size = Math.min(root.getChildCount(), mScores.size);
        int idx = 0;
        for (; idx < size; ++idx) {
            XmlReader.Element element = root.getChild(idx);
            int score = element.getIntAttribute("value");
            mScores.set(idx, score);
        }
        // Fill the array with zeros, just in case
        for (; idx < mScores.size; ++idx) {
            mScores.set(idx, 0);
        }
    }

    public void save() {
        save(new XmlWriter(mFile.writer(false)));
    }

    public void save(XmlWriter writer) {
        try {
            XmlWriter root = writer.element(("highscores"));
            for (int i = 0; i < mScores.size; ++i) {
                int score = mScores.get(i);
                root.element("highscore")
                        .attribute("value", score)
                        .pop();
            }
            writer.close();
        } catch (IOException e) {
            NLog.e("Failed to save highscores. Exception: %s", e);
        }
    }

    private void fill() {
        mScores.clear();
        for (int i = 0; i < SIZE; ++i) {
            mScores.add((SIZE - i) * 100);
        }
    }
}
