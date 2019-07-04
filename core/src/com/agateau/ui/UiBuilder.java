/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui;

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.PositionRule;
import com.agateau.ui.animscript.AnimScript;
import com.agateau.ui.animscript.AnimScriptLoader;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuScrollPane;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UiBuilder {
    private static final String PREVIOUS_ACTOR_ID = "$prev";

    private final Set<String> mVariables = new HashSet<String>();
    private final AnimScriptLoader mAnimScriptloader = new AnimScriptLoader();
    private final DimensionParser mDimParser = new DimensionParser();

    private Map<String, Actor> mActorForId = new HashMap<String, Actor>();
    private Map<String, ActorFactory> mFactoryForName = new HashMap<String, ActorFactory>();
    private TextureAtlas mAtlas;
    private Skin mSkin;
    private Actor mLastAddedActor;
    private Map<String, TextureAtlas> mAtlasMap = new HashMap<String, TextureAtlas>();

    public interface ActorFactory {
        Actor createActor(UiBuilder uiBuilder, XmlReader.Element element);
    }

    private static final String[] ANCHOR_NAMES = {
        "topLeft",
        "topCenter",
        "topRight",
        "centerLeft",
        "center",
        "centerRight",
        "bottomLeft",
        "bottomCenter",
        "bottomRight"
    };
    private static final Anchor[] ANCHORS = {
        Anchor.TOP_LEFT,
        Anchor.TOP_CENTER,
        Anchor.TOP_RIGHT,
        Anchor.CENTER_LEFT,
        Anchor.CENTER,
        Anchor.CENTER_RIGHT,
        Anchor.BOTTOM_LEFT,
        Anchor.BOTTOM_CENTER,
        Anchor.BOTTOM_RIGHT
    };

    public static class SyntaxException extends Exception {
        SyntaxException(String message) {
            super(message);
        }
    }

    public UiBuilder(TextureAtlas atlas, Skin skin) {
        mAtlas = atlas;
        mSkin = skin;
    }

    public void defineVariable(String name) {
        mVariables.add(name);
    }

    public Actor build(FileHandle handle) {
        return build(handle, null);
    }

    public Actor build(XmlReader.Element parentElement) {
        return build(parentElement, null);
    }

    public Actor build(FileHandle handle, Group parentActor) {
        XmlReader.Element element = FileUtils.parseXml(handle);
        assert(element != null);
        return build(element, parentActor);
    }

    public Actor build(XmlReader.Element parentElement, Group parentActor) {
        mActorForId.clear();
        try {
            return doBuild(parentElement, parentActor);
        } catch (SyntaxException e) {
            NLog.e("Parse error: " + e.getMessage());
            return null;
        }
    }

    public TextureAtlas getAtlas() {
        return mAtlas;
    }

    public Skin getSkin() {
        return mSkin;
    }

    public void addAtlas(String ui, TextureAtlas atlas) {
        mAtlasMap.put(ui, atlas);
    }

    private Actor doBuild(XmlReader.Element parentElement, Group parentActor) throws SyntaxException {
        Actor firstActor = null;
        for (int idx=0, size = parentElement.getChildCount(); idx < size; ++idx) {
            XmlReader.Element element = parentElement.getChild(idx);
            if (element.getName().equals("Action")) {
                continue;
            }
            if (element.getName().equals("Ifdef")) {
                XmlReader.Element elseElement = null;
                if (idx + 1 < size) {
                    elseElement = parentElement.getChild(idx + 1);
                    if (elseElement.getName().equals("Else")) {
                        // It's an else, swallow it
                        ++idx;
                    } else {
                        elseElement = null;
                    }
                }
                if (evaluateIfdef(element)) {
                    doBuild(element, parentActor);
                } else if (elseElement != null) {
                    doBuild(elseElement, parentActor);
                }
                continue;
            }
            Actor actor = createActorForElement(element);
            if (actor == null) {
                throw new SyntaxException("Failed to create actor for element: " + element);
            }
            if (idx == 0) {
                firstActor = actor;
            }
            if (actor instanceof Widget) {
                applyWidgetProperties((Widget)actor, element);
            }
            applyActorProperties(actor, element, parentActor);
            createActorActions(actor, element);
            String id = element.getAttribute("id", null);
            if (id != null) {
                if (mActorForId.containsKey(id)) {
                    throw new SyntaxException("Duplicate ids: " + id);
                }
                mActorForId.put(id, actor);
            }
            if (actor instanceof Group && !(actor instanceof ScrollPane)) {
                doBuild(element, (Group)actor);
            }
            mLastAddedActor = actor;
        }
        return firstActor;
    }

    public <T extends Actor> T getActor(String id) {
        Actor actor;
        if (id.equals(PREVIOUS_ACTOR_ID)) {
            actor = mLastAddedActor;
        } else {
            actor = mActorForId.get(id);
        }
        if (actor == null) {
            throw new RuntimeException("No actor with id '" + id + "'");
        }
        @SuppressWarnings("unchecked")
        T obj = (T)actor;
        return obj;
    }

    private Actor createActorForElement(XmlReader.Element element) throws SyntaxException {
        String name = element.getName();
        if (name.equals("Image")) {
            return createImage(element);
        } else if (name.equals("ImageButton")) {
            return createImageButton(element);
        } else if (name.equals("TextButton")) {
            return createTextButton(element);
        } else if (name.equals("Group")) {
            return createGroup(element);
        } else if (name.equals("AnchorGroup")) {
            return createAnchorGroup(element);
        } else if (name.equals("Label")) {
            return createLabel(element);
        } else if (name.equals("ScrollPane")) {
            return createScrollPane(element);
        } else if (name.equals("VerticalGroup")) {
            return createVerticalGroup(element);
        } else if (name.equals("HorizontalGroup")) {
            return createHorizontalGroup(element);
        } else if (name.equals("CheckBox")) {
            return createCheckBox(element);
        } else if (name.equals("Menu")) {
            return createMenu(element);
        } else if (name.equals("MenuScrollPane")) {
            return createMenuScrollPane(element);
        } else if (name.equals("Table")) {
            return createTable(element);
        }
        ActorFactory factory = mFactoryForName.get(name);
        if (factory != null) {
            return factory.createActor(this, element);
        }
        throw new SyntaxException("Unknown UI element type: " + name);
    }

    private Image createImage(XmlReader.Element element) {
        Image image = new Image();
        TextureAtlas atlas = getAtlasForElement(element);
        String attr = element.getAttribute("name", "");
        if (!attr.isEmpty()) {
            if (attr.endsWith(".9")) {
                initImageFromNinePatchName(image, atlas, attr);
            } else {
                boolean tiled = element.getBooleanAttribute("tiled", false);
                initImageFromRegionName(image, atlas, attr, tiled);
            }
        }
        return image;
    }

    private boolean evaluateIfdef(XmlReader.Element element) {
        String condition = element.getAttribute("var").trim();
        return mVariables.contains(condition);
    }

    private TextureAtlas getAtlasForElement(XmlReader.Element element) {
        String name = element.getAttribute("atlas", "");
        if (name.isEmpty()) {
            return mAtlas;
        }
        return mAtlasMap.get(name);
    }

    private void initImageFromNinePatchName(Image image, TextureAtlas atlas, String name) {
        NinePatch patch = atlas.createPatch(name.substring(0, name.length() - 2));
        image.setDrawable(new NinePatchDrawable(patch));
    }

    private void initImageFromRegionName(Image image, TextureAtlas atlas, String name, boolean tiled) {
        TextureRegion region = atlas.findRegion(name);
        Assert.check(region != null, "No region named " + name);
        Drawable drawable;
        if (tiled) {
            drawable = new TiledDrawable(region);
        } else {
            drawable = new TextureRegionDrawable(region);
        }
        image.setDrawable(drawable);
        if (image.getWidth() == 0) {
            image.setWidth(region.getRegionWidth());
        }
        if (image.getHeight() == 0) {
            image.setHeight(region.getRegionHeight());
        }
    }

    private ImageButton createImageButton(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(mSkin.get(styleName, ImageButton.ImageButtonStyle.class));
        String imageName = element.getAttribute("imageName", "");
        if (!imageName.isEmpty()) {
            style.imageUp = mSkin.getDrawable(imageName);
        }
        ImageButton button = new ImageButton(style);
        String imageColor = element.getAttribute("imageColor", "");
        if (!imageColor.isEmpty()) {
            Color color = Color.valueOf(imageColor);
            button.getImage().setColor(color);
        }
        return button;
    }

    private TextButton createTextButton(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        String text = processText(element.getText());
        return new TextButton(text, mSkin, styleName);
    }

    @SuppressWarnings("UnusedParameters")
    private Group createGroup(XmlReader.Element element) {
        return new Group();
    }

    private AnchorGroup createAnchorGroup(XmlReader.Element element) {
        mDimParser.gridSize = mDimParser.parse(element.getAttribute("gridSize", "1"));
        AnchorGroup group = new AnchorGroup();
        group.setGridSize(mDimParser.gridSize);
        return group;
    }

    private Label createLabel(XmlReader.Element element) throws SyntaxException {
        String styleName = element.getAttribute("style", "default");
        String text = processText(element.getText());
        Label label = new Label(text, mSkin, styleName);
        int align = parseAlign(element);
        if (align != -1) {
            label.setAlignment(align);
        }
        return label;
    }

    private ScrollPane createScrollPane(XmlReader.Element element) throws SyntaxException {
        String styleName = element.getAttribute("style", "");
        ScrollPane pane;
        if (styleName.isEmpty()) {
            pane = new ScrollPane(null);
        } else {
            pane = new ScrollPane(null, mSkin, styleName);
        }
        Actor child = doBuild(element, null);
        if (child != null) {
            pane.setActor(child);
        }
        return pane;
    }

    private VerticalGroup createVerticalGroup(XmlReader.Element element) throws SyntaxException {
        VerticalGroup group = new VerticalGroup();
        group.space(element.getFloatAttribute("spacing", 0));
        int align = parseAlign(element);
        if (align != -1) {
            group.align(align);
        }
        return group;
    }

    private HorizontalGroup createHorizontalGroup(XmlReader.Element element) {
        HorizontalGroup group = new HorizontalGroup();
        group.space(element.getFloatAttribute("spacing", 0));
        return group;
    }

    @SuppressWarnings("UnusedParameters")
    private Table createTable(XmlReader.Element element) {
        return new Table(mSkin);
    }

    private CheckBox createCheckBox(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        String text = element.getText();
        return new CheckBox(text, mSkin, styleName);
    }

    private Menu createMenu(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        Menu menu = new Menu(mSkin, styleName);
        float width = element.getIntAttribute("labelColumnWidth", 0);
        if (width > 0) {
            menu.setLabelColumnWidth(width);
        }
        return menu;
    }

    private MenuScrollPane createMenuScrollPane(XmlReader.Element element) {
        Menu menu = createMenu(element);
        return new MenuScrollPane(menu);
    }

    private void applyWidgetProperties(Widget widget, XmlReader.Element element) {
        widget.setFillParent(element.getBooleanAttribute("fillParent", false));
    }

    private void applyActorProperties(Actor actor, XmlReader.Element element, Group parentActor) throws SyntaxException {
        AnchorGroup anchorGroup = null;
        if (parentActor != null) {
            parentActor.addActor(actor);
            if (parentActor instanceof AnchorGroup) {
                anchorGroup = (AnchorGroup)parentActor;
            }
        }
        String attr = element.getAttribute("x", "");
        if (!attr.isEmpty()) {
            actor.setX(mDimParser.parse(attr));
        }
        attr = element.getAttribute("y", "");
        if (!attr.isEmpty()) {
            actor.setY(mDimParser.parse(attr));
        }
        attr = element.getAttribute("width", "");
        if (!attr.isEmpty()) {
            actor.setWidth(mDimParser.parse(attr));
        }
        attr = element.getAttribute("height", "");
        if (!attr.isEmpty()) {
            actor.setHeight(mDimParser.parse(attr));
        }
        attr = element.getAttribute("originX", "");
        if (!attr.isEmpty()) {
            actor.setOriginX(mDimParser.parse(attr));
        }
        attr = element.getAttribute("originY", "");
        if (!attr.isEmpty()) {
            actor.setOriginY(mDimParser.parse(attr));
        }
        attr = element.getAttribute("visible", "");
        if (!attr.isEmpty()) {
            actor.setVisible(Boolean.parseBoolean(attr));
        }
        attr = element.getAttribute("color", "");
        if (!attr.isEmpty()) {
            actor.setColor(Color.valueOf(attr));
        }
        attr = element.getAttribute("debug", "");
        if (!attr.isEmpty()) {
            if (actor instanceof Group) {
                Group group = (Group)actor;
                attr = attr.toLowerCase();
                if (attr.equals("true")) {
                    group.debug();
                } else if (attr.equals("all")) {
                    group.debugAll();
                }
            } else {
                actor.setDebug(Boolean.parseBoolean(attr));
            }
        }
        for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
            String anchorName = ANCHOR_NAMES[idx];
            attr = element.getAttribute(anchorName, "");
            if (!attr.isEmpty()) {
                if (anchorGroup == null) {
                    throw new SyntaxException("Parent of " + actor + " is not an anchor group");
                }
                PositionRule rule = parseRule(attr);
                rule.target = actor;
                rule.targetAnchor = ANCHORS[idx];
                anchorGroup.addRule(rule);
            }
        }
    }

    /**
     * Parse a string of the form "$actorId $anchorName [$xOffset $yOffset]"
     * @param txt the string to parse
     * @return a PositionRule
     */
    private PositionRule parseRule(String txt) throws SyntaxException {
        PositionRule rule = new PositionRule();
        String[] tokens = txt.split(" +");
        if (tokens.length != 1 && tokens.length != 3) {
            throw new SyntaxException("Invalid rule syntax: " + txt);
        }
        String[] tokens2 = tokens[0].split("\\.");
        if (tokens2.length != 2) {
            throw new SyntaxException("reference should be of the form <id>.<anchor>: " + txt);
        }
        rule.reference = getActor(tokens2[0]);
        for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
            if (tokens2[1].equals(ANCHOR_NAMES[idx])) {
                rule.referenceAnchor = ANCHORS[idx];
                break;
            }
        }
        if (rule.referenceAnchor == null) {
            throw new SyntaxException("Invalid anchor name: '" + tokens[1] + "'");
        }
        if (tokens.length == 3) {
            rule.hSpace = mDimParser.parse(tokens[1], DimensionParser.Unit.GRID);
            rule.vSpace = mDimParser.parse(tokens[2], DimensionParser.Unit.GRID);
        }
        return rule;
    }

    public void registerActorFactory(String name, ActorFactory factory) {
        mFactoryForName.put(name, factory);
    }

    private static String processText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\n", "\n");
    }

    private void createActorActions(Actor actor, XmlReader.Element element) throws SyntaxException {
        for (XmlReader.Element child: element.getChildrenByName("Action")) {
            String definition = child.getText();
            float duration = child.getFloatAttribute("duration", -1);
            if (duration < 0) {
                throw new SyntaxException("Missing 'duration' attribute for action '" + definition + "'");
            }
            AnimScript script = mAnimScriptloader.load(definition);
            actor.addAction(script.createAction(mDimParser.gridSize, mDimParser.gridSize, duration));
        }
    }

    private static int parseAlign(XmlReader.Element element) throws SyntaxException {
        String alignText = element.getAttribute("align", "");
        if (alignText.isEmpty()) {
            return -1;
        }
        if (alignText.equals("center")) {
            return Align.center;
        } else if (alignText.equals("centerLeft")) {
            return Align.left;
        } else if (alignText.equals("centerRight")) {
            return Align.right;
        } else if (alignText.equals("topLeft")) {
            return Align.topLeft;
        } else if (alignText.equals("topCenter")) {
            return Align.top;
        } else if (alignText.equals("topRight")) {
            return Align.topRight;
        } else if (alignText.equals("bottomLeft")) {
            return Align.bottomLeft;
        } else if (alignText.equals("bottomCenter")) {
            return Align.bottom;
        } else if (alignText.equals("bottomRight")) {
            return Align.bottomRight;
        } else {
            throw new SyntaxException("Unknown value of 'align': " + alignText);
        }
    }
}
