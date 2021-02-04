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
package com.agateau.pixelwheels.enginelab;

import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.DefaultSoundPlayer;
import com.agateau.pixelwheels.sound.EngineSoundPlayer;
import com.agateau.pixelwheels.sound.SoundAtlas;
import com.agateau.pixelwheels.sound.SoundPlayer;
import com.agateau.ui.StageScreen;
import com.agateau.ui.UiAssets;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.SliderMenuItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Locale;

/** Main screen for EngineLab */
class EngineLabScreen extends StageScreen {
    private final Skin mSkin;
    private EngineSoundPlayer mEngineSoundPlayer;
    private SliderMenuItem mSpeedItem;

    private SliderMenuItem mPitchItem;
    private final Array<SliderMenuItem> mVolumeItems = new Array<>();

    static class LabAudioManager implements AudioManager {
        @Override
        public boolean areSoundFxMuted() {
            return false;
        }

        @Override
        public void setSoundFxMuted(boolean muted) {}

        @Override
        public boolean isMusicMuted() {
            return false;
        }

        @Override
        public void setMusicMuted(boolean muted) {}

        @Override
        public void play(Sound sound, float volume) {}

        @Override
        public SoundPlayer createSoundPlayer(Sound sound) {
            return new DefaultSoundPlayer(sound);
        }

        @Override
        public void playMusic(String musicId) {}

        @Override
        public void fadeOutMusic() {}
    }

    public EngineLabScreen() {
        super(new ScreenViewport());
        setupEngineLab();
        UiAssets assets = new UiAssets();
        mSkin = assets.skin;
        setupUi();
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        Menu menu = new Menu(mSkin);
        menu.setLabelColumnWidth(200);
        menu.setWidth(500);

        mSpeedItem = new SliderMenuItem(menu);
        mSpeedItem.setRange(0, 1, 0.01f);
        menu.addItemWithLabel("Speed", mSpeedItem);

        mPitchItem = new SliderMenuItem(menu);
        mPitchItem.setRange(EngineSoundPlayer.MIN_PITCH, EngineSoundPlayer.MAX_PITCH, 0.01f);
        menu.addItemWithLabel("Pitch", mPitchItem);

        menu.addLabel("Volumes");
        for (int i = 0; i < mEngineSoundPlayer.getSoundCount(); ++i) {
            SliderMenuItem item = new SliderMenuItem(menu);
            item.setRange(0, 1, 0.01f);
            menu.addItemWithLabel(String.valueOf(i), item);
            mVolumeItems.add(item);
        }

        root.addPositionRule(menu, Anchor.CENTER, root, Anchor.CENTER);
    }

    private void setupEngineLab() {
        SoundAtlas soundAtlas = new SoundAtlas(Gdx.files.internal("sounds"));
        for (int i = 0; i < 5; ++i) {
            String name = String.format(Locale.US, "engine-%d", i);
            String filename = String.format(Locale.US, "loop_%d_0.wav", i + 1);
            soundAtlas.load(filename, name);
        }
        mEngineSoundPlayer = new EngineSoundPlayer(soundAtlas, new LabAudioManager());
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        mEngineSoundPlayer.play(mSpeedItem.getFloatValue(), /* maxVolume= */ 1);
        mPitchItem.setFloatValue(mEngineSoundPlayer.getPitch());
        for (int i = 0; i < mEngineSoundPlayer.getSoundCount(); ++i) {
            mVolumeItems.get(i).setFloatValue(mEngineSoundPlayer.getSoundVolume(i));
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    public boolean isBackKeyPressed() {
        return false;
    }
}
