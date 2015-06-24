package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Stores all assets
 */
public class Assets {
    private static final float EXPLOSION_FRAME_DURATION = 0.1f;
    private static final float IMPACT_FRAME_DURATION = 0.05f;
    private static final float MINE_FRAME_DURATION = 0.2f;

    private static final String[] VEHICLE_IDS = { "red", "police", "pickup", "roadster", "antonin" };

    public final Array<VehicleDef> vehicleDefs = new Array<VehicleDef>();
    public final Array<MapInfo> mapInfos = new Array<MapInfo>(new MapInfo[]{
            new MapInfo("race", "Let it Snow"),
            new MapInfo("be", "City")
    });
    public final Skin skin;
    public final TextureRegion wheel;
    public final TextureRegion dot;
    public final TextureRegion hudBackground;
    public final TextureAtlas atlas;
    public final Animation explosion;
    public final Animation iceExplosion;
    public final Animation impact;
    public final Animation mine;
    public final Array<TextureRegion> gifts = new Array<TextureRegion>();
    public final Animation gunAnimation;
    public final TextureRegion bullet;
    public final TextureRegion skidmark;

    private final HashMap<String, TextureAtlas.AtlasRegion> mRegions = new HashMap<String, TextureAtlas.AtlasRegion>();

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
        this.atlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));
        this.wheel = findRegion("vehicles/wheel");
        this.explosion = new Animation(EXPLOSION_FRAME_DURATION, this.findRegions("explosion"));
        this.iceExplosion = new Animation(EXPLOSION_FRAME_DURATION, this.findRegions("ice-explosion"));
        this.impact = new Animation(IMPACT_FRAME_DURATION, this.findRegions("impact"));
        this.mine = new Animation(MINE_FRAME_DURATION, this.findRegions("mine"));
        this.mine.setPlayMode(Animation.PlayMode.LOOP);
        for (int idx = 0; idx <= 2; ++idx) {
            String name = String.format("gift-%d", idx);
            this.gifts.add(findRegion(name));
        }
        this.gunAnimation = new Animation(0.1f / 3, this.findRegions("bonus-gun"));
        this.bullet = findRegion("bullet");

        // Fix white-pixel to avoid fading borders
        this.dot = findRegion("white-pixel");
        removeBorders(this.dot);

        this.hudBackground = findRegion("hud-bg");
        removeBorders(this.hudBackground);

        this.skidmark = findRegion("skidmark");

        loadVehicleDefinitions();
    }

    public VehicleDef getVehicleById(String id) {
        for (VehicleDef def : this.vehicleDefs) {
            if (def.id.equals(id)) {
                return def;
            }
        }
        return null;
    }

    private static void removeBorders(TextureRegion region) {
        region.setRegionX(region.getRegionX() + 2);
        region.setRegionY(region.getRegionY() + 2);
        region.setRegionWidth(region.getRegionWidth() - 4);
        region.setRegionHeight(region.getRegionHeight() - 4);
    }

    public TextureAtlas.AtlasRegion findRegion(String name) {
        TextureAtlas.AtlasRegion region = mRegions.get(name);
        if (region != null) {
            return region;
        }
        region = this.atlas.findRegion(name);
        if (region == null) {
            throw new RuntimeException("Failed to load a texture region named '" + name + "'");
        }
        mRegions.put(name, region);
        return region;
    }

    public Array<TextureAtlas.AtlasRegion> findRegions(String name) {
        Array<TextureAtlas.AtlasRegion> lst = this.atlas.findRegions(name);
        if (lst.size == 0) {
            throw new RuntimeException("Failed to load an array of regions named '" + name + "'");
        }
        return lst;
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

    private void loadVehicleDefinitions() {
        for (String id : VEHICLE_IDS) {
            this.vehicleDefs.add(VehicleIO.get(id));
        }
    }
}
