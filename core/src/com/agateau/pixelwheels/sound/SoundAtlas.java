/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.sound;

import com.agateau.utils.Assert;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;

/** Provides access to sound by name */
public class SoundAtlas {
    private final FileHandle mRootDir;
    private final HashMap<String, Sound> mSounds = new HashMap<>();

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
        mSounds.put(name, Gdx.audio.newSound(file));
    }
}
