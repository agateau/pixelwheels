package com.agateau.tinywheels.motorlab;

import com.agateau.tinywheels.SoundAtlas;
import com.agateau.tinywheels.sound.MotorSound;
import com.agateau.ui.Menu;
import com.agateau.ui.SliderMenuItem;
import com.agateau.ui.StageScreen;
import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Locale;

/**
 * Main screen for MotorLab
 */
class MotorLabScreen extends StageScreen {
    private Skin mSkin;
    private MotorSound mMotorSound;
    private SliderMenuItem mSpeedItem;

    public MotorLabScreen() {
        super(new ScreenViewport());
        loadSkin();
        setupUi();
        setupMotorLab();
    }

    private void loadSkin() {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        mSkin = new Skin(atlas);
        loadFonts();
        mSkin.load(Gdx.files.internal("ui/uiskin.json"));
    }

    private void loadFonts() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        mSkin.add("default-font", loadFont("fonts/Xolonium-Regular.ttf", 28));
        mSkin.add("title-font", loadFont("fonts/Aero.ttf", 32));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 0.5f;
        mSkin.add("small-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.borderWidth = 0.5f;
        mSkin.add("hud-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        parameter.characters = "1234567890thsrdneméè";
        parameter.borderWidth = 0.5f;
        mSkin.add("hud-rank-font", loadFont("fonts/Xolonium-Regular.ttf", parameter));
    }

    private BitmapFont loadFont(String name, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        return loadFont(name, parameter);
    }

    private BitmapFont loadFont(String name, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(name));
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private void setupUi() {
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        Menu menu = new Menu(mSkin);
        menu.setLabelColumnWidth(200);
        menu.setDefaultItemWidth(500);

        mSpeedItem = new SliderMenuItem(menu);
        mSpeedItem.setRange(0, 1, 0.01f);
        menu.addItemWithLabel("Speed", mSpeedItem);

        root.addPositionRule(menu, Anchor.CENTER, root, Anchor.CENTER);
    }

    private void setupMotorLab() {
        SoundAtlas soundAtlas = new SoundAtlas(Gdx.files.internal("sounds"));
        for (int i = 0; i < 5; ++i) {
            String name = String.format(Locale.US, "engine-%d", i);
            String filename = String.format(Locale.US, "loop_%d_0.wav", i + 1);
            soundAtlas.load(filename, name);
        }
        mMotorSound = new MotorSound(soundAtlas);
    }

    @Override
    public void render(float dt) {
        super.render(dt);
        mMotorSound.play(mSpeedItem.getFloatValue());
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean isBackKeyPressed() {
        return false;
    }
}
