package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * Stores all assets
 */
public class Assets {
    public final Skin skin;
    public final Array<TextureRegion> cars = new Array<TextureRegion>();
    public final TextureRegion wheel;
    public final TextureAtlas atlas;
    public Array<MapInfo> mapInfoList = new Array<MapInfo>();

    /**
     * This structure is used to store scaled pad values for NinePatches. After a NinePatch has
     * been scaled, we can't use the NinePatch.getPad*() methods because pads are stored as int, so
     * their value is 0.
     */
    public static class Pads {
        float left;
        float right;
        float top;
        float bottom;
    }

    Assets() {
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.atlas = new TextureAtlas(Gdx.files.internal("race.atlas"));
        this.wheel = this.atlas.findRegion("car/wheel");
        for (int idx = 1; idx <= 6; ++idx) {
            String name = String.format("car/car%d", idx);
            this.cars.add(this.atlas.findRegion(name));
        }

        for (String name: new String[]{
                "santa.tmx",
        }) {
            mapInfoList.add(new MapInfo(name));
        }
    }

    public NinePatch createScaledPatch(String name) {
        return createScaledPatch(name, null);
    }

    public NinePatch createScaledPatch(String name, Pads pads) {
        NinePatch patch = atlas.createPatch(name);
        if (pads != null) {
            pads.left = patch.getPadLeft();
            pads.right = patch.getPadRight();
            pads.top = patch.getPadTop();
            pads.bottom = patch.getPadBottom();
        }
        patch.scale(Constants.UNIT_FOR_PIXEL, Constants.UNIT_FOR_PIXEL);
        if (pads != null) {
            pads.left *= Constants.UNIT_FOR_PIXEL;
            pads.right *= Constants.UNIT_FOR_PIXEL;
            pads.top *= Constants.UNIT_FOR_PIXEL;
            pads.bottom *= Constants.UNIT_FOR_PIXEL;
        }
        return  patch;
    }
}
