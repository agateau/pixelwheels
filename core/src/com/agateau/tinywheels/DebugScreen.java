/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels;

import com.agateau.ui.FloatSliderMenuItem;
import com.agateau.ui.IntSliderMenuItem;
import com.agateau.ui.Menu;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.SwitchMenuItem;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.agateau.utils.Introspector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * The debug screen
 */
public class DebugScreen extends TwStageScreen {
    private final TwGame mGame;
    private Menu mMenu;

    // This field is set during setupUi: add* methods use it to bind the controls to the correct
    // introspector
    private Introspector mCurrentIntrospector = null;

    public DebugScreen(TwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new DebugScreen(mGame));
            }
        };
        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/debug.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mCurrentIntrospector = mGame.getGamePlayIntrospector();
        MenuScrollPane pane = builder.getActor("scrollPane");
        mMenu = pane.getMenu();
        addTitle("Race");
        addRange("Viewport width", "viewportWidth", 20, 800, 10);
        addRange("Racer count", "racerCount", 1, 8);
        addRange("Max skidmarks", "maxSkidmarks", 10, 200, 10);
        addRange("Border restitution", "borderRestitution", 1, 50);
        addTitle("Input");
        addCheckBox("Force touch input", "alwaysShowTouchInput");
        addTitle("Speed");
        addRange("Max driving force", "maxDrivingForce", 10, 200, 10);
        addRange("Max speed", "maxSpeed", 10, 400, 10);
        addTitle("Turbo");
        addRange("Strength", "turboStrength", 10, 800, 20);
        addRange("Duration", "turboDuration", 0.1f, 2f, 0.1f);
        addTitle("Wheels");
        addRange("Stickiness", "maxLateralImpulse", 1, 40);
        addRange("Steer: low speed", "lowSpeedMaxSteer", 2, 50, 2);
        addRange("Steer: high speed", "highSpeedMaxSteer", 2, 50, 1);
        addTitle("Vehicle");
        addRange("Density", "vehicleDensity", 1, 50);
        addRange("Restitution", "vehicleRestitution", 1, 50);

        mCurrentIntrospector = mGame.getDebugIntrospector();
        addTitle("Debug");
        addCheckBox("Show debug hud", "showDebugHud");
        addCheckBox("Show debug layer", "showDebugLayer");
        addCheckBox("- Draw velocities", "drawVelocities");
        addCheckBox("- Draw tile corners", "drawTileCorners");
        addCheckBox("Hud debug lines", "showHudDebugLines");

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });
    }

    private void addTitle(String text) {
        mMenu.addTitleLabel(text);
    }

    private void addCheckBox(String text, final String keyName) {
        final Introspector introspector = mCurrentIntrospector;
        //final DefaultLabel defaultLabel = new DefaultLabel(keyName, introspector);

        SwitchMenuItem item = new SwitchMenuItem(mMenu);
        boolean checked = introspector.get(keyName);
        item.setChecked(checked);
        mMenu.addItemWithLabel(text, item);


        /*
        final CheckBox checkBox = new CheckBox(text, mGame.getAssets().ui.skin);
        boolean checked = introspector.get(keyName);
        checkBox.setChecked(checked);

        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean value = checkBox.isChecked();
                introspector.set(keyName, value);
                defaultLabel.update();
            }
        });

        final HorizontalGroup group = new HorizontalGroup();
        group.addActor(checkBox);
        group.addActor(defaultLabel);
        return group;
        */
    }

    private void addRange(String text, final String keyName, int min, int max) {
        addRange(text, keyName, min, max, 1);
    }

    private void addRange(String text, final String keyName, int min, int max, int stepSize) {
        final Introspector introspector = mCurrentIntrospector;

        final MyIntSliderMenuItem item = new MyIntSliderMenuItem(mMenu, keyName, introspector);
        item.setRange(min, max);
        item.setStepSize(stepSize);
        item.setValue(introspector.getInt(keyName));
        item.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = item.getValue();
                introspector.setInt(keyName, value);
                item.updateMainActor();
            }
        });

        mMenu.addItemWithLabel(text, item);
    }

    private void addRange(String text, final String keyName, float min, float max, float stepSize) {
        final Introspector introspector = mCurrentIntrospector;

        final MyFloatSliderMenuItem item = new MyFloatSliderMenuItem(mMenu, keyName, introspector);
        item.setRange(min, max);
        item.setStepSize(stepSize);
        item.setValue(introspector.getFloat(keyName));
        item.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = item.getValue();
                introspector.setFloat(keyName, value);
                item.updateMainActor();
            }
        });

        mMenu.addItemWithLabel(text, item);
    }

    @Override
    public void onBackPressed() {
        mGame.getDebugIntrospector().save();
        mGame.getGamePlayIntrospector().save();
        mGame.popScreen();
    }

    private class MyIntSliderMenuItem extends IntSliderMenuItem {
        private final String mKeyName;
        private final Introspector mIntrospector;

        public MyIntSliderMenuItem(Menu menu, String keyName, Introspector introspector) {
            super(menu);
            mKeyName = keyName;
            mIntrospector = introspector;
        }

        @Override
        protected String formatValue(int value) {
            String text = super.formatValue(value);
            int ref = mIntrospector.getReference(mKeyName);
            int current = mIntrospector.get(mKeyName);

            if (ref != current) {
                text += " (" + super.formatValue(ref) + ")";
            }
            return text;
        }
    }

    private class MyFloatSliderMenuItem extends FloatSliderMenuItem {
        private final String mKeyName;
        private final Introspector mIntrospector;

        public MyFloatSliderMenuItem(Menu menu, String keyName, Introspector introspector) {
            super(menu);
            mKeyName = keyName;
            mIntrospector = introspector;
        }

        @Override
        protected String formatValue(float value) {
            String text = super.formatValue(value);
            float ref = mIntrospector.getReference(mKeyName);
            float current = mIntrospector.get(mKeyName);

            if (ref != current) {
                text += " (" + super.formatValue(ref) + ")";
            }
            return text;
        }
    }
}
