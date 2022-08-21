/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.gameobjet.AnimationObject;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.ChampionshipIO;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.obstacles.ObstacleDef;
import com.agateau.pixelwheels.obstacles.ObstacleIO;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.sound.SoundAtlas;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.pixelwheels.vehicledef.VehicleIO;
import com.agateau.translations.PoImplementation;
import com.agateau.translations.Translator;
import com.agateau.ui.FontSet;
import com.agateau.ui.StrictTextureAtlas;
import com.agateau.ui.UiAssets;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/** Stores all assets */
public class Assets implements TextureRegionProvider {

    private static final float EXPLOSION_FRAME_DURATION = 0.1f;
    private static final float IMPACT_FRAME_DURATION = 0.05f;
    private static final float MINE_FRAME_DURATION = 0.2f;
    private static final float TURBO_FRAME_DURATION = 0.1f;
    private static final float TURBO_FLAME_FRAME_DURATION = 0.04f;

    private static final String[] VEHICLE_IDS = {
        "red",
        "police",
        "pickup",
        "roadster",
        "antonin",
        "santa",
        "2cv",
        "harvester",
        "rocket",
        "dark-m",
        "jeep",
        "miramar",
    };

    public static final String MENU_MUSIC_ID = "menu";
    public static final String CHAMPIONSHIP_FINISHED_MUSIC_ID = "victory";

    public static final String CURSOR_FILENAME = "ui/cursor.png";

    public final Array<VehicleDef> vehicleDefs = new Array<>();
    public final Array<Championship> championships = new Array<>();
    public final Array<ObstacleDef> obstacleDefs = new Array<>();
    public UiAssets ui;

    public final TextureRegion dot;
    public final TextureAtlas atlas;
    public final Animation<TextureRegion> impact;
    public final Animation<TextureRegion> mine;
    public final Animation<TextureRegion> turbo;
    public final Animation<TextureRegion> turboFlame;
    public final TextureRegion gift;
    public final Animation<TextureRegion> gunAnimation;
    public final TextureRegion bullet;
    public final TextureRegion skidmark;
    public final TextureRegion missile;
    public final TextureRegion target;
    public final TextureRegion helicopterBody;
    public final TextureRegion helicopterPropeller;
    public final TextureRegion helicopterPropellerTop;
    public final TextureRegion lockedVehicle;
    public final SoundAtlas soundAtlas = new SoundAtlas(FileUtils.assets("sounds"));
    public final Languages languages;

    private final Animation<TextureRegion> explosion;

    Assets() {
        this.languages = new Languages(FileUtils.assets("ui/languages.xml"));
        this.atlas = new StrictTextureAtlas(FileUtils.assets("sprites/sprites.atlas"));
        this.explosion =
                new Animation<>(EXPLOSION_FRAME_DURATION, this.atlas.findRegions("explosion"));
        this.impact = new Animation<>(IMPACT_FRAME_DURATION, this.atlas.findRegions("impact"));
        this.mine = new Animation<>(MINE_FRAME_DURATION, this.atlas.findRegions("mine"));
        this.mine.setPlayMode(Animation.PlayMode.LOOP);
        this.turbo = new Animation<>(TURBO_FRAME_DURATION, this.atlas.findRegions("bonus-turbo"));
        this.turboFlame =
                new Animation<>(TURBO_FLAME_FRAME_DURATION, this.atlas.findRegions("turbo-flame"));
        this.turboFlame.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        this.gift = findRegion("gift");
        this.gunAnimation = new Animation<>(0.1f / 3, this.atlas.findRegions("bonus-gun"));
        this.bullet = findRegion("bullet");

        // Fix white-pixel to avoid fading borders
        this.dot = findRegion("white-pixel");
        removeBorders(this.dot);

        this.skidmark = findRegion("skidmark");

        this.missile = findRegion("missile");
        this.target = findRegion("target");

        this.helicopterBody = this.findRegion("helicopter-body");
        this.helicopterPropeller = this.findRegion("helicopter-propeller");
        this.helicopterPropellerTop = this.findRegion("helicopter-propeller-top");

        this.lockedVehicle = this.findRegion("vehicles/locked");

        loadVehicleDefinitions();
        loadObstacleDefinitions();
        initSoundAtlas();
        initChampionships();
    }

    public void setLanguage(String languageId) {
        String path = StringUtils.format("po/%s.po", languageId);
        FileHandle handle = FileUtils.assets(path);
        Translator.Implementation impl = PoImplementation.load(handle);
        Translator.setImplementation(impl);

        if (impl == null) {
            if (!Languages.DEFAULT_ID.equals(languageId)) {
                NLog.e("Failed to load translation for '%s'", languageId);
                languageId = Languages.DEFAULT_ID;
            }
        }
        String characters = impl == null ? "" : impl.getCharacters();

        FontSet fontSet = languages.getFontSet(languageId);
        if (ui != null) {
            ui.dispose();
        }
        ui = new UiAssets(fontSet, characters);
    }

    private void initSoundAtlas() {
        for (int i = 0; i < 5; ++i) {
            String name = StringUtils.format("engine-%d", i);
            String filename = StringUtils.format("loop_%d_0.wav", i + 1);
            this.soundAtlas.load(filename, name);
        }
        this.soundAtlas.load("drifting.wav");
        this.soundAtlas.load("bonus.wav");
        this.soundAtlas.load("explosion.wav");
        this.soundAtlas.load("shoot.wav");
        this.soundAtlas.load("impact.wav");
        this.soundAtlas.load("turbo.wav");
        this.soundAtlas.load("impact.wav", "collision");
        this.soundAtlas.load("helicopter.wav");
        this.soundAtlas.load("missile.wav");
        this.soundAtlas.load("countdown1.wav");
        this.soundAtlas.load("countdown2.wav");
        this.soundAtlas.load("splash.wav");
        this.soundAtlas.load("points-increase.wav");
    }

    public Music loadMusic(String musicId) {
        FileHandle handle = FileUtils.assets("musics/" + musicId + ".mp3");
        if (!handle.exists()) {
            NLog.e("No music with id " + musicId);
            return null;
        }
        return Gdx.audio.newMusic(handle);
    }

    public String getTrackMusicId(Track track) {
        Championship championship = track.getChampionship();
        return "championships/" + championship.getId();
    }

    private void initChampionships() {
        ChampionshipIO io = new ChampionshipIO();
        for (int idx = 0; ; ++idx) {
            String fileName = "championships/" + idx + ".xml";
            FileHandle handle = FileUtils.assets(fileName);
            if (!handle.exists()) {
                break;
            }
            this.championships.add(io.load(handle));
        }
        Assert.check(this.championships.notEmpty(), "No championships found");
    }

    private static void removeBorders(TextureRegion region) {
        region.setRegionX(region.getRegionX() + 2);
        region.setRegionY(region.getRegionY() + 2);
        region.setRegionWidth(region.getRegionWidth() - 4);
        region.setRegionHeight(region.getRegionHeight() - 4);
    }

    @Override
    public TextureRegion findRegion(String name) {
        return this.atlas.findRegion(name);
    }

    @Override
    public Array<TextureAtlas.AtlasRegion> findRegions(String name) {
        return this.atlas.findRegions(name);
    }

    public VehicleDef findVehicleDefById(String id) {
        for (VehicleDef def : vehicleDefs) {
            if (def.id.equals(id)) {
                return def;
            }
        }
        return null;
    }

    public Championship findChampionshipById(String id) {
        for (Championship championship : championships) {
            if (championship.getId().equals(id)) {
                return championship;
            }
        }
        return null;
    }

    public Track findTrackById(String id) {
        for (Championship championship : championships) {
            for (Track track : championship.getTracks()) {
                if (track.getId().equals(id)) {
                    return track;
                }
            }
        }
        return null;
    }

    public AnimationObject createExplosion(AudioManager audioManager, float x, float y) {
        AnimationObject obj = AnimationObject.create(explosion, x, y);
        obj.initAudio(audioManager, soundAtlas.get("explosion"));
        return obj;
    }

    public TextureRegion getChampionshipRegion(Championship championship) {
        return ui.atlas.findRegion("championship-icons/" + championship.getId());
    }

    public TextureRegion getTrackRegion(Track track) {
        return ui.atlas.findRegion("map-icons/" + track.getId());
    }

    public TextureRegion getLockedTrackRegion() {
        return ui.atlas.findRegion("map-icons/locked");
    }

    private void loadVehicleDefinitions() {
        for (String id : VEHICLE_IDS) {
            this.vehicleDefs.add(VehicleIO.get(id));
        }
    }

    private void loadObstacleDefinitions() {
        obstacleDefs.clear();
        obstacleDefs.addAll(ObstacleIO.getAll(this));
    }
}
