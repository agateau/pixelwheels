package com.greenyetilab.utils.log;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * Implementation of Printer which uses Gdx.app logging facilities
 *
 * @author aurelien
 */
public class GdxPrinter implements NLog.Printer {
    private final String mPrefix;

    public GdxPrinter() {
        this("");
    }

    public GdxPrinter(String prefix) {
        mPrefix = prefix.isEmpty() ? "" : (prefix + ".");
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void print(int level, String tag, String message) {
        tag = mPrefix + tag;
        if (level == Application.LOG_DEBUG) {
            Gdx.app.debug(tag, message);
        } else if (level == Application.LOG_INFO) {
            Gdx.app.log(tag, message);
        } else { // LOG_ERROR
            Gdx.app.error(tag, message);
        }
    }
}
