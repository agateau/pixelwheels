package com.agateau.tinywheels;

import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

/**
 * Provides access to sound by name
 */
public class SoundAtlas {
    private final FileHandle mRootDir;
    private final HashMap<String, Sound> mSounds = new HashMap<String, Sound>();

    public SoundAtlas(FileHandle rootDir) {
        mRootDir = rootDir;
    }

    public Sound get(String name) {
        Sound sound = mSounds.get(name);
        if (sound == null) {
            throw new RuntimeException("Sound '" + name + "' not found");
        }
        return sound;
    }

    public boolean contains(String name) {
        return mSounds.containsKey(name);
    }

    public void load(String filename) {
        load(filename, "");
    }
    public void load(String filename, String name) {
        FileHandle file = mRootDir.child(filename);
        Assert.check(file.exists(), "No sound named " + filename + " in " + mRootDir.path());
        if ("".equals(name)) {
            name = file.nameWithoutExtension();
        }
        NLog.i("Loading sound %s from %s", name, file.path());
        mSounds.put(name, Gdx.audio.newSound(file));
    }
}
