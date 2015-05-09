package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * The debug screen
 */
public class DebugScreen extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;
    private VerticalGroup mGroup;
    private GamePlay mReference = new GamePlay();

    public DebugScreen(RaceGame game) {
        mGame = game;
        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/debug.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mGroup = new VerticalGroup();
        mGroup.align(Align.left).space(20);
        addTitle("Race");
        mGroup.addActor(addRange("Viewport width:", "viewportWidth", 20, 80));
        mGroup.addActor(addRange("Racer count:", "racerCount", 1, 8));
        mGroup.addActor(addRange("Max skidmarks:", "maxSkidmarks", 10, 200, 10));
        mGroup.addActor(addRange("Border restitution:", "borderRestitution", 1, 50));
        addTitle("Wheel");
        mGroup.addActor(addRange("Max driving force:", "maxDrivingForce", 10, 200, 10));
        mGroup.addActor(addRange("Stickiness:", "maxLateralImpulse", 1, 20));
        mGroup.addActor(addRange("Steer: low speed:", "lowSpeedMaxSteer", 2, 50, 2));
        mGroup.addActor(addRange("Steer: high speed:", "highSpeedMaxSteer", 2, 50, 2));
        addTitle("Vehicle");
        mGroup.addActor(addRange("Density:", "vehicleDensity", 1, 50));
        mGroup.addActor(addRange("Restitution:", "vehicleRestitution", 1, 50));
        addTitle("Spin");
        mGroup.addActor(addRange("Impulse:", "spinImpulse", 1, 200));
        mGroup.addActor(addRange("Duration:", "spinDuration", 1, 20));
        addTitle("Hud");
        mGroup.addActor(addRange("Button Size:", "hudButtonSize", 10, 200, 10));
        addTitle("Debug");
        mGroup.addActor(addCheckBox("Show debug hud", "debug/showDebugHud"));
        mGroup.addActor(addCheckBox("Box2D: Debug", "debug/box2d"));
        mGroup.addActor(addCheckBox("Box2D: Draw velocities", "debug/box2d/drawVelocities"));
        mGroup.addActor(addCheckBox("Tiles: Draw corners", "debug/tiles/drawCorners"));

        mGroup.setSize(mGroup.getPrefWidth(), mGroup.getPrefHeight());

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GamePlay.instance.save();
                mGame.popScreen();
            }
        });

        ScrollPane pane = builder.getActor("scrollPane");
        pane.setWidget(mGroup);
        root.addSizeRule(pane, root, 1, 1, -5, 0);
    }

    private CheckBox addCheckBox(String text, final String key) {
        final CheckBox checkBox = new CheckBox(text, mGame.getAssets().skin);
        final Preferences prefs = RaceGame.getPreferences();
        checkBox.setChecked(prefs.getBoolean(key, false));
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean(key, checkBox.isChecked());
                prefs.flush();
            }
        });
        return checkBox;
    }

    private Actor addRange(String text, final String keyName, int min, int max) {
        return addRange(text, keyName, min, max, 1);
    }

    private Actor addRange(String text, final String keyName, int min, int max, int stepSize) {
        final Skin skin = mGame.getAssets().skin;

        final DefaultLabel defaultLabel = new DefaultLabel(keyName);

        final SpinBox spinBox = new SpinBox(min, max, skin);
        spinBox.setStepSize(stepSize);
        spinBox.setValue(GamePlay.instance.getIntrospector().getInt(keyName));
        spinBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = spinBox.getValue();
                GamePlay.instance.getIntrospector().setInt(keyName, value);
                defaultLabel.update();
            }
        });

        final HorizontalGroup group = new HorizontalGroup();
        group.addActor(new Label(text + " ", skin));
        group.addActor(spinBox);
        group.addActor(defaultLabel);
        return group;
    }

    private void addTitle(String title) {
        mGroup.addActor(new Label("-- " + title + " --", mGame.getAssets().skin));
    }

    private class DefaultLabel extends Label {
        private final String mKeyName;

        public DefaultLabel(String keyName) {
            super("", mGame.getAssets().skin);
            mKeyName = keyName;
            update();
        }

        public void update() {
            Object ref = mReference.getIntrospector().get(mKeyName);
            Object current = GamePlay.instance.getIntrospector().get(mKeyName);

            if (ref.equals(current)) {
                setVisible(false);
                return;
            }
            setVisible(true);
            String text = " (" + ref.toString() + ")";
            setText(text);
        }
    }
}
