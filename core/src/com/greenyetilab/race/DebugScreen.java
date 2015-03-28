package com.greenyetilab.race;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.Introspector;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * The debug screen
 */
public class DebugScreen extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;

    public DebugScreen(RaceGame game) {
        mGame = game;
        Skin skin = mGame.getAssets().skin;

        AnchorGroup root = new AnchorGroup();
        root.setFillParent(true);
        getStage().addActor(root);

        VerticalGroup vGroup = new VerticalGroup();
        vGroup.align(Align.left).space(20);
        vGroup.addActor(addCheckBox("Show debug hud", "debug/showDebugHud"));
        vGroup.addActor(addCheckBox("Box2D: Debug", "debug/box2d"));
        vGroup.addActor(addCheckBox("Box2D: Draw velocities", "debug/box2d/drawVelocities"));
        vGroup.addActor(addCheckBox("Tiles: Draw corners", "debug/tiles/drawCorners"));
        vGroup.addActor(addRange("Racer count:", "racerCount", 1, 8));
        vGroup.addActor(addRange("Max driving force:", "maxDrivingForce", 10, 200, 10));
        vGroup.addActor(addRange("Drift if impulse is more than:", "maxLateralImpulse", 1, 20));
        vGroup.addActor(addRange("Max skidmarks:", "maxSkidmarks", 10, 200, 10));
        vGroup.addActor(addRange("Steer: low speed:", "lowSpeedMaxSteer", 2, 50, 2));
        vGroup.addActor(addRange("Steer: high speed:", "highSpeedMaxSteer", 2, 50, 2));
        vGroup.setSize(vGroup.getPrefWidth(), vGroup.getPrefHeight());

        TextButton backButton = new TextButton("[", skin, "default");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GamePlay.save();
                mGame.popScreen();
            }
        });

        ScrollPane pane = new ScrollPane(vGroup);

        root.addPositionRule(pane, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT, 100, 0);
        root.addSizeRule(pane, root, 1, 1, -100, 0);
        root.addPositionRule(backButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
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

        final SpinBox spinBox = new SpinBox(min, max, skin);
        spinBox.setStepSize(stepSize);
        spinBox.setValue(Introspector.getInt(GamePlay.class, keyName));
        spinBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = spinBox.getValue();
                Introspector.setInt(GamePlay.class, keyName, value);
            }
        });

        final HorizontalGroup group = new HorizontalGroup();
        group.addActor(new Label(text + " ", skin));
        group.addActor(spinBox);
        return group;
    }
}
